/* (c) https://github.com/MontiCore/monticore */
import { PipelineStatsPerGroupTOP } from '@src/gui/pipelines/PipelineStatsPerGroupTOP';
import { GemBarChartData, GemBarChartEntry } from '@umlp/charts';

export class PipelineStatsPerGroup extends PipelineStatsPerGroupTOP {

  private disableButton = false;
  private maxValue: number = 0;
  private stacked: boolean = true;

  private maxDate: string;
  private minDate: string;

  private dateOptions: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  };

  projectDataCheck = { value: null, hash: null, calculating: false };

  public getBarChartForPipelineRunsData(): GemBarChartData {
    if (!this.getG()?.getStatistics()?.length) {
      return null;
    }
    //console.debug("Group is "+ this.getG().getName())
    let labels: string[] = []
    let entries: Map<string, number[]> = new Map()
    entries.set("Successful", [])
    entries.set("Unsuccessful", [])
    this.getG().getStatistics().filter(stat => {
      const isGreaterMin = ((new Date(this.minDate)) <= stat.getTime())
      const isSmallerMax = ((new Date(this.maxDate)) >= stat.getTime())
      if (this.minDate && this.maxDate) {
        return isGreaterMin && isSmallerMax
      }
      if (this.minDate) {
        return isGreaterMin
      }
      if (this.maxDate) {
        return isSmallerMax
      }
      return true
    }).forEach((stat) => {
      //console.debug("Statistic: ", stat)
      const label = stat.getTime().toLocaleString("de-DE", this.dateOptions)
      let index = labels.indexOf(label)
      if (index === -1) {
        labels.push(label)
      }
    })
    labels.sort((s1, s2) => {
      let str1Arr = s1.split(".");
      let reverse1Arr = str1Arr.reverse();
      let reverse1Str = reverse1Arr.join('');
      let str2Arr = s2.split(".");
      let reverse2Arr = str2Arr.reverse();
      let reverse2Str = reverse2Arr.join('');
      return reverse1Str.localeCompare(reverse2Str)
    })
    labels.reverse()
    if (!this.maxDate) {
      this.maxDate = labels[0].split(".").reverse().join("-");
    }
    if (!this.minDate) {
      this.minDate = labels[labels.length - 1].split(".").reverse().join("-");
    }
    labels.forEach(label => {
      entries.get("Successful").push(0)
      entries.get("Unsuccessful").push(0)
    })
    this.getG().getStatistics().forEach((stat) => {
      const label = stat.getTime().toLocaleString("de-DE", this.dateOptions)
      let index = labels.indexOf(label)
      if (!(index === -1)) {
        entries.get("Successful")[index] += stat.getNumberOfSuccessfulRuns()
        entries.get("Unsuccessful")[index] += stat.getNumberOfUnsuccessfulRuns()
      }
    })
    let barChartEntries: GemBarChartEntry[] = []
    this.maxValue = 0
    for (let i = 0; i < entries.get("Successful").length; i++) {
      if (this.stacked) {
        if (this.maxValue < entries.get("Successful")[i] + entries.get("Unsuccessful")[i] + 5) {
          this.maxValue = entries.get("Successful")[i] + entries.get("Unsuccessful")[i] + 5
        }
      } else {
        if (this.maxValue < entries.get("Successful")[i] + 5) {
          this.maxValue = entries.get("Successful")[i] + 5
        }
        if (this.maxValue < +entries.get("Unsuccessful")[i]) {
          this.maxValue = entries.get("Unsuccessful")[i] + 5
        }
      }
    }
    entries.forEach((data, label) => {
      barChartEntries.push({
        label: label,
        data: data,
        backgroundColor: (label === "Successful") ? '#00ff00' : '#D21404'
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
    //console.debug(data)
    return this.projectDataCheck.value
  }

  public getGemButton1Disabled(): boolean {
    return this.disableButton;
  }

  public getBarChartForPipelineRunsMaxValue(): number {
    return this.maxValue;
  }

  public getBarChartForPipelineRunsStacked(): boolean {
    return this.stacked;
  }

  public getMaxDateEntry(): string {
    return this.maxDate;
  }

  public getMinDateEntry(): string {
    return this.minDate;
  }

  public setMinDateEntry(value: string): void {
    console.debug("Setting Min Date:", value)
    this.minDate = value
  }

  public setMaxDateEntry(value: string): void {
    console.debug("Setting Max Date:", value)
    this.maxDate = value
  }

  public toggleStackedInfo(event: any): void {
    this.stacked = !this.stacked
  }

  public getStackedToggleLabel(): string {
    if (this.stacked) {
      return "Balken nebeneinander anzeigen";
    }
    return "Balken übereinander anzeigen"
  }

}
