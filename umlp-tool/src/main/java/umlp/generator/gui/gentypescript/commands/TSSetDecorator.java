/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.codegen.methods.AccessorDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Names;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.commands.SetDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.method.TSAccessorDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;
import java.util.Set;

public class TSSetDecorator extends SetDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSSetDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
    this.hwPath = hwPath;
    this.outPath = outPath;
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
  protected String getAssocsCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.commands.assocs.TSAssocsCheckAccessPolicy";
  }

  @Override
  protected String getGeneralCheckAccessPolicyTemplate() {
    return "umlp.gui2ts.accesspolicy.TSGeneralCheckAccessPolicy";
  }

  @Override
  protected void addImportAccessPolicy(ASTCDClass ast, SymTypeExpression type, String otherType) {
    ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(cd, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(cd, DEFAULT_PACKAGE) + "." + otherType + "AccessPolicy", importerQName);
  }

  @Override
  protected void addImports(ASTCDClass ast, ASTCDCompilationUnit origCD, ASTCDType domainType,
      SymTypeExpression type, boolean isGem, ASTCDAttribute attr) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("StatusResult"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(ast, PathUtil.getBackendRTEUtilPath("LogUtil"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("GeneralErrorCollector"));
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainType.getName(), importerQName);
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + origCD.getCDDefinition().getName() + ManagerDecorator.SUFFIX, importerQName);
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + domainType.getName() + "AccessPolicy", importerQName);
    if (GenService.isOptional(type)) {
      if (GenService.isInnerCDEnum(type)) {
        importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + GenService.getInnerType(attr.getMCType()), importerQName);
      }
    }
    else {
      if (GenService.isCDEnum(type)) {
        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        importHelper.addImport(getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print(), importerQName);
      }
      else if (isGem) {
        ASTCDCompilationUnit typeCD = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());
        importHelper.addImport(getPackageName(typeCD, DEFAULT_PACKAGE) + "." + type.print(), importerQName);
      }
    }
  }

  @Override
  protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD, boolean useDeSer,
      boolean isCDEnum, String baseClassDecorator, String typeName, String cmdName) {
    getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = getPackageName(origCD, COMMAND_PACKAGE) + "." + ast.getName();

    importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE) + "." + cmdName, importerQName);
    if (isCDEnum) {
      importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + Names.getSimpleName(typeName), importerQName);
    }
    if (useDeSer) {
      importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseClassDecorator, importerQName);
    }
  }

  @Override
  protected void addDeSerJavaImport(ASTCDClass ast, String typeName) {
    // don't
  }
}
