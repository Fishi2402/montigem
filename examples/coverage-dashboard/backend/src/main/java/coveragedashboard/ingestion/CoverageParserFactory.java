package coveragedashboard.ingestion;

import coveragedashboard.ReportType;

class CoverageParserFactory {

  public CoverageReportParser getParser(ReportType type){
    switch (type){
      case GENERATED_CODE_TEST:
        return new C8JsonCoverageParser();
      case GENERATOR_UNIT_TEST:
      case GENERATOR_COMPILATION_TEST:
        return new JacocoXmlCoverageParser(type);
      default:
        throw new IllegalArgumentException("Unknown ReportType: " + type);
    }
  }
}
