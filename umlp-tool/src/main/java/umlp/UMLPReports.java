package umlp;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import de.monticore.io.paths.MCPath;

/**
 * Initializes and provides the set of reports desired for UMLP to the
 * reporting framework.
 */

public class UMLPReports implements ReportManager.ReportManagerFactory {


        protected String outputDirectory;

        protected MCPath handwrittenPath;

        protected MCPath templatePath;

        /**
         * Constructor for de.monticore.MontiCoreReports
         */
        protected UMLPReports(
                String outputDirectory,
                String reportDirectory,
                MCPath handwrittenPath,
                MCPath templatePath) {
            this.outputDirectory = outputDirectory;
            this.handwrittenPath = handwrittenPath;
            this.templatePath = templatePath;
        }

        /**
         * @see ReportManagerFactory#provide(String)
         */
        @Override
        public ReportManager provide(String modelName) {
            /* MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
            ReportingRepository repository = new ReportingRepository(identifierHelper);
            repository.initAllTemplates();*/


            ReportManager reports = new ReportManager(this.outputDirectory);

            // IncGenCheckReporter incGenCheck = new IncGenCheckReporter(this.outputDirectory, modelName);
            IncGenGradleReporter gradleReporter = new IncGenGradleReporter(this.outputDirectory, modelName);

            // reports.addReportEventHandler(incGenCheck); // IncGenCheck
            reports.addReportEventHandler(gradleReporter);

            return reports;
        }


    }
