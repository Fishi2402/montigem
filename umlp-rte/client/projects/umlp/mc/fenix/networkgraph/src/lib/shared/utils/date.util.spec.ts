import { DateUtil } from "./date.util";

describe("DateUtils", () => {
  const DEBUG: boolean = false;

  const VALID_DATES: (string | Date)[] = [
    new Date(),
    "20/08/2023",
    "08/20/2023",
    "2023/08/20",
    "2023-08-20",
    "20-08-2023",
    "08-20-2023",
    "20230820",
    "20.08.2023",
    "2023.08.20",
    "20 08 2023",
    "08 20 2023",
    "2000-02-29",
    "1999-12-31",
    "1999 12 31",
    "01/01/1900",
    "2023-08-25",
    "1900-01-01",
    "2023-08-25T13:45:30",
    "2023-08-25T13:45:30Z",
    "2023-08-25T13:45:30.123Z",
    "2023-08-25T13:45:30+01:00",
    "2023-08-25T13:45:30-05:00",
  ];

  const INVALID_DATES: any[] = [
    "08-2023",
    "Aug 32, 2023",
    "2023.13.20",
    "2023.08.2023",
    "20/2023",
    "20.08",
    "20.2023.08",
    "22.08.20233",
    "22.08.1899",
    "33.08.2023",
    "22.13.2023",
    "1999, 12, 31",
    123,
    "test",
    "12345",
    {
      date: new Date(),
    },
    [new Date()],
    "2000-00-01",
    "2000-13-01",
    "00/00/0000",
    "2000/02",
    "1899-12-31",
    "2100-01-01",
    "32/01/2000",
    "2000/01/00",
    "2000 13 01",
    "2000, 01, 00",
    "19-13-31",
    "00/00/00",
    "31 Dec 1999",
    "08, 20, 2023",
    "20, Aug, 2023",
    "Aug 20, 2023",
    "August 20, 2023",
    "Sunday, Aug 20, 2023",
    "Sat, 01 Jan 2000 00:00:00 GMT",
    true,
    null,
    undefined,
    new Date("invalid date"),
    "Date: 01/01/2000",
    "01/01/2000 is the date",
    "String 1",
    "String 01/01/2022",
    "2023-08-25T134530",
    "2023-08-25T13:45:30 EST",
    "String 2023-08-25",
    "2023-08-25T25:45:30Z",
    "2023-08-25T13:45:30+01:60",
  ];

  it("Valid Dates", () => {
    VALID_DATES.forEach((date: any) => {
      if (DEBUG)
        console.log(
          "Date to test: " + date + "\t\t\tIsDate: " + DateUtil.isDate(date),
          "\t\t\tParse Date: " + DateUtil.parseDate(date)
        );
      expect(DateUtil.isDate(date)).toBe(true);
      expect(DateUtil.parseDate(date)).toBeInstanceOf(Date);
    });
  });

  it("Invalid Dates", () => {
    INVALID_DATES.forEach((date: any) => {
      if (DEBUG)
        console.log(
          "Date to test: " + date + "\t\t\tIsDate: " + DateUtil.isDate(date),
          "\t\t\tParse Date: " + DateUtil.parseDate(date)
        );
      expect(DateUtil.isDate(date)).toBe(false);
      expect(DateUtil.parseDate(date)).toBe(null);
    });
  });
});
