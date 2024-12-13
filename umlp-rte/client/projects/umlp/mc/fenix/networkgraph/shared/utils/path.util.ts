/** Enum storing the supported web schemes */
export enum WebSchemes {
  /** HTTP Websites */
  HTTP = "http",

  /** HTTPS Websites */
  HTTPS = "https",

  /** FPT Paths */
  FTP = "ftp",

  /** File Paths */
  FILE = "file",
}

/** Utility class to manage, manipulate, and clean file paths. */
export class PathUtil {
  /** An empty string constant. */
  private static readonly EMPTY_STRING: string = "";

  /** Prefix used for extended paths in Windows. */
  private static readonly EXTENDED_PATH_PREFIX: string = "?";

  /** A string representing the current directory. */
  private static readonly CURRENT_DIR: string = ".";

  /** A string representing the parent directory. */
  private static readonly PARENT_DIR: string = "..";

  /** A string representing a forward slash. */
  private static readonly FORWARD_SLASH: string = "/";

  /** A string representing a backward slash. */
  private static readonly BACKWARD_SLASH: string = "\\";

  /** A string representing a double backward slash. */
  private static readonly DOUBLE_BACKWARD_SLASH: string = "\\\\";

  /** A string representing a double forward slash. */
  private static readonly DOUBLE_FORWARD_SLASH: string = "//";

  /** A string representing the http(s) prefix. */
  private static readonly PROTOCOL_SEPARATOR: string = "://";

  /**
   * Extract the base name from a given path, which is the last component in the path.
   *
   * @example
   *   PathUtils.baseName("./assets/dir/abc"); // Returns "abc"
   *
   * @param {string} path - The path to extract the base name from.
   * @returns {string} - The base name.
   */
  public static baseName(path: string): string {
    return this.splitFileExtension(path)[0];
  }

  /**
   * Get the extension of a file.
   *
   * @param {string} fileName - The filename to extract the extension from.
   * @returns {string} - The file extension.
   */
  public static getExtension(fileName: string): string {
    return this.isWebPath(fileName) ? this.EMPTY_STRING : this.splitFileExtension(fileName)[1];
  }

  /**
   * Determines if a given path is a web path.
   *
   * @param {string} path - The path to check.
   * @returns {boolean} - True if it's a web path, false otherwise.
   */
  public static isWebPath(path: string): boolean {
    return !!PathUtil.getProtocol(path);
  }

  /**
   * Determines if a given path is a local file path.
   *
   * @param {string} path - The path to check.
   * @returns {boolean} - True if it's a local file path, false otherwise.
   */
  public static isLocalFilePath(path: string): boolean {
    return !this.isWebPath(path);
  }

  /**
   * @param {string} path - The file path or URL.
   * @returns {string | null} - Returns the protocol of the URL, or null for local paths.
   */
  public static getProtocol(path: string): WebSchemes | null {
    const scheme: string = path.split(PathUtil.PROTOCOL_SEPARATOR)[0].toLowerCase();
    if (Object.values(WebSchemes).includes(scheme as WebSchemes)) return scheme as WebSchemes;
    return null;
  }

  /**
   * Returns a meaningful name based on the path depending on its type. For local paths, it returns the base name of the
   * path. For web paths, it returns the hostname.
   *
   * @param {string} path - The file path or URL.
   * @returns {string} - Returns the base name of a local path or meaningful part of a web path.
   */
  public static getName(path: string): string {
    return this.isWebPath(path) ? this.extractReadableURLSegment(path) || path : this.baseName(path);
  }

  /**
   * Normalize a given file path.
   *
   * @example
   *   PathUtils.normalizeFilePath("./a/b/.././c/d/../e/"); // Returns "a/c/e"
   *
   * @param {string} filePath - The path to normalize.
   * @param {boolean} stripTrailing - Flag to remove trailing slashes.
   * @returns {string} - The normalized path.
   */
  public static normalizeFilePath(filePath: string, stripTrailing: boolean = true): string {
    const protocol: WebSchemes | null = this.getProtocol(filePath);
    let protocolPrefix: string = "";

    if (protocol) {
      protocolPrefix = `${protocol}${PathUtil.PROTOCOL_SEPARATOR}`;
      filePath = filePath.slice(protocolPrefix.length);
    }

    if (this.isRootPath(filePath)) return this.FORWARD_SLASH;
    if (filePath.length <= 1) return protocolPrefix + filePath;

    const prefix: string = this.getWin32NamespacePrefix(filePath);
    if (prefix) filePath = filePath.slice(2);

    const segments: string[] = this.splitFilePath(filePath);
    if (stripTrailing && segments.at(-1) === "") segments.pop();

    return protocolPrefix + prefix + this.resolvePathSegments(segments).join(this.FORWARD_SLASH);
  }

  /**
   * Joins the given path segments together.
   *
   * @example
   *   PathUtils.join("a", "b", "c"); // Returns 'a/b/c'
   *
   * @param {...string} segments - The segments to concatenate.
   * @returns {string} - The concatenated path.
   */
  public static join(...segments: string[]): string {
    if (segments.length === 0) return this.EMPTY_STRING;
    return this.normalizeFilePath(segments.join(this.FORWARD_SLASH));
  }

  /**
   * Split a filename into its name and extension.
   *
   * @example
   *   PathUtils.splitFileExtension("example.txt"); // Returns ["example", "txt"]
   *
   * @param {string} fileName - The filename to split.
   * @returns {string[]} - An array containing the name and extension. If no extension is found, the second element is
   *   an empty string.
   */
  public static splitFileExtension(fileName: string): string[] {
    // If we have a web path, just return it as it is without any extension
    if (this.isWebPath(fileName)) return [fileName, this.EMPTY_STRING];

    const segments: string[] = this.splitFilePath(fileName);
    const lastSegment: string = segments.at(-1) || segments.at(-2) || fileName;

    const lastDotIndex: number = lastSegment.lastIndexOf(".");
    if (lastDotIndex === -1 || lastDotIndex === 0 || lastDotIndex === lastSegment.length - 1) {
      // No file extension
      return [lastSegment, this.EMPTY_STRING];
    }

    return [lastSegment.slice(0, lastDotIndex), lastSegment.slice(lastDotIndex + 1)];
  }

  /**
   * Check if a filename has a valid extension.
   *
   * @example
   *   PathUtils.hasValidFileExtension("example.txt", ".txt"); // Returns true
   *   PathUtils.hasValidFileExtension("example.txt", ".json"); // Returns false
   *   PathUtils.hasValidFileExtension("example.txt", ".txt", "json"); // Returns true
   *
   * @param {string} fileName - Name of the file.
   * @param {...string} validExtensions - One or more valid file extensions.
   * @returns {boolean} - True if the filename has one of the valid extensions, false otherwise.
   */
  static hasValidFileExtension(fileName: string, ...validExtensions: string[]): boolean {
    const lowerCaseFileName: string = fileName.toLowerCase();

    return validExtensions.some((extension: string): boolean => {
      const lowerCaseValidExtension: string = extension.startsWith(PathUtil.CURRENT_DIR)
        ? extension.toLowerCase()
        : `${PathUtil.CURRENT_DIR}${extension.toLowerCase()}`;
      return lowerCaseFileName.endsWith(lowerCaseValidExtension);
    });
  }

  /**
   * Check if the given path is a root path.
   *
   * @private
   * @param {string} filePath - The path to check.
   * @returns {boolean} - True if it's a root path, false otherwise.
   */
  private static isRootPath(filePath: string): boolean {
    return filePath === this.BACKWARD_SLASH || filePath === this.FORWARD_SLASH;
  }

  /**
   * Get the win32 namespace prefix from a given path.
   *
   * @private
   * @param {string} filePath - The path to extract the prefix from.
   * @returns {string} - The extracted prefix.
   */
  private static getWin32NamespacePrefix(filePath: string): string {
    if (filePath.length <= 4) return this.EMPTY_STRING;
    if (filePath[3] !== this.BACKWARD_SLASH) return this.EMPTY_STRING;

    const ch: string = filePath[2];
    if (
      (ch === this.EXTENDED_PATH_PREFIX || ch === this.CURRENT_DIR) &&
      filePath.startsWith(this.DOUBLE_BACKWARD_SLASH)
    ) {
      return this.DOUBLE_FORWARD_SLASH;
    }

    return this.EMPTY_STRING;
  }

  /**
   * Split a file path into its individual segments.
   *
   * @private
   * @param {string} filePath - The path to split.
   * @returns {string[]} - The split path segments.
   */
  private static splitFilePath(filePath: string): string[] {
    // Split the file path on all "/" and "\\" characters.
    return filePath.split(new RegExp(`[${this.FORWARD_SLASH}${this.DOUBLE_BACKWARD_SLASH}]+`));
  }

  /**
   * Resolve the path segments by removing any "." or ".." references.
   *
   * @private
   * @param {string[]} segments - The path segments to resolve.
   * @returns {string[]} - The resolved path segments.
   */
  private static resolvePathSegments(segments: string[]): string[] {
    const resolvedSegments: string[] = [];
    for (const currentSegment of segments) {
      if (currentSegment === this.PARENT_DIR && resolvedSegments.length > 0) {
        resolvedSegments.pop();
      } else if (currentSegment !== this.CURRENT_DIR) {
        resolvedSegments.push(currentSegment);
      }
    }
    return resolvedSegments;
  }

  /**
   * Extracts a human-readable segment from a URL for display purposes.
   *
   * Functionality:
   *
   * - The function first parses the given URL to separate its hostname and pathname.
   * - As a fallback, it takes the last two segments of the hostname to use as a default visual representation.
   * - It then identifies the last non-empty segment from the URL's pathname.
   * - This last segment is checked for readability based on specific criteria (see isReadableBlock).
   * - If the last segment passes the readability checks, it is returned.
   * - If it doesn't pass or if there is no last segment, the default hostname-based representation is returned.
   *
   * Readability Criteria:
   *
   * - A segment is considered readable if all its 'blocks' are readable.
   * - Blocks are substrings of a segment, separated by hyphens or underscores.
   * - Each block is readable if it doesn't have more than 2 transitions between letters and numbers.
   *
   * @param {string} url - The URL that needs to be processed for extracting a readable segment.
   * @returns {string | null} - Returns either a readable segment for visual display or null if the URL is invalid.
   */
  private static extractReadableURLSegment(url: string): string | null {
    try {
      const urlObject: URL = new URL(url);

      // Default to the last two segments of the hostname for representation
      const domainRepresentation: string = urlObject.hostname.split(".").slice(-2).join(".");

      // Get the last non-empty segment of the URL's pathname
      const lastSegment: string | undefined = urlObject.pathname
        .split("/")
        .filter((s: string): boolean => s.trim() !== "")
        .at(-1);

      if (lastSegment) {
        // Split the last segment by hyphen or underscore
        const blocks: string[] = lastSegment.split(/[_-]/);

        // Check readability of each block
        if (blocks.every(this.isReadableBlock)) return lastSegment;
      }

      // Fallback to the domain when the last segment is not human readable
      return domainRepresentation;
    } catch (error) {
      console.error("Invalid URL:", error);
      return null;
    }
  }

  /**
   * Checks if a given string block is readable.
   *
   * @param {string} block - A string segment, without hyphens or underscores.
   * @returns {boolean} - Returns true if the block is readable, false otherwise.
   */
  private static isReadableBlock(block: string): boolean {
    let transitions: number = 0;
    let wasPreviousChartDigit: boolean = !Number.isNaN(Number(block[0]));

    for (let index: number = 1; index < block.length; index++) {
      const isDigit: boolean = !Number.isNaN(Number(block[index]));
      if (isDigit !== wasPreviousChartDigit) transitions++;
      wasPreviousChartDigit = isDigit;
    }

    // Threshold for transitions between letters and numbers within a block
    return transitions <= 2;
  }
}
