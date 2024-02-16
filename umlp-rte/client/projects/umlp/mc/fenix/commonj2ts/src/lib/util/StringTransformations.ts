export class StringTransformations {
  /**
   * Capitalizes the given String.
   */
  public static capitalize(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }


  /**
   * Uncapitalizes the given String.
   */
  public static uncapitalize(str: string): string {
    return str.charAt(0).toLowerCase() + str.slice(1);
  }

}
