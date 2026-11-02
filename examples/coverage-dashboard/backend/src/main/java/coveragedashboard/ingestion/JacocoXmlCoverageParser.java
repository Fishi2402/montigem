package coveragedashboard.ingestion;

import coveragedashboard.CoverageReport;

import java.io.File;
import java.nio.file.Path;

class JacocoXmlCoverageParser implements CoverageReportParser {

  @Override
  public CoverageReport parse(File artifactFile, String reportName, Path basePath){
    throw new UnsupportedOperationException("JaCoCo XML parsing is not yet implemented.");
  }
}
