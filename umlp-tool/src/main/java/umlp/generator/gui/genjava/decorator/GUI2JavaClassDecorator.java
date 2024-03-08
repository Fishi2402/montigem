/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.GUIAbstractDecorator;

import java.util.function.Function;

public class GUI2JavaClassDecorator extends GUIAbstractDecorator implements GUIDSLVisitor2 {

  protected ASTCDClass clazz;
  protected GUIDSLTraverser traverser;
  protected ASTGUICompilationUnit gui;

  public GUI2JavaClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    this.gui = gui;

    // package
    ASTCDPackage cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(gui.getMCPackageDeclaration().getMCQualifiedName())
        .build();
    cd.getCDDefinition().addCDElement(cdPackage);

    // class
    clazz = emptyClass(gui);
    cdPackage.addCDElement(withOrigin(clazz));

    // constructor
    addConstructor();

    // TODO add router
    // addAdditionalAttributes(clazz);

    gui.accept(traverser);
  }

  @Override
  public void visit(ASTGUIParam node) {
    if (util.isCreateModel(gui.getComponentQName()) && util.isDomainType(node)) {
      addBuilderLogic(node);
    }
    else {
      addDomainLogic(node);
    }
  }

  @Override
  public void visit(ASTGUINamedArg node) {

  }

  protected ASTCDClass emptyClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    String className = domainClass.getName();
    return UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .build();
  }

  protected ASTCDMethodSignature addConstructor() {
    return getCD4C().addConstructor(clazz, "umlp.gui2java.GUI2JavaConstructor");
  }

  protected ASTCDAttribute addBuilderLogic(ASTGUIParam p) {
    // attr
    ASTCDAttribute attr = addBuilderAttr(clazz, p);

    // getter
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    clazz.addAllCDMembers(methodDecorator.decorate(attr));

    // build
    getCD4C().addMethod(clazz, getBuildErrorTemplate(), attr);
    getCD4C().addMethod(clazz, getBuildTemplate(), attr);

    // init
    if (util.getMainDomainModelSimpleName().isPresent()) {
      // builder init
      ASTCDMethodSignature builderInit = getCD4C().addMethod(
          clazz, getInitBuilderTemplate(),
          attr, ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get())
      );

      // add builder init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitBuilderCallTemplate(), builderInit)
      );
    }
    else {
      Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
    }
    return attr;
  }

  protected ASTCDAttribute addDomainLogic(ASTGUIParam p) {
    // attr
    ASTCDAttribute attr = addDomainAttr(clazz, p);

    // getter
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    clazz.addAllCDMembers(methodDecorator.decorate(attr));

    // init
    if (util.getMainDomainModelSimpleName().isPresent()) {
      // attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDomainTemplate(),
          attr, ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get())
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitBuilderCallTemplate(), attrInit)
      );
    }
    else {
      Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
    }
    return attr;
  }

  protected ASTCDAttribute addBuilderAttr(ASTCDClass clazz, ASTGUIParam param) {
    return addAttr(clazz, param, x -> BuilderDecorator.getClassName(x));
  }

  protected ASTCDAttribute addDomainAttr(ASTCDClass clazz, ASTGUIParam param) {
    return addAttr(clazz, param, x -> x);
  }

  protected ASTCDAttribute addAttr(
      ASTCDClass clazz, ASTGUIParam param, Function<String, String> typeConversion
  ) {
    // type
    if (!param.isPresentSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + param.getName()
          + "' at " + param.get_SourcePositionStart().toString()
          + " - " + param.get_SourcePositionEnd().toString()
      );
    }
    GUIParamSymbol paramSym = param.getSymbol();
    String type = paramSym.getType().print();
    getCD4C().addImport(clazz, paramSym.getType().printFullName());

    // name
    String name = param.getName();
    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), type, name);

    clazz.addCDMember(domainAttribute);

    return domainAttribute;
  }

  protected String getInitDomainTemplate() {
    return "umlp.gui2java.GUI2JavaInitDomain";
  }

  protected String getInitBuilderTemplate() {
    return "umlp.gui2java.GUI2JavaInitBuilder";
  }

  protected String getInitBuilderCallTemplate() {
    return "umlp.gui2java.GUI2JavaInitBuilderCall";
  }

  protected String getBuildErrorTemplate() {
    return "umlp.gui2java.GUI2JavaBuildError";
  }

  protected String getBuildTemplate() {
    return "umlp.gui2java.GUI2JavaBuild";
  }

  protected String getInitBodyHookpoint() {
    return "umlp.gui2java.GUI2JavaInitBody";
  }

}
