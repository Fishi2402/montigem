package coveragedashboard.ingestion;


import coveragedashboard.CoverageReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

interface CoverageReportParser {
  CoverageReport parse(File artifactFile, String reportName, Path basePath) throws IOException;
}
