package coveragedashboard.ingestion;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.ReportType;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class JacocoXmlCoverageParser implements CoverageReportParser {

  private final XMLInputFactory xmlFactory;
  private final CoverageModuleAggregator moduleAggregator;
  private final ReportType type;

  public JacocoXmlCoverageParser(ReportType type){
    this.xmlFactory = XMLInputFactory.newInstance();

    this.xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    this.xmlFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

    this.moduleAggregator = new CoverageModuleAggregator();

    this.type = type;
  }

  @Override
  public CoverageReport parse(File artifactFile, String reportName, Path basePath) throws IOException {
    List<CoverageSourceFile> parsedFiles = new ArrayList<>();


    try(FileInputStream inputStream = new FileInputStream(artifactFile)){
      XMLEventReader reader = xmlFactory.createXMLEventReader(inputStream);


      String currentPackage = "";
      String currentSourceFileName = null;



      int coveredInstructions = 0;
      int missedInstructions = 0;
      int coveredBranches = 0;
      int missedBranches = 0;
      int coveredLines = 0;
      int missedLines = 0;

      List<CoverageLine> currentLines = new ArrayList<>();

      while(reader.hasNext()) {
        XMLEvent event = reader.nextEvent();

        if (event.isStartElement()){
          StartElement startElement = event.asStartElement();
          String elementName = startElement.getName().getLocalPart();
          if("package".equals(elementName)){
            Attribute nameAttribute = startElement.getAttributeByName(new QName("name"));
            if(nameAttribute != null){
              currentPackage = nameAttribute.getValue();
            }
          }else if("sourcefile".equals(elementName)) {
            Attribute nameAttribute = startElement.getAttributeByName(new QName("name"));
            if (nameAttribute != null) {
              currentSourceFileName = nameAttribute.getValue();
            }
            // new file, reset counters
            coveredInstructions = 0;
            missedInstructions = 0;
            coveredBranches = 0;
            missedBranches = 0;
            coveredLines = 0;
            missedLines = 0;
          }else if("line".equals(elementName) && currentSourceFileName != null){
            // individual line data
            int nr = getIntAttribute(startElement, "nr");
            int ci = getIntAttribute(startElement, "ci");
            int mi = getIntAttribute(startElement, "mi");
            int cb = getIntAttribute(startElement, "cb");
            int mb = getIntAttribute(startElement, "mb");

            CoverageLine line = CoverageDashboardManager.coverageLineBuilder()
                .lineNumber(nr)
                .coveredInstructions(ci)
                .missedInstructions(mi)
                .coveredBranches(cb)
                .missedBranches(mb)
                .build().get();

            currentLines.add(line);

          }else if("counter".equals(elementName) && currentSourceFileName != null){
            // Aggregated file metrics
            Attribute typeAttribute = startElement.getAttributeByName(new QName("type"));
            if(typeAttribute != null){
              String type = typeAttribute.getValue();
              int missed = getIntAttribute(startElement, "missed");
              int covered = getIntAttribute(startElement, "covered");
              switch (type){
                case "INSTRUCTION":
                  missedInstructions = missed;
                  coveredInstructions = covered;
                  break;
                case "BRANCH":
                  missedBranches = missed;
                  coveredBranches = covered;
                  break;
                case "LINE":
                  missedLines = missed;
                  coveredLines = covered;
                  break;
              }
            }
          }
        }else if(event.isEndElement()){
          String elementName = event.asEndElement().getName().getLocalPart();
          if("sourcefile".equals(elementName) && currentSourceFileName != null){
            String path = currentPackage.isEmpty()
                ? currentSourceFileName
                : currentPackage + "/" + currentSourceFileName;

            CoverageMetrics fileMetrics = CoverageDashboardManager.coverageMetricsBuilder()
                .coveredInstructions(coveredInstructions)
                .missedInstructions(missedInstructions)
                .coveredBranches(coveredBranches)
                .missedBranches(missedBranches)
                .coveredLines(coveredLines).missedLines(missedLines)
                .build().get();


            CoverageSourceFileBuilder sourceFileBuilder = CoverageDashboardManager.coverageSourceFileBuilder()
                .path(path)
                .name(currentSourceFileName)
                .physicalPath((basePath + path).toString())
                .category(ArtifactCategory.GENERATED)
                .metrics(fileMetrics);

            for(CoverageLine parsedLine : currentLines){
              sourceFileBuilder.linesAdd(parsedLine);
            }
            CoverageSourceFile sourceFile = sourceFileBuilder.build().get();

            parsedFiles.add(sourceFile);
            currentSourceFileName = null; // Exit source file state
            currentLines.clear();
          }else if("package".equals(elementName)){
            currentPackage = "";
          }
        }
      }
    }catch(XMLStreamException ex){
      throw new IOException("Expected valid XML format: " + ex.getMessage(), ex);
    }

    // Aggregate into modules
    CoverageModule rootModule = moduleAggregator.groupFilesInModules(parsedFiles);

    return CoverageDashboardManager.coverageReportBuilder()
        .name(reportName)
        .type(this.type)
        .module(rootModule)
        .build().get();
  }

  private int getIntAttribute(StartElement element, String attributeName) {
    Attribute attribute = element.getAttributeByName(new QName(attributeName));
    if (attribute == null)
      return 0;
    try {
      return Integer.parseInt(attribute.getValue());
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
