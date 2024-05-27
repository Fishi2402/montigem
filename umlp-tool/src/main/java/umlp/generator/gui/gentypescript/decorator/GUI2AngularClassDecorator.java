/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUI2ClassDecorator;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GUI2AngularClassDecorator extends GUI2ClassDecorator implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  public static final String COMPONENT_SUFFIX = "Component";
  protected Set<String> extraImports = new HashSet<>();
  protected boolean removedObserver = false;

  protected final MCPath hwPath;

  public GUI2AngularClassDecorator(final GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex);
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    this.hwPath = hwPath;
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
  protected void replaceTemplates() {
    super.replaceTemplates();
    glex.replaceTemplate(
        "cd2java.Package", new StringHookPoint(""));
    glex.replaceTemplate(
        "cd2java.Class", new TemplateHookPoint("umlp.gui2ts.TSClass"));
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
    ASTMCType mcType = createTypeAndAddImports(typeSTE);

    // calc name
    String name = "_" + node.getName();

    String getterName = "get" + StringTransformations.capitalize(node.getName());
    if (SymTypeRelations.isBoolean(typeSTE)) {
      getterName = "is" + StringTransformations.capitalize(node.getName());
    }
    String setterName = "set" + StringTransformations.capitalize(node.getName());

    // GUIViewElement or List<List< ... List<GUIViewElement> ... >>
    if (util.isViewElement(typeSTE) || util.isViewElementsCollection(typeSTE)) {
      // add attr
      ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
          CDModifier.PUBLIC.build(), "TemplateRef<any>", node.getName());
      clazz.addCDMember(attr);
      extraImports.add("import { Input } from '@angular/core';");
      glex.replaceTemplate(GUI2ComponentTemplates.ANNOTATIONS, attr,
          new StringHookPoint("@Input()"));
    }
    // Event attr
    else if (util.isEventArgument(node.getSymbol())) {
      // @Output() public myEvent: EventEmitter<MyType> = new EventEmitter<MyType>();
      Optional<SymTypeExpression> innerSTE = util.getFirstInnerType(typeSTE);
      String innerType = TypeScriptTypes.VOID.getTSType();
      if (innerSTE.isPresent()) {
        innerType = importAndPrintType(innerSTE.get());
      }
      ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
          CDModifier.PROTECTED.build(), "EventEmitter<" + innerType + ">", node.getName());
      clazz.addCDMember(attr);
      extraImports.add("import { Output } from '@angular/core';");
      glex.replaceTemplate(GUI2ComponentTemplates.ANNOTATIONS, attr,
          new StringHookPoint("@Output()"));

      extraImports.add("import { EventEmitter } from '@angular/core';");
      glex.replaceTemplate("cd2java.Value", attr,
          new StringHookPoint(" = new EventEmitter<" + innerType + ">()"));

      /*
        public getMyEvent(): Event<MyType> {
          const res = super.getMyEvent();
          this.myEvent.emit(null);
          return res;
        }
      */
      glex.replaceTemplate("umlp.Empty", attr,
          new TemplateHookPoint("umlp.gui2ts.GUI2AngularOverrideEventGetter",
              mcType, attr.getName(), getterName));
    }
    // Normal attr
    else {
      /*
        protected override _myAttr: MyType;

        public get myAttr(): MyType {
          return this.getMyAttr();
        }

        @Input()
        public set myAttr(val: MyType) {
          this.setMyAttr(val);
        }
      */
      // add attr
      ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
          CDModifier.PROTECTED.build(), mcType, name);
      clazz.addCDMember(attr);

      // attr overrides top class attr
      glex.replaceTemplate("umlp.gui2ts.TSAttribute", attr,
          new TemplateHookPoint("umlp.gui2ts.TSOverrideAttribute"));

      extraImports.add("import { Input } from '@angular/core';");
      // setter + getter
      glex.replaceTemplate("umlp.Empty", attr,
          new TemplateHookPoint("umlp.gui2ts.GUI2AngularAttrGetterSetter",
              attr.getMCType(), node.getName(), getterName, setterName));
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
  protected String className() {
    return util.getBaseClassName(gui.getGUIComponentDeclaration()) + COMPONENT_SUFFIX;
  }

  @Override
  protected void extendClass(ASTCDCompilationUnit cd) {
    String superClass = util.getBaseClassName(gui.getGUIComponentDeclaration());
    String qName = cd.getMCPackageDeclaration().getMCQualifiedName().getQName()
        + "." + superClass;
    boolean existsHw = GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qName);
    if (existsHw) {
      superClass += TopDecorator.TOP_SUFFIX;
      qName += TopDecorator.TOP_SUFFIX;
    }

    clazz.setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(superClass));
    getCD4C().addImport(clazz, qName);
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
    extraImports.add("import { ActivatedRoute } from '@angular/router';");
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
    // TODO AGe: should be added by component annotation decorator
    extraImports.add("import { Component } from '@angular/core';");
    glex.replaceTemplate("umlp.gui2ts.TSEmptyImports", clazz,
        new StringHookPoint(String.join("\n", extraImports)));
  }

}
