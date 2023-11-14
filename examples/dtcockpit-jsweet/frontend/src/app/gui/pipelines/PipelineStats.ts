/* (c) https://github.com/MontiCore/monticore */

import { GemBarChartData } from '@umlp/charts';
import { GemBarChartEntry } from '@umlp/charts';
import { DtcockpitManager } from '@src/dtcockpit/DtcockpitManager';
import { Project } from '@src/dtcockpit/Project';
import { PipelineStatistic } from '@src/dtcockpit/PipelineStatistic';
import { PipelineStatsTOP } from '@src/gui/pipelines/PipelineStatsTOP';




export class PipelineStats extends PipelineStatsTOP {

  private disableButton = false;
  private maxValue : number = 0;
  private stacked : boolean = true;

  projectDataCheck = { value: null, hash: null, calculating: false };
  private project : Project;

  public  getGemCard0Title (): string {
    return "Pipeline dashboard for "+this.getP().getName();
  }

  public  getBarChartForPipelineRunsData (): GemBarChartData {
    if(!this.getP()) {
      return null;
    }
    console.debug("Project is "+ this.getP().getName())
    let labels : string[] = []
    let entries : Map<string,number[]> = new Map()
    entries.set("Successful", [])
    entries.set("Unsuccessful", [])
    this.getP().getPStatistics().forEach((stat : PipelineStatistic) =>
        {
          const label = stat.getTime().toLocaleString("de-DE", {
            year: "2-digit",
            month: "2-digit",
            day: "2-digit",
          })
          let index = labels.indexOf(label)
          if(index === -1) {
            labels.push(label)
            index = labels.indexOf(label)
            entries.get("Successful").push(0)
            entries.get("Unsuccessful").push(0)
          }
          if(stat.isSuccessful()) {
            entries.get("Successful")[index] +=1
          } else {
            entries.get("Unsuccessful")[index] +=1
          }
        }
    )
    let barChartEntries : GemBarChartEntry[] = []
    entries.forEach((data, label) => {
      if(data[0] >= this.maxValue) {
        this.maxValue = data[0] +5
      } if(data[1] >= this.maxValue) {
        this.maxValue = data[1] +5
      }
      barChartEntries.push({
        label:label,
        data:data
      })
    })
    if(this.stacked) {
      let counterSuccessful = 0;
      let counterUnsuccessful = 0;
      entries.forEach((data, label) => {
        counterSuccessful += data[0];
        counterUnsuccessful += data[1];
      })
      if(counterSuccessful >= this.maxValue) {
        this.maxValue = counterSuccessful +5
      } if(counterUnsuccessful >= this.maxValue) {
        this.maxValue = counterUnsuccessful +5
      }
    }
    const data : GemBarChartData = {
      labels: labels,
      entries: barChartEntries
    }
    const newHash = JSON.stringify(data);
    if(this.projectDataCheck.hash !== newHash) {
      this.projectDataCheck.hash = newHash;
      this.projectDataCheck.value = data;
    }
    console.debug(data)
    return this.projectDataCheck.value
  }

  public getGemButton1Disabled() :boolean {
    return this.disableButton;
  }

  // TODO alles erst mal provisorisch um anzuzeigen, dass sich im Backend was getan hat
  public  getGemButton1Label (): string {
    if(this.disableButton) {
      return "PipelineInformation already loaded into Backend"
    }
    return "Load PipelineInformation into Backend";

  }

  // TODO how do I add a custom command into a UMLP App
  public  loadPipelineInfoIntoBackend (p: any): (arg: any) => void {

    return () => {}
    /*    return event => {
      DtcockpitManager.createPipelineStatisticGem(this.getP().getGemId()).load().finally(
        () => {this.disableButton = true}
    )};*/
  }

  public  getBarChartForPipelineRunsMaxValue (): number {
    return this.maxValue;
  }

  public  getBarChartForPipelineRunsStacked (): boolean {
    return this.stacked;
  }

}
