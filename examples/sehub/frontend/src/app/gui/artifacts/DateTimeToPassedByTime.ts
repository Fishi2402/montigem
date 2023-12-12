/* (c) https://github.com/MontiCore/monticore */

import {DateTimeToPassedByTimeComponent} from '@src/gui/artifacts/DateTimeToPassedByTimeComponent';

import {Component,} from '@angular/core';
import {config} from "@src/gui/artifacts/DateTimeToPassedByTimeStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'date-time-to-passed-by-time',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/artifacts/DateTimeToPassedByTime.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class DateTimeToPassedByTime extends DateTimeToPassedByTimeComponent {

  oneYearInMilliSes : number    = 31557600000;
  oneMonthInMilliSecs : number  =  2629800000;
  oneWeekInMilliSecs : number   =   604800016;
  oneDayInMilliSecs : number    =    86400000;
  oneHourInMilliSecs : number   =     3600000;
  oneMinuteInMilliSecs : number =       60000;

  public  getGemText0Value (): string {
    const passedTime = Date.now().valueOf() - new Date(this.artifact.getModified()).valueOf()

    const years = Math.floor(passedTime/this.oneYearInMilliSes)
    if(years>0) {
      return years + " years ago"
    }
    const months = Math.floor(passedTime/this.oneMonthInMilliSecs)
    if(months>0) {
      return months + " months ago"
    }
    const weeks = Math.floor(passedTime/this.oneWeekInMilliSecs)
    if(weeks>0) {
      return weeks + " weeks ago"
    }
    const days = Math.floor(passedTime/this.oneDayInMilliSecs)
    if(days>0) {
      return days + " days ago"
    }
    const hours = Math.floor(passedTime/this.oneHourInMilliSecs)
    if(hours>0) {
      return hours + " hours ago"
    }
    const minutes = Math.floor(passedTime/this.oneMinuteInMilliSecs)
    if(minutes>0) {
      return minutes + " minutes ago"
    }
    return "just now"
  }


}


