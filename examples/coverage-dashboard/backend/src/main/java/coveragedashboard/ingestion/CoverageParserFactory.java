package coveragedashboard.ingestion;

import coveragedashboard.ReportType;

class CoverageParserFactory {

  public CoverageReportParser getParser(ReportType type){
    switch (type){
      case E2E_FRONTEND:
        return new C8JsonCoverageParser();
      case E2E_BACKEND:
      case INTERNAL:
        return new JacocoXmlCoverageParser();
      default:
        throw new IllegalArgumentException("Unknown ReportType: " + type);
    }
  }
}
