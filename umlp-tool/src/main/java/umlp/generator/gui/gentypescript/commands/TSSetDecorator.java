/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Names;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.commands.SetDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.method.TSAccessorDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.Set;

public class TSSetDecorator extends SetDecorator {

  public TSSetDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  @Override
  protected String extendedClass() {
    return "Command<void>";
  }

  @Override
  protected void addConstructor(ASTCDClass ast, String classname, String packageName,
      String attrName, String type) {
    glex.replaceTemplate(GUI2ComponentTemplates.CONSTRUCTOR, ast,
        new TemplateHookPoint(getConstructorTemplate(), classname, packageName, attrName, type));
  }

  protected AccessorDecorator getAccessorDecorator() {
    return new TSAccessorDecorator(glex);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.set.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.set.TSDoRun";
  }

  @Override
  protected String getSet_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSSet_BuildJson";
  }

  @Override
  protected String getSet_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSSet_FromJsonObject";
  }

  @Override
  protected void addImports(ASTCDClass ast, ASTCDCompilationUnit origCD, ASTCDType domainType,
      SymTypeExpression type, boolean isGem, ASTCDAttribute attr) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainType.getName());
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX);
    getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+ "." + domainType.getName() + "AccessPolicy");
    if (GenService.isOptional(type)) {
      if (GenService.isInnerCDEnum(type)) {
        getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + GenService.getInnerType(attr.getMCType()));
      }
    }
    else {
      if (GenService.isCDEnum(type)) {
        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
      }
      else if (isGem) {
        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        getCD4C().addImport(ast, getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print());
      }
    }
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
      boolean isCDEnum, String baseClassDecorator, String typeName, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName);
    if (isCDEnum) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + Names.getSimpleName(typeName));
    }
    if (useDeSer) {
      getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator);
    }
  }

  @Override
  protected void addDeSerJavaImport(ASTCDClass ast, String typeName) {
    // don't
  }
}
