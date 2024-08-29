/* (c) https://github.com/MontiCore/monticore */

import { PipelineStatistic } from '@src/dtcockpit/PipelineStatistic';
import { PipelineStatsComponent,config } from '@src/gui/pipelines/PipelineStatsComponent';
import { GemBarChartData, GemBarChartEntry } from '@umlp/charts';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class PipelineStats extends PipelineStatsComponent {

  private disableButton = false;
  private maxValue: number = 0;
  private stacked: boolean = true;

  private dateOptions: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  };

  projectDataCheck = { value: null, hash: null, calculating: false };

  public init() {
    this.projectDataCheck = { value: null, hash: null, calculating: false };
    super.init();
  }

  public getBarChartForPipelineRunsData(): GemBarChartData {
    if (!this.getP()) {
      return null;
    }
    let labels: string[] = []
    let entries: Map<string, number[]> = new Map()
    entries.set("Successful", [])
    entries.set("Unsuccessful", [])
    this.getP().getPStatisticsList().forEach((stat: PipelineStatistic) => {
      const label = stat.getTime().toLocaleString("de-DE", this.dateOptions)
      let index = labels.indexOf(label)
      if (index === -1) {
        labels.push(label)
        index = labels.indexOf(label)
        entries.get("Successful").push(0)
        entries.get("Unsuccessful").push(0)
      }
      if (stat.isSuccessful()) {
        entries.get("Successful")[index] += 1
      } else {
        entries.get("Unsuccessful")[index] += 1
      }
    }
    )
    for (let i = 0; i < entries.get("Successful").length; i++) {
      if (this.stacked) {
        if (this.maxValue < entries.get("Successful")[i] + entries.get("Unsuccessful")[i] + 5) {
          this.maxValue = entries.get("Successful")[i] + entries.get("Unsuccessful")[i] + 5
        }
      } else {
        if (this.maxValue < entries.get("Successful")[i] + 5) {
          this.maxValue = entries.get("Successful")[i] + 5
        }
        if (this.maxValue < entries.get("Unsuccessful")[i] + 5) {
          this.maxValue = entries.get("Unsuccessful")[i] + 5
        }
      }
    }
    let barChartEntries: GemBarChartEntry[] = []
    entries.forEach((data, label) => {
      barChartEntries.push({
        label: label,
        data: data,
        backgroundColor: (label==="Successful")? '#57ab27' : '#cc071e'
      })
    })
    const data: GemBarChartData = {
      labels: labels,
      entries: barChartEntries
    }
    const newHash = JSON.stringify(data);
    if (this.projectDataCheck.hash !== newHash) {
      this.projectDataCheck.hash = newHash;
      this.projectDataCheck.value = data;
    }
    return this.projectDataCheck.value
  }

  public getGemButton1Disabled(): boolean {
    return this.disableButton;
  }

  // TODO alles erst mal provisorisch um anzuzeigen, dass sich im Backend was getan hat
  public getGemButton1Label(): string {
    if (this.disableButton) {
      return "PipelineInformation already loaded into Backend"
    }
    return "Load PipelineInformation into Backend";

  }

  // TODO how do I add a custom command into a UMLP App
  public loadPipelineInfoIntoBackend(p: any): (arg: any) => void {

    return () => { }
    /*    return event => {
      DtcockpitManager.createPipelineStatisticGem(this.getP().getGemId()).load().finally(
        () => {this.disableButton = true}
    )};*/
  }

  public getBarChartForPipelineRunsMaxValue(): number {
    return this.maxValue;
  }

  public getBarChartForPipelineRunsStacked(): boolean {
    return this.stacked;
  }

}
