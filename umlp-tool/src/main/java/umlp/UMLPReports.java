package umlp;

import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.*;
import de.monticore.io.paths.MCPath;
import umlp._visitor.UMLPTraverser;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Initializes and provides the set of reports desired for UMLP to the
 * reporting framework.
 */

public class UMLPReports implements ReportManager.ReportManagerFactory {


        protected String outputDirectory;

        protected String reportDirectory;

        protected MCPath handwrittenPath;
        protected Function<Path,Path> reportPathOutput;


  // protected MCPath templatePath;

        /**
         * Constructor for de.monticore.MontiCoreReports
         */
        protected UMLPReports(
                String outputDirectory,
                String reportDirectory,
                Function<Path,Path> reportPathOutput,
                MCPath handwrittenPath) {
            this.outputDirectory = outputDirectory;
            this.reportDirectory = reportDirectory;
            this.handwrittenPath = handwrittenPath;
            this.reportPathOutput = reportPathOutput;
            // this.templatePath = templatePath;
        }

        /**
         * @see ReportManager.ReportManagerFactory#provide(String)
         */
        @Override
        public ReportManager provide(String modelName) {
            String lowerCaseName = modelName.toLowerCase();

            ReportingRepository repository = new ReportingRepository(new ASTNodeIdentHelper());
            repository.initAllTemplates();


            ReportManager reports = new ReportManager(this.reportDirectory);

          /*  UMLPTraverser traverserSummary = UMLPMill.inheritanceTraverser();
            reports.addReportEventHandler(new SummaryReporter(this.reportDirectory, lowerCaseName, repository, traverserSummary));
            reports.addReportEventHandler(new GeneratedFilesReporter(this.reportDirectory, lowerCaseName,repository));
            reports.addReportEventHandler(new HandWrittenCodeReporter(this.reportDirectory,lowerCaseName, repository));
            reports.addReportEventHandler(new TemplatesReporter(this.reportDirectory, lowerCaseName, repository));
            reports.addReportEventHandler(new HookPointReporter(this.reportDirectory, lowerCaseName,repository));
            reports.addReportEventHandler(new InstantiationsReporter(this.reportDirectory,
                    lowerCaseName));
            reports.addReportEventHandler(new VariablesReporter(this.reportDirectory, lowerCaseName));
            reports.addReportEventHandler(new DetailedReporter(this.reportDirectory, lowerCaseName, repository));
            reports.addReportEventHandler(new TemplateTreeReporter(this.reportDirectory, lowerCaseName));
            reports.addReportEventHandler(new InvolvedFilesReporter(this.reportDirectory));

            UMLPTraverser traverserNodeTree = UMLPMill.inheritanceTraverser();
            reports.addReportEventHandler(new NodeTreeReporter(this.reportDirectory, lowerCaseName, repository, traverserNodeTree));
            reports.addReportEventHandler(new NodeTreeDecoratedReporter(
                    this.reportDirectory, lowerCaseName, repository, traverserNodeTree));
            UMLPTraverser traverserNodeType = UMLPMill.inheritanceTraverser();
            reports.addReportEventHandler(new NodeTypesReporter(this.reportDirectory, lowerCaseName, traverserNodeType));
            reports.addReportEventHandler(new TransformationReporter(this.reportDirectory,
                    lowerCaseName, repository));
            reports.addReportEventHandler(new ArtifactGmlReporter(this.reportDirectory, lowerCaseName));
            reports.addReportEventHandler(new ArtifactGVReporter(this.reportDirectory, lowerCaseName));
            //reports.addReportEventHandler(new ODReporter(this.reportDirectory, lowerCaseName, repository));
            reports.addReportEventHandler(new SuccessfulReporter(this.reportDirectory, lowerCaseName));

            reports.addReportEventHandler(new IncGenGradleReporter(this.reportDirectory, reportPathOutput, lowerCaseName));*/


            return reports;
        }


    }
