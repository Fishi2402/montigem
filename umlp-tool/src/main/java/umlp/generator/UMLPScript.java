/* (c) https://github.com/MontiCore/monticore */

package umlp.generator;

import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import groovy.lang.Script;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import umlp._ast.ASTUMLPCompilationUnit;
import umlp.generator.configure.UMLPConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Baseclass for running UMLP Groovy scripts
 */
public class UMLPScript extends Script implements GroovyRunner {

//  public static final String PACKAGE_NAME = "foo";
  protected static final String[] DEFAULT_IMPORTS = { "bar" };

  protected UMLPGenerator umlpGen;

//  public static String getTaggingPackageName(List<String> packageString) {
//    return Joiners.DOT.join(Joiners.DOT.join(packageString), PACKAGE_NAME);
//  }

  /**
   * Executes the given DEx script with the given optional cfg.
   *
   * @param script        to execute (NOT file or path, the actual Groovy source code)
   * @param cfg - of this DEx execution
   */
  @Override public void run(String script, Configuration cfg) {
        GeneratorSetup genSetup = translateConfig2GenSetup(cfg);
    //    new UMLPGenerator(genSetup).run(Optional.ofNullable(cfg), script);
    new UMLPScript().run(script, genSetup);

  }

  public void run(String script, GeneratorSetup genSetup){
    GroovyInterpreter.Builder builder = getBuilder();

    UMLPConfiguration umlpConfig = UMLPConfiguration.withConfiguration(cfg);
    // we add the cfg object as property with a special property name
    builder.addVariable(UMLPConfiguration.CONFIGURATION_PROPERTY, umlpConfig);

    umlpGen= new UMLPGenerator(genSetup);

    builder.addVariable("astPrinter", UMLPGenerator.getAstPrinter());
    builder.addVariable("cD4CodeAfterParseTrafo", umlpGen.getcD4CodeAfterParseTrafo());
    builder.addVariable("cD4CodeDirectCompositionTrafo", umlpGen.getcD4CodeDirectCompositionTrafo());
    builder.addVariable("cDAssociationCreateFieldsFromNavigableRoles", umlpGen.getcDAssociationCreateFieldsFromNavigableRoles());
    builder.addVariable("cD4CodeTrafo4Defaults", umlpGen.getcD4CodeTrafo4Defaults());

    builder.addVariable("cdGenerator", umlpGen.getCdGenerator());

    //    umlpConfig.getAllValues().forEach(builder::addVariable);
    //
    //    // after adding everything we override a couple of known variable bindings to have them properly typed in the script
    //    builder.addVariable(ConfigurationOptions.MODELS.toString(),
    //        umlpConfig.getModels());
    //    builder.addVariable(ConfigurationOptions.OCL.toString(),
    //        umlpConfig.getOCL());
    //    builder.addVariable(ConfigurationOptions.MODELPATH.toString(),
    //        umlpConfig.getModelPath());
    //    builder.addVariable(ConfigurationOptions.OUT.toString(),
    //        umlpConfig.getOut());
    //    builder.addVariable(ConfigurationOptions.TEST_OUT.toString(),
    //        umlpConfig.getTestOut());
    //    builder.addVariable(ConfigurationOptions.HANDCODEDPATH.toString(),
    //        umlpConfig.getHandcodedPath());
    //    builder.addVariable(ConfigurationOptions.PACKAGE.toString(),
    //        umlpConfig.getPackagePath());
    //    builder.addVariable(ConfigurationOptions.TEMPLATEPATH.toString(),
    //        umlpConfig.getTemplatePath());
    //    builder.addVariable(ConfigurationOptions.GRAMMARS.toString(),
    //        umlpConfig.getGrammarConfig());

    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  protected GeneratorSetup translateConfig2GenSetup(Configuration cfg) {
  GeneratorSetup genSetup = new GeneratorSetup();
  return genSetup;
  }

  @Override
  public Object run() {
    return true;
  }

  // to be callable in script
  public void generate(ASTUMLPCompilationUnit modelAST) {
    new UMLPGenerator(new GeneratorSetup()).generate(modelAST);
  }

  /**
   * Entrypoint for the GroovyScript. Starts generators for
   * (1) Tags
   * (2) Scopes
   * (3) Repository
   *
   * @param modelPaths      - model path to tag schema
   * @param outputDirectory - target directory
   * @param packageStrings  - package name
   * @param handcodedPath   - path to handcoded extensions
   * @param grammars        - a list of grammars with their visitor and node types
   */
  public void generate(
      List<Path> modelPaths,
      File outputDirectory,
      List<String> packageStrings,
      List<String> handcodedPath,
      List<String> grammars
  ) {
//    checkArgument(grammars != null && !grammars.isEmpty());
//
//    MCPath hwcp = new MCPath(handcodedPath.stream().map(Paths::get).collect(Collectors.toList()));
//    String out = outputDirectory.getAbsolutePath();
//
//    // parse schemas
//    List<ASTTagSchemaUnit> tagSchemas = ParserTagSchema.getTagSchemas(modelPaths);
//    // construct scope map
//    Map<String, String> scopeMap = ParserTagScope.getScopeNames(modelPaths);
//
//    // check CoCos
//    TagSchemaCoCoChecker checker = new TagSchemaCoCos(scopeMap).getCheckerForAllCoCos();
//    tagSchemas.forEach(checker::checkAll);
//
//    // generate tags for each schema
//    tagSchemas.forEach(schema -> TagSchemaGenerator.generate(schema, grammars, scopeMap, out, hwcp));
//
//    for (String grammar : grammars) {
//      // generate visitors for scopes
//      TagScopeGenerator.generate(grammar, scopeMap, tagSchemas, out, packageStrings, hwcp);
//    }
//
//    TagCommonMatcherGenerator.generate(grammars, out, packageStrings, hwcp);
//
//    // generate repository
//    TagRepositoryGenerator.generate(tagSchemas, out, packageStrings);
//

  }

//  /**
//   * Entrypoint for the GroovyScript to create Tests.
//   *
//   * @param modelPaths      - model path to tag schema
//   * @param outputDirectory - target directory
//   * @param handcodedPath   - path to handcoded extensions
//   */
//
//  public void generateTests(List<Path> modelPaths, File outputDirectory, List<String> packageString, List<String> handcodedPath) {
//    MCPath hwcp = new MCPath(handcodedPath.stream().map(Paths::get).collect(Collectors.toList()));
//    String out = outputDirectory.getAbsolutePath();
//
//    List<ASTTagSchemaUnit> tagSchemaUnits = ParserTagSchema.getTagSchemas(modelPaths);
//
//    TagTestGenerator.generate(tagSchemaUnits, out, packageString, hwcp);
//  }

  // to be callable in script
  public void debug(String msg) {
    System.err.println(msg);
  }

  protected GroovyInterpreter.Builder getBuilder() {
    return GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(getClass())
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
  }

}
