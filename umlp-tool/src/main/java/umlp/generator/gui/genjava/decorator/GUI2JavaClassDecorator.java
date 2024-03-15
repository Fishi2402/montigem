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
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;

import java.util.Collection;
import java.util.HashSet;

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
    // calc type
    if (!node.isPresentSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + node.getName()
          + "' at " + node.get_SourcePositionStart().toString()
          + " - " + node.get_SourcePositionEnd().toString()
      );
    }
    SymTypeExpression typeSTE = node.getSymbol().getType();
    String type = importAndPrintType(typeSTE);

    // calc name
    String name = node.getName();

    // add attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), type, name);
    clazz.addCDMember(attr);

    // add getter and setter
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    clazz.addAllCDMembers(methodDecorator.decorate(attr));

    if (util.getMainDomainModelSimpleName().isPresent()) {
      String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());
      // add attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDomainTemplate(),
          attr, manager
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitBuilderCallTemplate(), attrInit)
      );

      // add builder init
      ASTCDMethodSignature builderInit = getCD4C().addMethod(
          clazz, getInitBuilderTemplate(),
          attr, manager
      );

      // add builder init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitBuilderCallTemplate(), builderInit)
      );
    }
    else {
      Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
    }
  }

  @Override
  public void visit(ASTGUINamedArg node) {
    // calc type
    if (!node.isPresentNameSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + node.getName()
          + "' at " + node.get_SourcePositionStart().toString()
          + " - " + node.get_SourcePositionEnd().toString()
      );
    }
    SymTypeExpression typeSTE = node.getNameSymbol().getType();
    String type = importAndPrintType(typeSTE);

    // calc name
    String name = node.getName();

    // add getter and setter
    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));

    // replace field access with getters
    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, iterateItems
    );
    Expression2SetterPrinter setterPrinter = new Expression2MutatorPrinter(
        new IndentPrinter(), false, iterateItems, iterateItems
    );
    Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

    String nullCheck = nullCheckPrinter.prettyprint(node.getGUIArgValue());
    String getterValue = getterPrinter.prettyprint(node.getGUIArgValue());
    String setterValue = setterPrinter.prettyprint(node.getGUIArgValue());

    // add getter
    getCD4C().addMethod(
        clazz, getArgGetterTemplate(),
        type, name, nullCheck, getterValue
    );

    // add setter
    getCD4C().addMethod(
        clazz, getArgSetterTemplate(),
        type, name, nullCheck, setterValue
    );
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

  protected String importAndPrintType(SymTypeExpression ste) {
    if (ste.isObjectType()) {
      // given in GUI:
      // import hardy.Domain.Person;
      // the resolved type is `hardy.Person`
      // but the CD Generator produces `hardy.domain.Person`, so calculate that instead
      IBasicSymbolsScope scope = ste.getTypeInfo().getEnclosingScope();
      if (scope.isPresentName()
          && util.getMainDomainModelSimpleName().isPresent()
          && scope.getName().equals(util.getMainDomainModelSimpleName().get())) {

        String insert = StringTransformations.uncapitalize(scope.getName()) + ".";
        String qTypeName = ste.asObjectType().printFullName();

        int lastIndex = qTypeName.lastIndexOf('.');

        if (lastIndex != -1) {
          StringBuilder sb = new StringBuilder(qTypeName);
          sb.insert(lastIndex + 1, insert);
          qTypeName = sb.toString();
        }
        else {
          qTypeName = insert + qTypeName;
        }
        getCD4C().addImport(clazz, qTypeName);
      }
      else {
        getCD4C().addImport(clazz, ste.printFullName());
      }
    }
    else if (ste.isGenericType()) {
      SymTypeOfGenerics stog = ste.asGenericType();

      getCD4C().addImport(clazz, stog.getTypeConstructorFullName());

      for (SymTypeExpression steArg : stog.getArgumentList()) {
        importAndPrintType(steArg);
      }
    }
    else if (ste.isPrimitive()) {
      return ste.asPrimitive().getBoxedPrimitiveName();
    }
    else if (!(ste.isNullType() || ste.isVoidType() || ste.isWildcard())) {
      Log.error("0x00010 Encountered unsupported type in " + gui.getComponentQName()
          + ": parameter type '" + ste.printFullName() + "'");
    }
    return ste.print();
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

  protected String getInitBodyHookpoint() {
    return "umlp.gui2java.GUI2JavaInitBody";
  }

  protected String getArgGetterTemplate() {
    return "umlp.gui2java.GUI2JavaArgGetter";
  }

  protected String getArgSetterTemplate() {
    return "umlp.gui2java.GUI2JavaArgSetter";
  }

}
