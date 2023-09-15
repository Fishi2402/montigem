/* (c) https://github.com/MontiCore/monticore */

import { Artifact } from '@src/sehub/Artifact';
import { SehubManager } from '@src/sehub/SehubManager';
import { DateTimeToPassedByTimeTOP } from '@src/gui/artifacts/DateTimeToPassedByTimeTOP';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Directive()
export  class DateTimeToPassedByTime extends DateTimeToPassedByTimeTOP {

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


