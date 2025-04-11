/* (c) https://github.com/MontiCore/monticore */

export class Conversion {
  public static date2String(date: Date): string | null {
    if (date == null) {
      return null;
    }
    const year = String(date.getFullYear()).padStart(4, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  public static string2Date(str: string): Date | null {
    const pattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!pattern.test(str)) {
      return null;
    }

    const parts = str.split('-');
    const [year, month, day] = [+parts[0], +parts[1], +parts[2]];

    return new Date(year, month - 1, day);
  }
}