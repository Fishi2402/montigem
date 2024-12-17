/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericTypeBuilder;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.GUI2ClassDecorator;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.java2ts.GUI2ComponentTemplates;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.TagLookUp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GUI2AngularClassDecorator extends GUI2ClassDecorator implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  public static final String COMPONENT_SUFFIX = "Component";
  protected final MCPath hwPath;
  protected final Path outPath;
  protected TSImportHelper importHelper;

  public GUI2AngularClassDecorator(final GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    this.hwPath = hwPath;
    this.outPath = outPath;
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
  }

  @Override
  protected void init() {
    importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
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
          CDModifier.PUBLIC.build(), createTypeAndAddImports(typeSTE), name);
      clazz.addCDMember(attr);
      addImportString("import { Input } from '@angular/core';");
      glex.replaceTemplate(GUI2ComponentTemplates.ANNOTATIONS, attr,
          new StringHookPoint("@Input()"));

      glex.replaceTemplate("umlp.Empty", attr,
          new TemplateHookPoint("umlp.gui2ts.GUI2AngularAttrSimpleGetterSetter",
              mcType, node.getName(), name));
    }
    // Event attr
    else if (util.isEventArgument(node.getSymbol())) {
      // @Output() public myEvent: EventEmitter<MyType> = new EventEmitter<MyType>();
      Optional<SymTypeExpression> innerSTE = util.getFirstInnerType(typeSTE);
      String innerType = TypeScriptTypes.VOID.getTSType();
      if (innerSTE.isPresent()) {
        innerType = new TSUtilsPrinter().printType(createTypeAndAddImports(innerSTE.get()));
      }
      ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
          CDModifier.PUBLIC.build(), "EventEmitter<" + innerType + ">", node.getName());
      clazz.addCDMember(attr);
      addImportString("import { Output } from '@angular/core';");
      glex.replaceTemplate(GUI2ComponentTemplates.ANNOTATIONS, attr,
          new StringHookPoint("@Output()"));

      addImportString("import { EventEmitter } from '@angular/core';");
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
          CDModifier.PUBLIC.build(), mcType, name);
      clazz.addCDMember(attr);

      if (util.isDomainType(typeSTE)
          && !util.isEnumType(typeSTE)
          && !TagLookUp.isSingleton(typeSTE.getTypeInfo())
          && !TagLookUp.isDerived(typeSTE.getTypeInfo())
      ) {
        // add attr id init
        glex.addAfterTemplate(
            "umlp.gui2ts.GUI2AngularInitBody",
            new TemplateHookPoint("umlp.gui2ts.GUI2AngularInitId", util.getRoutingId(node))
        );
      }

      // attr overrides top class attr
      glex.replaceTemplate("umlp.gui2ts.TSAttribute", attr,
          new TemplateHookPoint("umlp.gui2ts.TSOverrideAttribute"));

      addImportString("import { Input } from '@angular/core';");
      // setter + getter
      if (UMLPGeneratorConfig.getInstance().isUseJsweet()) {
//        addImport(PathUtil.getBackendRTECommonPath("UMLPArrayList"));
        mcType = createTypeAndAddImports(typeSTE);
      }
      glex.replaceTemplate("umlp.Empty", attr,
          new TemplateHookPoint("umlp.gui2ts.GUI2AngularAttrGetterSetter",
              mcType, node.getName(), getterName, setterName));
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
    addImport(qName);
  }

  @Override
  protected void addRouter() {
    if (TypeScriptTypes.ROUTER.getJavaQName().isPresent()) {
      addImport(TypeScriptTypes.ROUTER.getJavaQName().get());
    }
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "Router", "_router");
    clazz.addCDMember(attr);
  }

  @Override
  protected void addConstructor() {
    addImportString("import { inject } from '@angular/core';");
    glex.replaceTemplate("umlp.gui2ts.TSConstructor", clazz,
        new TemplateHookPoint("umlp.gui2ts.GUI2AngularConstructor"));
  }


  protected void addRoute() {
    addImportString("import { ActivatedRoute } from '@angular/router';");
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "ActivatedRoute", "_route");

    clazz.addCDMember(attr);
  }

  protected void addSubscriptions() {
    addImportString("import { Subscription } from 'rxjs';");
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "Subscription[]", "_subscriptions");

    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(" = []"));
    clazz.addCDMember(attr);
  }

  @Override
  protected void addInit() {
    getCD4C().addMethod(clazz, "umlp.gui2ts.GUI2AngularInit", clazz.getName());
  }

  @Override
  protected void addDestroy() {
    getCD4C().addMethod(clazz, "umlp.gui2ts.GUI2AngularDestroy", clazz.getName());
  }

  protected void removeObserver() {
  }

  @Override
  protected ASTMCType createTypeAndAddImports(SymTypeExpression ste) {
    if (!ste.hasTypeInfo()) {
      Log.error("0x0001B Encountered a SymTypeExpression disconnected from a type symbol: "
          + ste.printFullName());
    }
    if (ste.isObjectType()) {
      // given in GUI:
      // import hardy.Domain.Person;
      // the resolved type is `hardy.Person`
      // but the CD Generator produces `hardy.domain.Person`, so calculate that instead
      IBasicSymbolsScope scope = ste.getTypeInfo().getEnclosingScope();
      if (scope.isPresentName()
          && util.getMainDomainModelSimpleName().isPresent()
          && scope.getName().equals(util.getMainDomainModelSimpleName().get())) {

        String qType = ste.asObjectType().printFullName();
        String type = ste.asObjectType().print();

        List<String> parts = new ArrayList<>(Splitters.DOT.splitToList(qType));
        if (parts.size() > 1) {
          String domainPackage = parts.get(parts.size() - 2).toLowerCase();
          parts.set(parts.size() - 2, domainPackage);
          qType = Joiners.DOT.join(parts);
        }
        addImport(qType);
        return MCTypeFacade.getInstance().createQualifiedType(type);
      }
      else {
        addImport(ste.printFullName());
        return MCTypeFacade.getInstance().createQualifiedType(ste.print());
      }
    }
    else if (ste.isGenericType()) {
      SymTypeOfGenerics stog = ste.asGenericType();
      String qType = stog.getTypeConstructorFullName();
      String type = stog.getTypeInfo().getName();
      if (qType.equals(BuiltInTypes.EVENT.getQName())) {
        qType = TypeScriptTypes.CONSUMER.getJavaQName().get();
        type = TypeScriptTypes.CONSUMER.getMCType();
      }

      addImport(qType);

      ASTMCBasicGenericTypeBuilder genTypeBuilder = MCFullGenericTypesMill.mCBasicGenericTypeBuilder()
          .setNamesList(new ArrayList<>(Arrays.asList(type)))
          .setMCTypeArgumentsList(new ArrayList<>());
      for (SymTypeExpression steArg : stog.getArgumentList()) {
        genTypeBuilder.addMCTypeArgument(
            MCSimpleGenericTypesMill.mCCustomTypeArgumentBuilder()
                .setMCType(createTypeAndAddImports(steArg))
                .build());
      }
      return genTypeBuilder.build();
    }
    else if (ste.isPrimitive()) {
      return MCTypeFacade.getInstance().createQualifiedType(
          ste.asPrimitive().getBoxedPrimitiveName());
    }
    else if (!(ste.isNullType() || ste.isVoidType() || ste.isWildcard() || ste.isTypeVariable())) {
      Log.error("0x00010 Encountered unsupported type in a parameter: '"
          + ste.printFullName() + "'");
    }
    return MCTypeFacade.getInstance().createQualifiedType(ste.printFullName());
  }

  @Override
  protected void addImport(String qType) {
    importHelper.addImport(qType, gui.getComponentQName());
  }

  protected void addImportString(String importStr) {
    importHelper.addImportString(importStr);
  }
}
