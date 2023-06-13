import * as moment from "moment";
import { Moment, MomentFormatSpecification } from "moment";

export enum DateTimeFormat {
  ISO = "YYYY-MM-DDTHH:mm:ss.SSSZ",
  MONTIGEM_DATE = "DD.MM.YYYY",
  MONTIGEM_TIME = "HH:mm",
  MONTIGEM_LONG_TIME = "HHH:mm",
  MONTIGEM_DATE_TIME = "DD.MM.YYYY HH:mm",
  MONTIGEM_DATE_WITHOUT_YEAR = "DD.MM",
  MONTIGEM_DATE_ONLY_DAY = "DD",
  MONTIGEM_MIDNIGHT = "00:00",
  MONTIGEM = "YYYYMMDD",
  MACOCO = "YYYYMMDD",
}

export class DateTimeRegex {
  // When checking the format prefer using moment(dateString, moment.ISO_8601).isValid()
  public static readonly ISO = new RegExp(
    "" +
    /(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+([+-][0-2]\d:[0-5]\d|Z))/.source +
    /|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d([+-][0-2]\d:[0-5]\d|Z))/.source +
    /|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d([+-][0-2]\d:[0-5]\d|Z))/.source
  );
  public static readonly MONTIGEM_DATE_COMPLETE = new RegExp(
    "" +
    /^[0-9]{2}\.[0-9]{2}\.[0-9]{4}$/.source
  )
  public static readonly MONTIGEM_DATE = new RegExp(
    "" +
    /^[0-9][0-9]?\.[0-9][0-9]?\.[0-9]{4}$/
  );
  public static readonly MONTIGEM_DATE_SHORT = new RegExp(
    "" +
    /^(0[1-9]|[1-9]|1\d|2\d|3[01])[\.,]/.source +
    /(0[1-9]|[1-9]|1[012])[\.,]/.source +
    /(\d{4}|\d{1}|\d{2})$/.source
  );
  public static readonly MONTIGEM_TIME = new RegExp(
    "" +
    /^([0-1][0-9]|2[0-3]):[0-5][0-9]$/.source
  )
}

export function dateAsString(
  date: Date | null,
  format: DateTimeFormat = DateTimeFormat.MONTIGEM_DATE
): string {
  if (!!date && date + "" !== "Invalid Date") {
    if (format === DateTimeFormat.ISO) {
      // Interpret date as UTC
      // return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
      return moment.utc(date).utcOffset(0, true).toISOString();
    } else {
      return moment.utc(date).format(format);
    }
  }

  return "";
}

export function stringAsDate(date: string | Date | null): Date | null {
  if (date instanceof Date) {
    return date;
  }

  let dateObj: Date | null = null;

  if (DateTimeRegex.MONTIGEM_DATE.test(date)) {
    dateObj = moment(date, DateTimeFormat.MONTIGEM_DATE).isValid()
      ? moment
        .utc(date, DateTimeFormat.MONTIGEM_DATE)
        .utcOffset(0, true)
        .toDate()
      : null;
  }

  if (moment(date, moment.ISO_8601).isValid()) {
    dateObj = moment.utc(date).utcOffset(0, true).toDate();
  }

  if (dateObj + "" === "Invalid Date") {
    console.error(`MAF0x0096: transform - invalid date >>${date}<<`);
  }

  return dateObj;
}

export function timeAsString(
  time: Date | null,
  format: DateTimeFormat = DateTimeFormat.MONTIGEM_TIME,
  defaultDate?: Date
): string {
  if (time + "" !== "Invalid Date") {
    if (format === DateTimeFormat.ISO) {
      if (defaultDate == null) {
        return time ? moment.utc(time).utcOffset(0, true).toISOString() : "";
      } else if (time != null) {
        // we need to take UTC time to get the correct year, month and date
        const defaultMoment = moment.utc(defaultDate).utcOffset(0, true);
        const date = moment
          .utc(time)
          .utcOffset(0, true)
          .year(defaultMoment.year())
          .month(defaultMoment.month())
          .date(defaultMoment.date());
        return date.toISOString();
      } else {
        return "";
      }
    } else {
      // return time ? moment.utc(time).format(format) : '';
      return time ? moment.utc(time).utcOffset(0, true).format(format) : "";
    }
  }
  return "";
}

export function utcTolocal(
  date: string,
  format: MomentFormatSpecification
): Moment {
  return moment(date, format).utc(false).local();
}

export function toLocalString(
  date: string,
  format: MomentFormatSpecification
): string {
  return moment(date, format).toDate().toString();
}

export function stringAsTime(date: string | Date | null): Date | null {
  if (date instanceof Date) {
    return date;
  }

  let dateObj: Date | null = null;

  if (DateTimeRegex.MONTIGEM_TIME.test(date)) {
    // const time = moment.utc(date, 'HH:mm');
    const time = moment.utc(date, DateTimeFormat.MONTIGEM_TIME);
    dateObj = time.isValid() ? time.utcOffset(0, true).toDate() : null;
  }

  if (moment(date, moment.ISO_8601).isValid()) {
    const time = moment.utc(date);
    dateObj = time.isValid() ? time.utcOffset(0, true).toDate() : null;
  }

  if (dateObj + "" === "Invalid Date") {
    this.logger.error(`MAF0x0096: transform - invalid time >>${date}<<`);
  }

  return dateObj;
}

export function completeDate(value: string): string {
  if (value && typeof value === "string") {
    value = value.trim();
    if (DateTimeRegex.MONTIGEM_DATE_SHORT.test(value)) {
      const autoCompletedDate = value
        .replace(/,/g, ".")
        .split(".")
        .map((partialDate: string, index: number) => {
          if (index < 2) {
            if (partialDate.length === 1) {
              return `0${partialDate}`;
            }
          } else if (partialDate.length === 2) {
            return `20${partialDate}`;
          } else if (partialDate.length === 1) {
            return `200${partialDate}`;
          }
          return partialDate;
        })
        .join(".");

      if (!!stringAsDate(autoCompletedDate)) {
        return autoCompletedDate;
      }
    }
  }

  return value;
}

export function compareISODates(a: string, b: string): number {
  const aDate = stringAsDate(a).setHours(0, 0, 0, 0);
  const bDate = stringAsDate(b).setHours(0, 0, 0, 0);
  if (aDate < bDate) {
    return -1;
  } else if (aDate > bDate) {
    return 1;
  }
  return 0;
}

/**
 * @param date ISO string representing the UTC date
 *
 * @returns day of week, where 1 is Monday, 7 is Sunday, null if the date is incorrect
 */
export function dayOfWeek(date: string): number | null {
  if (isValidISODate(date)) {
    return moment.utc(date).utcOffset(0, true).isoWeekday();
  }
}

/**
 * @param date ISO string representing the UTC date
 *
 * @returns week number, null if the date is incorrect
 */
export function calendarWeek(date: string): number {
  if (isValidISODate(date)) {
    return moment.utc(date).utcOffset(0, true).isoWeek();
  }
}

/**
 * @param date ISO string representing the UTC date
 *
 * @returns true if the date is valid ISO date
 */
export function isValidISODate(date: string): boolean {
  return moment(date, moment.ISO_8601).isValid();
}
