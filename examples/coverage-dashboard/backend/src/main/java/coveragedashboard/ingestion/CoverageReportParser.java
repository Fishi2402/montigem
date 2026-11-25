package coveragedashboard.ingestion;


import coveragedashboard.CoverageReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

interface CoverageReportParser {
  CoverageReport parse(
      File artifactFile,
      String reportName,
      Path basePath,
      Path rootPath,
      Map<String, Double> perspectiveWeights) throws IOException;
}
