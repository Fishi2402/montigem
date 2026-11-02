package coveragedashboard.ingestion;

import coveragedashboard.ReportType;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CoverageParserFactoryTest {

  private final CoverageParserFactory factory = new CoverageParserFactory();

  @Test
  void testSelectsC8JsonParserForJsonFile() {
    File jsonFile = new File("coverage.json");
    CoverageReportParser parser = factory.getParser(jsonFile, ReportType.GENERATED_CODE_TEST);

    assertInstanceOf(C8JsonCoverageParser.class, parser);
  }

  @Test
  void testSelectsJacocoXmlParserForXmlFile() {
    File xmlFile = new File("jacocoTestReport.xml");
    CoverageReportParser parser = factory.getParser(xmlFile, ReportType.GENERATED_CODE_TEST);

    assertInstanceOf(JacocoXmlCoverageParser.class, parser);
  }

  @Test
  void testThrowsForUnsupportedExtension() {
    File txtFile = new File("report.txt");
    assertThrows(IllegalArgumentException.class, () ->
        factory.getParser(txtFile, ReportType.GENERATED_CODE_TEST));
  }
}
