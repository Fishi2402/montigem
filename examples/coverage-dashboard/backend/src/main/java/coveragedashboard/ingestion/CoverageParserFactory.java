package coveragedashboard.ingestion;

import coveragedashboard.ReportType;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

class CoverageParserFactory {

  public CoverageReportParser getParser(File artifactFile, ReportType type) {
    Objects.requireNonNull(artifactFile, "artifactFile must not be null");
    Objects.requireNonNull(type, "type must not be null");

    String fileName = artifactFile.getName().toLowerCase(Locale.ROOT);
    if (fileName.endsWith(".json")) {
      return new C8JsonCoverageParser(type);
    } else if (fileName.endsWith(".xml")) {
      return new JacocoXmlCoverageParser(type);
    } else {
      throw new IllegalArgumentException("Unsupported coverage artifact extension: " + artifactFile.getName());
    }
  }
}
