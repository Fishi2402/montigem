package coveragedashboard.ingestion;

import coveragedashboard.CoverageReport;
import coveragedashboard.ReportType;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;

public class CoverageIngestionService {

  private final CoverageParserFactory parserFactory;

  public CoverageIngestionService(){
    this.parserFactory = new CoverageParserFactory();
  }

  public CoverageReport ingestArtifact(File artifactFile, String reportName, Path basePath, Path rootPath, ReportType type){
    CoverageReportParser parser = parserFactory.getParser(type);
    try {
      CoverageReport coverageReport = parser.parse(artifactFile, reportName, basePath, rootPath);

      return coverageReport;
    }catch (UnsupportedOperationException e){
      Log.error("Feature pending: " + e.getMessage());
    }catch (Exception e){
      Log.error("Failed to ingest report: " + e.getMessage());
    }
    return null;
  }
}
