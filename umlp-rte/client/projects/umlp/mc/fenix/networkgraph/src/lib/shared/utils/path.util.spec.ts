import { PathUtil } from "./path.util";

describe("PathUtils", () => {
  describe("baseName", () => {
    it("should return the base name of the path", () => {
      expect(PathUtil.baseName("./assets/dir/abc")).toBe("abc");
      expect(PathUtil.baseName("abc")).toBe("abc");
      expect(PathUtil.baseName("/abc")).toBe("abc");
    });

    it("should properly remove extensions the base name of the path", () => {
      expect(PathUtil.baseName("./assets/dir/abc.json")).toBe("abc");
      expect(PathUtil.baseName("abc.png")).toBe("abc");
      expect(PathUtil.baseName("/abc_sample.xlsx")).toBe("abc_sample");
    });

    it("should handle paths ending with a slash", () => {
      expect(PathUtil.baseName("./assets/dir/")).toBe("dir");
      expect(PathUtil.baseName("/")).toBe("/");
    });

    it("should throw an error for non-string input", () => {
      expect(() => PathUtil.baseName(null as any)).toThrow();
      expect(() => PathUtil.baseName(undefined as any)).toThrow();
      expect(() => PathUtil.baseName(123 as any)).toThrow();
    });

    it("should return an empty string for empty paths", () => {
      expect(PathUtil.baseName("")).toBe("");
    });
  });

  describe("getProtocol", () => {
    it("should return the protocol for a web path", () => {
      expect(PathUtil.getProtocol("http://example.com")).toBe("http");
      expect(PathUtil.getProtocol("https://example.com")).toBe("https");
      expect(PathUtil.getProtocol("ftp://example.com")).toBe("ftp");
    });

    it("should return null for a local path", () => {
      expect(PathUtil.getProtocol("C:/example.txt")).toBe(null);
      expect(PathUtil.getProtocol("/usr/local/example.txt")).toBe(null);
    });

    it("should be case insensitive", () => {
      expect(PathUtil.getProtocol("HTTP://example.com")).toBe("http");
      expect(PathUtil.getProtocol("hTTpS://example.com")).toBe("https");
    });

    it("should throw a TypeError for non-string input", () => {
      expect(() => PathUtil.getProtocol(null as any)).toThrow();
      expect(() => PathUtil.getProtocol(undefined as any)).toThrow();
      expect(() => PathUtil.getProtocol(123 as any)).toThrow();
    });
  });

  describe("getName", () => {
    it("should return the base name for local paths", () => {
      expect(PathUtil.getName("C:/folder/subfolder/file.txt")).toBe("file");
      expect(PathUtil.getName("/usr/local/file.txt")).toBe("file");
      expect(PathUtil.getName("file.txt")).toBe("file");
    });

    it("should return the last part of the path for web URLs", () => {
      expect(PathUtil.getName("http://example.com/folder/file")).toBe("file");
      expect(PathUtil.getName("https://example.com/folder/file")).toBe("file");
    });

    it("should return human-readable segments or domain for various URL formats", () => {
      expect(PathUtil.getName("https://chat.openai.com/c/25575be8-01f0-4aa7-9145-4c6cb2989554")).toBe("openai.com");
      expect(PathUtil.getName("https://mocki.io/v1/85c7e209-7f2c-461e-9119-c3d8573dd72a")).toBe("mocki.io");
      expect(PathUtil.getName("https://mocki.io/v1/sample_data_2023-10-04")).toBe("sample_data_2023-10-04");
      expect(PathUtil.getName("https://mocki.io/v1/sample2023_data10_04")).toBe("sample2023_data10_04");
      expect(PathUtil.getName("https://mocki.io/v1/s20am2p3le-10_da-04-ta")).toBe("mocki.io");
    });

    it("should handle trailing slashes", () => {
      expect(PathUtil.getName("http://example.com/folder/")).toBe("folder");
      expect(PathUtil.getName("C:/folder/subfolder/")).toBe("subfolder");
    });

    it("should handle root paths and URLs", () => {
      expect(PathUtil.getName("http://example.com/")).toBe("example.com");
      expect(PathUtil.getName("C:/")).toBe("C:");
      expect(PathUtil.getName("/")).toBe("/");
    });

    it("should throw a TypeError for non-string input", () => {
      expect(() => PathUtil.getName(null as any)).toThrow();
      expect(() => PathUtil.getName(undefined as any)).toThrow();
      expect(() => PathUtil.getName(123 as any)).toThrow();
    });

    it("should return an empty string for empty input", () => {
      expect(PathUtil.getName("")).toBe("");
    });
  });

  describe("isWebPath", () => {
    it("should return true for http://example.com", () => {
      expect(PathUtil.isWebPath("http://example.com")).toBe(true);
    });

    it("should return true for https://example.com", () => {
      expect(PathUtil.isWebPath("https://example.com")).toBe(true);
    });

    it("should return false for C:/example.txt", () => {
      expect(PathUtil.isWebPath("C:/example.txt")).toBe(false);
    });

    it("should return false for /usr/local/example.txt", () => {
      expect(PathUtil.isWebPath("/usr/local/example.txt")).toBe(false);
    });

    it("should throw a TypeError for non-string input", () => {
      expect(() => PathUtil.isWebPath(null as any)).toThrow();
    });
  });

  describe("isLocalFilePath", () => {
    it("should return false for http://example.com", () => {
      expect(PathUtil.isLocalFilePath("http://example.com")).toBe(false);
    });

    it("should return true for C:/example.txt", () => {
      expect(PathUtil.isLocalFilePath("C:/example.txt")).toBe(true);
    });

    it("should return true for /usr/local/example.txt", () => {
      expect(PathUtil.isLocalFilePath("/usr/local/example.txt")).toBe(true);
    });

    it("should throw a TypeError for non-string input", () => {
      expect(() => PathUtil.isLocalFilePath(null as any)).toThrow();
    });
  });

  describe("join", () => {
    it("should return an empty string if no segments are given", () => {
      expect(PathUtil.join()).toBe("");
    });

    it("should concatenate and normalize given paths", () => {
      expect(PathUtil.join("folder", "subfolder\\file")).toBe("folder/subfolder/file");
    });
  });

  describe("hasValidFileExtension", () => {
    // Test for valid single file extension
    it("should return true for valid single file extensions", () => {
      expect(PathUtil.hasValidFileExtension("test.PNG", ".png")).toBe(true);
    });

    // Test for invalid single file extension
    it("should return false for invalid single file extensions", () => {
      expect(PathUtil.hasValidFileExtension("test.jpg", ".png")).toBe(false);
    });

    // Test for valid multiple file extensions
    it("should return true for valid multiple file extensions", () => {
      expect(PathUtil.hasValidFileExtension("test.jpg", ".png", ".jpg")).toBe(true);
    });

    // Test for invalid multiple file extensions
    it("should return false for invalid multiple file extensions", () => {
      expect(PathUtil.hasValidFileExtension("test.gif", ".png", ".jpg")).toBe(false);
    });

    // Test for valid extension without leading dot
    it("should return true for valid extension without leading dot", () => {
      expect(PathUtil.hasValidFileExtension("test.PNG", "png")).toBe(true);
    });

    // Test for multiple valid extensions with and without leading dots
    it("should return true for multiple valid extensions with and without leading dots", () => {
      expect(PathUtil.hasValidFileExtension("test.JPG", ".png", "jpg")).toBe(true);
    });

    // Test for case insensitivity
    it("should be case-insensitive", () => {
      expect(PathUtil.hasValidFileExtension("test.PNG", ".PNG")).toBe(true);
      expect(PathUtil.hasValidFileExtension("TEST.png", ".png")).toBe(true);
    });
  });

  describe("splitFileExtension", () => {
    it("should split file name and extension correctly", () => {
      expect(PathUtil.splitFileExtension("example.txt")).toEqual(["example", "txt"]);
      expect(PathUtil.splitFileExtension("archive.tar.gz")).toEqual(["archive.tar", "gz"]);
      expect(PathUtil.splitFileExtension("no_extension")).toEqual(["no_extension", ""]);
    });

    it("should handle filenames starting or ending with a dot", () => {
      expect(PathUtil.splitFileExtension(".hiddenfile")).toEqual([".hiddenfile", ""]);
      expect(PathUtil.splitFileExtension("file.")).toEqual(["file.", ""]);
    });

    it("should handle webpaths properly", () => {
      expect(PathUtil.splitFileExtension("http://localhost:4200/?dashboard=reduced")).toEqual([
        "http://localhost:4200/?dashboard=reduced",
        "",
      ]);
      expect(PathUtil.splitFileExtension("https://primeng.org/icons.")).toEqual(["https://primeng.org/icons.", ""]);
    });

    it("should return an empty extension for empty filenames", () => {
      expect(PathUtil.splitFileExtension("")).toEqual(["", ""]);
    });

    it("should throw an error for non-string inputs", () => {
      expect(() => PathUtil.splitFileExtension(null as any)).toThrow();
      expect(() => PathUtil.splitFileExtension(undefined as any)).toThrow();
      expect(() => PathUtil.splitFileExtension(123 as any)).toThrow();
    });
  });

  describe("getExtension", () => {
    it("should return the extension of the path", () => {
      expect(PathUtil.getExtension("./assets/dir/abc")).toBe("");
      expect(PathUtil.getExtension("abc")).toBe("");
      expect(PathUtil.getExtension("/abc")).toBe("");
    });

    it("should properly remove extensions the extension of the path", () => {
      expect(PathUtil.getExtension("./assets/dir/abc.json")).toBe("json");
      expect(PathUtil.getExtension("abc.png")).toBe("png");
      expect(PathUtil.getExtension("/abc_sample.xlsx")).toBe("xlsx");
    });

    it("should handle paths ending with a slash", () => {
      expect(PathUtil.getExtension("./assets/dir/")).toBe("");
      expect(PathUtil.getExtension("/")).toBe("");
    });

    it("should throw an error for non-string input", () => {
      expect(() => PathUtil.getExtension(null as any)).toThrow();
      expect(() => PathUtil.getExtension(undefined as any)).toThrow();
      expect(() => PathUtil.getExtension(123 as any)).toThrow();
    });

    it("should return an empty string for empty paths", () => {
      expect(PathUtil.baseName("")).toBe("");
    });
  });

  describe("normalizeFilePath", () => {
    it("should throw an error if the path is not a string", () => {
      expect(() => PathUtil.normalizeFilePath(null as any)).toThrow();
    });

    it("should handle all types of malformed paths properly", () => {
      expect(PathUtil.normalizeFilePath("./a/b/.././c/d/../e/")).toEqual("a/c/e");
    });

    it("should handle root paths", () => {
      expect(PathUtil.normalizeFilePath("\\")).toBe("/");
      expect(PathUtil.normalizeFilePath("/")).toBe("/");
    });

    it("should not change paths with length <= 1", () => {
      expect(PathUtil.normalizeFilePath("a")).toBe("a");
    });

    it("should handle win32 namespaces", () => {
      expect(PathUtil.normalizeFilePath("\\\\?\\test\\path")).toBe("//?/test/path");
      expect(PathUtil.normalizeFilePath("\\\\.\\test\\path")).toBe("//test/path");
    });

    it("should split and reconstruct paths", () => {
      expect(PathUtil.normalizeFilePath("folder//subfolder\\file")).toBe("folder/subfolder/file");
    });

    it("should strip trailing slashes if asked to", () => {
      expect(PathUtil.normalizeFilePath("folder/subfolder/", true)).toBe("folder/subfolder");
    });

    it('should handle ".." in paths', () => {
      expect(PathUtil.normalizeFilePath("folder/../folder2")).toBe("folder2");
    });

    it('should handle "." in paths', () => {
      expect(PathUtil.normalizeFilePath("folder/./file")).toBe("folder/file");
    });

    it("should preserve http:// protocol and normalize the rest of the path", () => {
      const path = "http://example.com/a/b/.././c/d/../e/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("http://example.com/a/c/e");
    });

    it("should preserve https:// protocol and normalize the rest of the path", () => {
      const path = "https://example.com/a/b/.././c/d/../e/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("https://example.com/a/c/e");
    });

    it("should not add protocol if not present", () => {
      const path = "example.com/a/b/.././c/d/../e/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("example.com/a/c/e");
    });

    it("should handle paths without any directory traversal", () => {
      const path = "https://example.com/a/b/c/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("https://example.com/a/b/c");
    });

    it("should return root path for just the protocol", () => {
      const path = "http://";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("http://");
    });

    it("should handle mixed slashes after protocol", () => {
      const path = "https://example.com/a/b\\c/d/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("https://example.com/a/b/c/d");
    });

    it("should preserve ftp:// protocol and normalize the rest of the path", () => {
      const path = "ftp://example.com/a/b/.././c/d/../e/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("ftp://example.com/a/c/e");
    });

    it("should preserve file:// protocol and normalize the rest of the path", () => {
      const path = "file://example.com/a/b/.././c/d/../e/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("file://example.com/a/c/e");
    });

    it("should handle local file paths", () => {
      const path = "/a/b/.././c/d/../e/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("/a/c/e");
    });

    it("should handle Windows-style local file paths", () => {
      const path = "\\a\\b\\..\\.\\c\\d\\..\\e\\";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("/a/c/e");
    });

    it("should return an empty string for an empty input", () => {
      const path = "";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("");
    });

    it("should handle a single forward slash", () => {
      const path = "/";
      const normalized = PathUtil.normalizeFilePath(path);
      expect(normalized).toBe("/");
    });
  });
});
