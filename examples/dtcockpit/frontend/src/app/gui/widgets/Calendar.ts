/* (c) https://github.com/MontiCore/monticore */

import { Meeting } from '@src/dtcockpit/Meeting';
import { GemList } from '@umlp/common';

import { Component, Input } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import {config} from "@src/gui/widgets/CalendarComponent";
interface CalendarBlock {
  x: number;
  y: number;
  w: number;
  h: number;
  title: string;
  loc: string;
}

@Component({
  standalone:true,
  imports:[...config.imports, HttpClientModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class Calendar {

  protected _meetings: GemList<Meeting>

  @Input()
  public set meetings(value: GemList<Meeting>) {
    this._meetings = value;
  }

  public get meetings(): GemList<Meeting> {
    return this._meetings;
  }

  // in %
  stdEmptyBlockHeight: number;
  stdEmptyBlockWidth: number;
  stdNumberBlockWidth: number = 3;
  stdDateBlockHeight: number = 10;

  today = new Date();
  days: Date[] = [];
  start = 6; // 6:00
  end = 21; // 22:00
  hours: number[] = [];

  calendar: CalendarBlock[] = [];

  public ngOnInit(): void {
    this.days = this.getAllDatesOfWeek(this.today);
    for (let i = this.start; i < this.end; ++i) {
      this.hours.push(i);
    }

    this.stdEmptyBlockWidth = (100 - this.stdNumberBlockWidth) / 7;
    this.stdEmptyBlockHeight = (100 - this.stdDateBlockHeight) / (this.end - this.start);
  }

  getCalendar(): CalendarBlock[] {
    if (this.meetings == null) {
      return null;
    }

    const ms: Meeting[] = this.meetings.toArray();
    let cal: CalendarBlock[] = [];
    for (const m of ms) {
      for (let i = m.getStart().getDate(); i <= m.getEnd().getDate(); ++i) {
        if (this.days[0].getDate() > i
          || this.days[this.days.length - 1].getDate() < i
        ) {
          continue;
        }

        const start = m.getStart().getHours() + m.getStart().getMinutes() / 60;
        const end = m.getEnd().getHours() + m.getEnd().getMinutes() / 60;
        const x = (m.getStart().getDate() - this.days[0].getDate()) * this.stdEmptyBlockWidth + this.stdNumberBlockWidth;
        const y = Math.max(start - this.start, 0) * this.stdEmptyBlockHeight + this.stdDateBlockHeight;
        const width = this.stdEmptyBlockWidth;
        const height = Math.min(end - start, this.end - start) * this.stdEmptyBlockHeight - y;
        cal.push({
          x: x,
          y: y,
          w: width,
          h: height,
          title: m.getTitle(),
          loc: m.getLocation()
        });
      }
    }
    if (JSON.stringify(this.calendar) !== JSON.stringify(cal)) {
      this.calendar = cal;
    }
    return this.calendar;
  }

  protected getMeetingAt(day: Date, hour: number): Meeting | null {
    const ms = this.meetings.toArray();
    for (const m of ms) {
      if (m.getStart().getFullYear() === day.getFullYear()
        && m.getStart().getMonth() === day.getMonth()
        && m.getStart().getDate() === day.getDate()
        && m.getStart().getHours() === hour
      ) {
        return m;
      }
    }
    return null;
  }

  protected getAllDatesOfWeek(input: Date): Date[] {
    const date = new Date(input);
    const dayOfWeek = date.getDay();
    const daysToSubtract = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    date.setDate(date.getDate() - daysToSubtract);
    const allDatesOfWeek = [];

    for (let i = 0; i < 7; i++) {
      let currentDate = new Date(date);
      currentDate.setDate(date.getDate() + i);
      currentDate.setHours(0);
      currentDate.setMinutes(0);
      currentDate.setSeconds(0);
      currentDate.setMilliseconds(0);
      allDatesOfWeek.push(currentDate);
    }

    return allDatesOfWeek;
  }

}


