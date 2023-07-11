/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import com.google.common.base.Joiner;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.genjava.decorator.DomainRefDecorator;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSDomainRefDecorator extends DomainRefDecorator {

  protected ASTGUICompilationUnit guiAst;

  public TSDomainRefDecorator(final GlobalExtensionManagement glex, boolean isInputGui) {
    super(glex, isInputGui);
  }

  @Override
  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    this.guiAst = guiAst;
    super.transform(guiAst, decoratedAST);
  }

  @Override
  protected void addManagerImport(ASTCDClass clazz, String manager) {
    getCD4C().addImport(decoratedClass, util.gui2tsManagerImport(guiAst.getComponentName()));
  }

  @Override
  protected void addDefaultConstructor(ASTCDClass clazz) {
  }

  @Override
  protected void addEditConstructor(ASTCDClass clazz, List<String> params,
      List<ASTCDAttribute> domainAttributes, List<ASTCDAttribute> otherAttributes) {
    List<String> ids = util.getRoutingIds(guiAst);
    glex.replaceTemplate(getConstructorTemplate(), clazz,
        new TemplateHookPoint(getEditConstructorTemplate(), ids));
    for (String id : ids) {
      getCD4C().addAttribute(clazz, "public int " + id);
    }
  }

  @Override
  protected void addBuilderConstructor(
      ASTCDClass clazz, List<ASTCDAttribute> domainAttributes, String componentName
  ) {
    Optional<String> domainQName = util.getMainDomainModelQName();
    if (domainQName.isPresent()) {
      String className = clazz.getName();
      glex.replaceTemplate(getConstructorTemplate(), clazz,
          new TemplateHookPoint(getConstructorWithBuilderTemplate(), domainQName.get(),
              className, domainAttributes, guiDSLPrinter)
      );
      /*
      getCD4C().addConstructor(clazz, getConstructorWithBuilderTemplate(),
          domainQName.get(), className, // TODO MB: Diagramm name UMLPDefSymbol
          domainAttributes, guiDSLPrinter);
          /**/
    }
    else {
      Log.error("0x00000 GUI Model " + componentName
          + " is missing a connection to the domain model."
          + " Please specify the domain model using '-dm' flag.");
    }
  }

  @Override
  protected ASTCDMethod addInitMethod() {
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), "ngOnInit");
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getInitTemplate()));
    return method;
  }

  @Override
  protected String getInitTemplate() {
    return "umlp.gui2ts.TSInit";
  }

  @Override
  protected String getInitBuilderTemplate() {
    return "umlp.gui2ts.TSInitBuilder";
  }

  @Override
  protected String getInitDomainTemplate() {
    return "umlp.gui2ts.TSInitDomain";
  }

  @Override
  protected String getInitDomainListTemplate() {
    return "umlp.gui2ts.TSInitDomainList";
  }

  @Override
  protected void addAnnotation(ASTCDAttribute attr) {
    glex.replaceTemplate(
        GUI2ComponentTemplates.ANNOTATIONS,
        attr,
        new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_INPUT)
    );
  }

  @Override
  protected ASTMCType transformTypeForAttr(ASTCDClass clazz, ASTGUIParam param) {
    if (util.isDomainListType(param)) {
      String inner = util.getDomainListInnerType(param);
      getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
      return MCTypeFacade.getInstance().createBasicGenericTypeOf("GemList", inner);
    }
    return param.getMCType();
  }

  @Override
  protected String getBuildTemplate() {
    return "umlp.gui2ts.TSBuild";
  }

  @Override
  protected String getGetterParamTemplate() {
    return "umlp.gui2ts.TSGetParam";
  }

  @Override
  protected String getConstructorWithBuilderTemplate() {
    return "umlp.gui2ts.TSConstructorWithBuilder";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.TSConstructor";
  }

  protected String getEditConstructorTemplate() {
    return "umlp.gui2ts.TSEditConstructor";
  }

  // TODO AGe: own event decorator
  @Override
  protected void addInitialValue(ASTCDAttribute attr) {
    if (attr.getMCType().printType().equals(BuiltInTypes.EVENT.getName())) {
      glex.replaceTemplate("cd2java.Value", attr,
          new StringHookPoint(" = new EventEmitter<any>()"));
    }
  }

  @Override
  protected void addImportForGUIParameter(ASTCDClass clazz, String qName) {
    if (BuiltInTypes.EVENT.getName().equals(qName)) {
      getCD4C().addImport(clazz, qName);
      return;
    }
    super.addImportForGUIParameter(clazz, qName);
  }
}
