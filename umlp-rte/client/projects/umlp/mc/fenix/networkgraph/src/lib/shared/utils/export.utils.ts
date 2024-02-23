import { CellObject, ColInfo, utils, WorkBook, WorkSheet, writeFile } from "xlsx-js-style";
import { ColorFormat, ColorUtil, RGB } from "./color.util";
import domtoimage from "dom-to-image";

export class ExportUtils {
  private static readonly EXTENSIONS = {
    svg: "svg",
    png: "png",
    json: "json",
    excel: "xlsx",
  };

  /**
   * Downloads a given object as a JSON file.
   *
   * @param {any} exportObject - The object to be downloaded as a JSON file.
   * @param {string} exportName - The desired name for the downloaded file.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   * @param {string} fileExtension - The file extension to use for the downloaded file. Default is JSON.
   */
  public static downloadObjectAsJson(
    exportObject: any,
    exportName: string,
    appendDate: boolean = true,
    fileExtension: string = this.EXTENSIONS.json
  ): void {
    const dataString: string = `data:application/json;charset=utf-8,${encodeURIComponent(
      JSON.stringify(exportObject)
    )}`;
    this.downloadDataURL(dataString, exportName, fileExtension, appendDate);
  }

  /**
   * Downloads a given HTML Element as a PNG file.
   *
   * @param {HTMLElement} element - The HTML element to be downloaded.
   * @param {string} exportName - The desired name for the downloaded file.
   * @param {number} scale - The scale factor to use for the exported image. Default is 1.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   */
  public static downloadHTMLElementAsPNG(
    element: HTMLElement,
    exportName: string,
    scale: number = 1,
    appendDate: boolean = true
  ): void {
    domtoimage
      .toPng(element, {
        width: element.clientWidth * scale,
        height: element.clientHeight * scale,
        style: {
          transform: "scale(" + scale + ")",
          transformOrigin: "top left",
        },
      })
      .then((dataUrl: string): void => {
        ExportUtils.downloadDataURL(dataUrl, exportName, this.EXTENSIONS.png, appendDate);
      })
      .catch((error: unknown): void => {
        console.error("Error while exporting to SVG:", error);
      });
  }

  /**
   * Downloads a given HTML Element as a PNG file.
   *
   * @param {HTMLElement} element - The HTML element to be downloaded.
   * @param {string} exportName - The desired name for the downloaded file.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   */
  public static downloadHTMLElementAsSVG(element: HTMLElement, exportName: string, appendDate: boolean = true): void {
    domtoimage
      .toSvg(element)
      .then((dataUrl: string): void => {
        ExportUtils.downloadDataURL(dataUrl, exportName, this.EXTENSIONS.svg, appendDate);
      })
      .catch((error: unknown): void => {
        console.error("Error while exporting to SVG:", error);
      });
  }

  /**
   * Downloads a given canvas as a PNG file.
   *
   * @param {HTMLCanvasElement} canvas - The canvas to be downloaded.
   * @param {string} exportName - The desired name for the downloaded file.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   */
  public static downloadCanvasAsPNG(canvas: HTMLCanvasElement, exportName: string, appendDate: boolean = true): void {
    this.downloadBase64AsPNG(canvas.toDataURL("image/png"), exportName, appendDate);
  }

  /**
   * Downloads a given base64 string as a PNG file.
   *
   * @param {string} base64 - The base64 string of the PNG image.
   * @param {string} exportName - The desired name for the downloaded file.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   */
  public static downloadBase64AsPNG(base64: string, exportName: string, appendDate: boolean = true): void {
    this.downloadDataURL(base64, exportName, this.EXTENSIONS.png, appendDate);
  }

  /**
   * Downloads a given SVG content as an SVG file.
   *
   * @param {string} svgContent - The SVG content.
   * @param {string} exportName - The desired name for the downloaded file.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   */
  public static downloadSVGStringAsSVG(svgContent: string, exportName: string, appendDate: boolean = true): void {
    const blob: Blob = new Blob([svgContent], { type: "image/svg+xml;charset=utf-8" });
    const url: string = URL.createObjectURL(blob);
    this.downloadDataURL(url, exportName, this.EXTENSIONS.svg, appendDate);
    URL.revokeObjectURL(url);
  }

  /**
   * Exports a table element as an Excel file.
   *
   * @param {HTMLTableElement} table - The HTML table element containing the data.
   * @param {string} name - The name of the Excel file.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   * @param {string[]} excludeColumns - Optional array of column header names to exclude.
   */
  public static downloadTableAsExcel(
    table: HTMLTableElement,
    name: string,
    appendDate: boolean = true,
    excludeColumns: string[] = []
  ): void {
    const ws: WorkSheet = this.createWorksheetFromTableWithStyles(table, excludeColumns);
    const wb: WorkBook = utils.book_new();
    utils.book_append_sheet(wb, ws, "Sheet1");

    // Use XLSX.writeFile to trigger a download action in the browser
    writeFile(wb, this.getFileName(name, this.EXTENSIONS.excel, appendDate), { cellStyles: true });
  }

  /**
   * Utility function to get the file name for a given name and file extension. If the file name already has the
   * extension, it replaces the extension with the provided one.
   *
   * @param {string} name - Name of the file.
   * @param {string} fileExtension - File extension.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   * @returns {string} - The file name.
   */
  public static getFileName(name: string, fileExtension: string, appendDate: boolean = true): string {
    const sanitized: string = name.toLowerCase().endsWith(`.${fileExtension.toLowerCase()}`)
      ? name.slice(0, -fileExtension.length - 1)
      : name;
    return (appendDate ? `${sanitized}_${this.getFormattedDate()}` : sanitized) + `.${fileExtension}`;
  }

  /**
   * Downloads a given data url as a file.
   *
   * @param {string} dataURL - The data URL or blob URL of the file.
   * @param {string} name - The name of the file to be downloaded.
   * @param {string} fileExtension - The file extension of the file to be downloaded.
   * @param {boolean} appendDate - Whether to append the current date to the file name. Default is true.
   */
  public static downloadDataURL(
    dataURL: string,
    name: string,
    fileExtension: string,
    appendDate: boolean = true
  ): void {
    const downloadAnchorNode: HTMLAnchorElement = document.createElement("a");
    downloadAnchorNode.setAttribute("href", dataURL);
    downloadAnchorNode.setAttribute("download", this.getFileName(name, fileExtension, appendDate));
    document.body.append(downloadAnchorNode);
    downloadAnchorNode.click();
    downloadAnchorNode.remove();
  }

  /**
   * Gets the current date in the format `YYYY-MM-DD_HH-MM-SS`.
   *
   * @returns {string} The formatted date
   */
  private static getFormattedDate(): string {
    const date: Date = new Date();
    return `${date.getFullYear()}-${this.padLeadingZero(date.getMonth() + 1)}-${this.padLeadingZero(
      date.getDate()
    )}_${this.padLeadingZero(date.getHours())}-${this.padLeadingZero(date.getMinutes())}-${this.padLeadingZero(
      date.getSeconds()
    )}`;
  }

  /**
   * Pads a number with a leading zero if it is less than 10.
   *
   * @param {number} number_ - The number to pad.
   * @returns {string} - The padded string representation of the number.
   */
  private static padLeadingZero(number_: number): string {
    return String(number_).padStart(2, "0");
  }

  /**
   * Creates an Excel worksheet from an HTML table with styles.
   *
   * @param {HTMLTableElement} table - The HTML table element.
   * @param {string[]} excludeColumns - Optional array of column header names to exclude.
   * @returns {WorkSheet} - The Excel worksheet.
   */
  private static createWorksheetFromTableWithStyles(table: HTMLTableElement, excludeColumns: string[] = []): WorkSheet {
    const ws: WorkSheet = {};
    const cols: ColInfo[] = [];
    const rows: HTMLCollectionOf<HTMLTableRowElement> = table.rows;

    // Identify indexes of columns to exclude
    const excludeIndexes: number[] = [];
    if (rows.length > 0) {
      // eslint-disable-next-line unicorn/prefer-spread
      const headerCells: HTMLTableCellElement[] = Array.from(rows[0].cells);
      for (const [index, headerCell] of headerCells.entries()) {
        if (excludeColumns.includes(headerCell.textContent || "") && headerCell.tagName.toLowerCase() === "th") {
          // Exclude column when its a table header and matches any of the excluded column names
          excludeIndexes.push(index);
        } else {
          // Define column widths for non-excluded columns
          cols.push({ wpx: headerCell.offsetWidth });
        }
      }
    }
    ws["!cols"] = cols;

    // Fill in worksheet data
    // eslint-disable-next-line unicorn/no-for-loop
    for (let rowIndex: number = 0; rowIndex < rows.length; rowIndex++) {
      const row: HTMLTableRowElement = rows[rowIndex];
      for (let colIndex: number = 0; colIndex < row.cells.length; colIndex++) {
        // Skip excluded columns
        if (excludeIndexes.includes(colIndex)) continue;

        const originalCell: HTMLTableCellElement = row.cells[colIndex];
        // Get the styles from the original cell
        const computedStyle: CSSStyleDeclaration = getComputedStyle(originalCell);
        // Filter the cell only keeping visible elements
        const filteredCell: HTMLTableCellElement | null = this.filterAndCloneCell(originalCell);
        // Continue when nothing is left visible
        if (!filteredCell) continue;
        // Populate the worksheet with the value and styles at the right position
        ws[utils.encode_cell({ r: rowIndex, c: colIndex })] = {
          v: filteredCell.textContent,
          t: "s",
          s: this.generateExcelStyles(computedStyle),
        };
      }
    }

    // Adjust the end range considering excluded columns
    ws["!ref"] = utils.encode_range({
      s: { r: 0, c: 0 },
      e: { r: rows.length - 1, c: cols.length - 1 },
    });
    return ws;
  }

  /**
   * Converts a CSS color value to an Excel-compatible color value.
   *
   * @param {string} colorValue - The CSS color value.
   * @returns {string | undefined} - The Excel-compatible color value.
   */
  private static convertColor(colorValue: string): string {
    if (!colorValue || colorValue === "") return "000000";
    const parsedColor: RGB = ColorUtil.parseColorIntoFormat(colorValue, ColorFormat.RGB).newColor as RGB;
    return (
      parsedColor.a === 0
        ? "ffffff"
        : ColorUtil.toString(ColorUtil.parseColorIntoFormat(parsedColor, ColorFormat.HEX).newColor)
    ).replace("#", "");
  }

  /**
   * Generates Excel styles based on computed CSS styles.
   *
   * @param {CSSStyleDeclaration} computedStyle - The computed CSS styles of an element.
   * @returns {CellObject["s"]} - The Excel-compatible styles.
   */
  private static generateExcelStyles(computedStyle: CSSStyleDeclaration): CellObject["s"] {
    return {
      font: {
        name: computedStyle.fontFamily.split(",")[0] || "Arial",
        sz: Number.parseInt(computedStyle.fontSize, 10),
        color: { rgb: this.convertColor(computedStyle.color ?? "#000") },
        bold: Number.parseInt(computedStyle.fontWeight, 10) >= 600,
        italic: computedStyle.fontStyle === "italic",
      },
      fill: {
        patternType: "solid",
        fgColor: { rgb: this.convertColor(computedStyle.backgroundColor ?? "#fff") },
      },
      alignment: {
        horizontal: computedStyle.textAlign,
        vertical: computedStyle.verticalAlign,
        wrapText: computedStyle.whiteSpace !== "nowrap",
      },
      border: {
        top: {
          style: this.excelBorderStyle(computedStyle.borderTopStyle, computedStyle.borderTopWidth),
          color: { rgb: this.convertColor(computedStyle.borderTopColor) },
        },
        bottom: {
          style: this.excelBorderStyle(computedStyle.borderBottomStyle, computedStyle.borderBottomWidth),
          color: { rgb: this.convertColor(computedStyle.borderBottomColor) },
        },
        left: {
          style: this.excelBorderStyle(computedStyle.borderLeftStyle, computedStyle.borderLeftWidth),
          color: { rgb: this.convertColor(computedStyle.borderLeftColor) },
        },
        right: {
          style: this.excelBorderStyle(computedStyle.borderRightStyle, computedStyle.borderRightWidth),
          color: { rgb: this.convertColor(computedStyle.borderRightColor) },
        },
      },
    };
  }

  /**
   * Converts CSS border style to Excel border style.
   *
   * @param {string} cssBorderStyle - The CSS border style.
   * @param {string} borderWidth - The CSS border width.
   * @returns {string} - The Excel border style.
   */
  private static excelBorderStyle(cssBorderStyle: string, borderWidth: string): string {
    const width: number = Number.parseFloat(borderWidth);
    if (cssBorderStyle === "solid") {
      if (width === 0) return "none";
      if (width <= 0.5) return "thin";
      if (width <= 2) return "medium";
      return "thick";
    }
    return cssBorderStyle === "dashed" || cssBorderStyle === "dotted" ? cssBorderStyle : "none";
  }

  /**
   * Filters and clones an HTML table cell.
   *
   * @param {HTMLTableCellElement} cell - The original table cell.
   * @returns {HTMLTableCellElement | null} - The cloned table cell or null if the cell is not visible.
   */
  private static filterAndCloneCell(cell: HTMLTableCellElement): HTMLTableCellElement | null {
    if (!this.isElementVisible(cell)) return null;

    const newCell: HTMLTableCellElement = cell.cloneNode(false) as HTMLTableCellElement;
    const fragment: DocumentFragment = document.createDocumentFragment();

    // eslint-disable-next-line unicorn/prefer-spread
    Array.from(cell.childNodes).forEach((child: ChildNode): void => {
      const newChild: ChildNode | null = this.filterAndCloneNode(child);
      if (newChild) fragment.append(newChild);
    });

    newCell.append(fragment);
    return newCell;
  }

  /**
   * Filters and clones a node.
   *
   * @param {ChildNode} node - The original node.
   * @returns {ChildNode | null} - The cloned node or null if the node is not visible.
   */
  private static filterAndCloneNode(node: ChildNode): ChildNode | null {
    if (node instanceof Element && !this.isElementVisible(node)) return null;

    const newNode: Node = node.cloneNode(false);
    if (newNode instanceof Element || newNode instanceof DocumentFragment) {
      const fragment: DocumentFragment = document.createDocumentFragment();
      // eslint-disable-next-line unicorn/prefer-spread
      Array.from(node.childNodes).forEach((child: ChildNode): void => {
        const newChild: ChildNode | null = this.filterAndCloneNode(child);
        if (newChild) fragment.append(newChild);
      });

      newNode.append(fragment);
    }

    return newNode as ChildNode;
  }

  /**
   * Checks if an element is visible.
   *
   * @param {Element} element - The element to check.
   * @returns {boolean} - True if the element is visible, false otherwise.
   */
  private static isElementVisible(element: Element): boolean {
    return getComputedStyle(element).display !== "none";
  }
}
