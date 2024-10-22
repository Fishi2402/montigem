/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This adapter excludes every class, which are not transpiled, from getting imported.
 * E.g., DAO classes
 */
public class ImportExcludeAdapter extends PrinterAdapter {
  public ImportExcludeAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);

    Map<String, Object> configuration = getContext().options.getConfiguration();
    if (configuration.containsKey("domainName")) {
      this.excludes = computeExcludes(configuration.get("domainName").toString());
    } else {
      Log.error("0JSC005 Could not retrieve domainName from jsweetconfig file");
    }
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    for (Pattern exclude : excludes) {
      if (exclude.matcher(qualifiedName).matches()) {
        return null;
      }
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    for (Pattern exclude : excludes) {
      if (exclude.matcher(importedClass.getQualifiedName().toString()).matches()) {
        return null;
      }
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private List<Pattern> excludes = null;
  private static final String PATTERN_MANAGER_IMPL = "%s\\.%sManagerImpl";
  private static final String PATTERN_MOCK = "%s\\.%sMock";
  private static final String PATTERN_MOCK_TEST_SETUP = "%s\\.%sMockTestSetup";
  private static final String PATTERN_DUMMY_PROVIDER = "%s\\.%sDummyProvider";
  private static final String PATTERN_TEST_CONTEXT = "%s\\.%sTestContext";
  private static final String PATTERN_SERVER_APPLICATION = "%s\\.%sServerApplication";
  private static final String PATTERN_SHELL_API_APPLICATION = "%s\\.%sShellAPIApplication";
  private static final String PATTERN_DAO_FACTORY = "%s\\.%sDAOFactory";
  private static final String PATTERN_TEST_INFRASTRUCTURE = "%s\\.test_infrastructure\\..*";
  private static final String PATTERN_SHELLAPI = "%s\\.shellapi\\..*";
  private static final String PATTERN_DAO = "%s\\..*DAO";
  private static final String PATTERN_DAO_INTERFACE = "%s\\..*DAOInterface";
  private static final String PATTERN_DAO_NEO4J = "%s\\..*DAONeo4J";
  private static final String PATTERN_DB_OBSERVER = "%s\\..*DbObserver";

  private List<Pattern> computeExcludes(String domainName) {
    return Arrays.asList(
        Pattern.compile("security\\.MailService"),
        generatePattern(PATTERN_MANAGER_IMPL, domainName),
        generatePattern(PATTERN_MOCK, domainName),
        generatePattern(PATTERN_MOCK_TEST_SETUP, domainName),
        generatePattern(PATTERN_DUMMY_PROVIDER, domainName),
        generatePattern(PATTERN_TEST_CONTEXT, domainName),
        generatePattern(PATTERN_DAO, domainName),
        generatePattern(PATTERN_DAO_INTERFACE, domainName),
        generatePattern(PATTERN_DAO_FACTORY, domainName),
        generatePattern(PATTERN_DAO_NEO4J, domainName),
        generatePattern(PATTERN_DB_OBSERVER, domainName),
        generatePattern(PATTERN_TEST_INFRASTRUCTURE, domainName),
        generatePattern(PATTERN_SHELLAPI, domainName),
        generatePattern(PATTERN_SERVER_APPLICATION, domainName),
        generatePattern(PATTERN_SHELL_API_APPLICATION, domainName)
    );
  }

  private Pattern generatePattern(String pattern, String domainName) {
    return Pattern.compile(String.format(pattern, StringTransformations.uncapitalize(domainName), domainName));
  }
}
