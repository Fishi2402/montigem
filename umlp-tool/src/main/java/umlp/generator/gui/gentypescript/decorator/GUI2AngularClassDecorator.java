/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.gui.GUI2ClassDecorator;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.util.ArrayList;
import java.util.List;

public class GUI2AngularClassDecorator extends GUI2ClassDecorator implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  public static final String COMPONENT_SUFFIX = "Component";
  protected List<String> extraImports = new ArrayList<>();
  protected boolean removedObserver = false;

  public GUI2AngularClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    super.transform(gui, cd);
    gui.accept(traverser);
    addRoute();
    addRouter();
    addSubscriptions();
    addInit();
    addDestroy();
    addConstructor();
    // must run last
    addExtraImports();
  }

  @Override
  public void endVisit(ASTGUIParam node) {
    // calc type
    if (!node.isPresentSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + node.getName()
          + "' at " + node.get_SourcePositionStart().toString()
          + " - " + node.get_SourcePositionEnd().toString()
      );
    }
    SymTypeExpression typeSTE = node.getSymbol().getType();
    // exclude GUIViewElement
    if (util.isViewElement(typeSTE)
        || util.isViewElementsCollection(typeSTE)) {
      return;
    }
    String type = importAndPrintType(typeSTE);

    // calc name
    String name = "_" + node.getName();

    String getterName = "get" + StringTransformations.capitalize(node.getName());
    String setterName = "set" + StringTransformations.capitalize(node.getName());

    // add attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), type, name);
    clazz.addCDMember(attr);

    // attr overrides top class attr
    glex.replaceTemplate("umlp.gui2ts.TSAttribute", attr,
        new TemplateHookPoint("umlp.gui2ts.TSOverrideAttribute"));

    // Event attr:
    // @Output() public override _myEvent: EventEmitter<MyType> = new EventEmitter<MyType>();
    if (util.isEventArgument(node.getSymbol())) {
      glex.replaceTemplate(GUI2ComponentTemplates.ANNOTATIONS, attr,
          new StringHookPoint("@Output()"));
      glex.replaceTemplate("cd2java.Value", attr,
          new StringHookPoint(" = new EventEmitter<any>()"));
    }
    // Normal attr:
    // public override _myAttr: MyType;
    else {
      glex.replaceTemplate("umlp.Empty", attr,
          new TemplateHookPoint("umlp.gui2ts.GUI2AngularAttrGetterSetter",
              node.getMCType(), node.getName(), getterName, setterName));
    }

    if (util.getDomainListInnerType(typeSTE).isPresent()
        && !util.isEnumType(util.getDomainListInnerType(typeSTE).get())) {
      if (!removedObserver) {
        removeObserver();
        removedObserver = true;
      }
    }
  }

  @Override
  protected ASTCDClass emptyClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    String className = domainClass.getName() + COMPONENT_SUFFIX;
    return UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .build();
  }

  @Override
  protected void extendClass(ASTCDCompilationUnit cd) {
    List<ASTCDClass> classes = cd.getCDDefinition().getCDClassesList();
    if (classes.size() == 1) {
      ASTCDClass superClass = classes.get(0);
      clazz.setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(superClass.getName()));
      String qName = cd.getMCPackageDeclaration().getMCQualifiedName().getQName()
          + "." + superClass.getName();
      getCD4C().addImport(clazz, qName);
    }
  }

  @Override
  protected void addClassAnnotation() {
    if (TypeScriptTypes.DIRECTIVE.getJavaQName().isPresent()) {
      getCD4C().addImport(clazz, TypeScriptTypes.DIRECTIVE.getJavaQName().get());
    }
    glex.replaceTemplate("umlp.gui2ts.TSAnnotations", clazz,
        new TemplateHookPoint("umlp.gui2ts.TSAnnotationDirective"));
  }

  @Override
  protected void addRouter() {
    if (TypeScriptTypes.ROUTER.getJavaQName().isPresent()) {
      getCD4C().addImport(clazz, TypeScriptTypes.ROUTER.getJavaQName().get());
    }
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "Router", "_router");
    clazz.addCDMember(attr);
  }

  @Override
  protected void addConstructor() {
    List<String> ids = util.getRoutingIds(gui);
    extraImports.add("import { inject } from '@angular/core';");
    glex.replaceTemplate("umlp.gui2ts.TSConstructor", clazz,
        new TemplateHookPoint("umlp.gui2ts.GUI2AngularConstructor", ids));
  }


  protected void addRoute() {
    extraImports.add("import { ActivatedRoute } from '@angular/Router';");
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "ActivatedRoute", "_route");

    clazz.addCDMember(attr);
  }

  protected void addSubscriptions() {
    extraImports.add("import { Subscription } from 'rxjs';");
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "Subscription[]", "_subscriptions");

    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(" = []"));
    clazz.addCDMember(attr);
  }

  @Override
  protected void addInit() {
    getCD4C().addMethod(clazz, "umlp.gui2ts.GUI2AngularInit");
  }

  protected ASTCDMethodSignature addDestroy() {
    return getCD4C().addMethod(clazz, "umlp.gui2ts.GUI2AngularDestroy",
        getAndImportManager());
  }

  protected void removeObserver() {
    glex.addAfterTemplate(
        "umlp.gui2ts.GUI2AngularDestroyBody",
        new TemplateHookPoint("umlp.gui2ts.GUI2AngularRemoveObserver", getAndImportManager())
    );
  }

  protected void addExtraImports() {
    glex.replaceTemplate("umlp.gui2ts.TSEmptyImports", clazz,
        new StringHookPoint(String.join("\n", extraImports)));
  }

}
