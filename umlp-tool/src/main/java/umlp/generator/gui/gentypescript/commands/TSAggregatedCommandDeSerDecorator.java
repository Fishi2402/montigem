/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.commands.AggregatedCommandDeSerDecorator;
import umlp.generator.domain.decorator.commands.GetFreeGemIdRequestDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;

import java.nio.file.Path;
import java.util.List;

public class TSAggregatedCommandDeSerDecorator extends AggregatedCommandDeSerDecorator {

  protected String astcdPackage;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSAggregatedCommandDeSerDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath, List<ASTCDClass> deSers) {
    super(glex, deSers);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName,
      List<String> commandList) {
    addCommandImports(ast, commandList);
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), commandList, classname, packageName));
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.aggregatedcommanddeser.TSConstructor";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
  }

  protected void addCommandImports(ASTCDClass ast, List<String> commandList) {
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + ast.getName();

    importHelper.addImport(this.astcdPackage + "." + COMMAND_PACKAGE + "." + GetFreeGemIdRequestDecorator.CLASS_NAME + "DeSer", importerQName);
    for (String command : commandList) {
      importHelper.addImport(command + "DeSer", importerQName);
    }
  }

}
