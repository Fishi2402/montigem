/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.PathUtil;

import java.util.Iterator;

public abstract class GUI2ClassDecorator extends AbstractDecorator {

  protected ASTCDClass clazz;
  protected ASTGUICompilationUnit gui;

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  public GUI2ClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {};

  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    this.gui = gui;
    replaceTemplates();

    // package
    ASTCDPackage cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(gui.getMCPackageDeclaration().getMCQualifiedName())
        .build();
    cd.getCDDefinition().addCDElement(cdPackage);

    // class
    clazz = emptyClass(gui);
    extendClass(cd);

    cdPackage.addCDElement(withOrigin(clazz));
    // directive
    addClassAnnotation();
  }

  protected void replaceTemplates() {
  }

  /**
   * @param ast base GUI AST to create an empty class from
   * @return empty class
   */
  protected ASTCDClass emptyClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    String className = domainClass.getName();
    return UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .build();
  }

  protected void extendClass(ASTCDCompilationUnit cd) {
  }

  protected void addClassAnnotation() {
  }

  protected void addRouter() {
  }

  protected void addConstructor() {
  }

  protected void addInit() {
  }

  /**
   * Transform a GUI type into a Java type and add all necessary imports for its usage. For example,
   * `List<Person>` is transformed into `GemList<Person>` and imports `umlp.backendrte.GemList`,
   * `my.domain.Person` are added along the way.
   *
   * @param ste GUI type to be processed
   * @return Java type that corresponds to the input GUI type
   */
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

        String domain = StringTransformations.uncapitalize(scope.getName());
        String qType = ste.asObjectType().printFullName();
        getCD4C().addImport(clazz, insertDomain2QType(qType, domain));
      }
      else {
        getCD4C().addImport(clazz, ste.printFullName());
      }
    }
    else if (ste.isGenericType()) {
      SymTypeOfGenerics stog = ste.asGenericType();
      // a List<Person> must be printed as GemList<Person>
      // a List<String> as a List<String>
      // a List<MyDomainEnum> as a List<MyDomainEnum>
      if (util.getDomainListInnerType(ste).isPresent()
          && !util.isEnumType(util.getDomainListInnerType(ste).get())) {
        getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
        StringBuilder sb = new StringBuilder("GemList");
        sb.append("<");
        Iterator<SymTypeExpression> steArgIter = stog.getArgumentList().iterator();
        while (steArgIter.hasNext()) {
          sb.append(importAndPrintType(steArgIter.next()));
          if (steArgIter.hasNext()) {
            sb.append(",");
          }
        }
        sb.append(">");
        return sb.toString();
      }
      getCD4C().addImport(clazz, stog.getTypeConstructorFullName());
      for (SymTypeExpression steArg : stog.getArgumentList()) {
        importAndPrintType(steArg);
      }
    }
    else if (ste.isPrimitive()) {
      return ste.asPrimitive().getBoxedPrimitiveName();
    }
    else if (!(ste.isNullType() || ste.isVoidType() || ste.isWildcard() || ste.isTypeVariable())) {
      Log.error("0x00010 Encountered unsupported type in " + gui.getComponentQName()
          + ": parameter type '" + ste.printFullName() + "'");
    }
    return ste.print();
  }

  protected String getAndImportManager() {
    if (util.getMainDomainModelSimpleName().isEmpty() || util.getMainDomainModelQName().isEmpty()) {
      Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
    }
    // We want to import java generated somepackage.flensburg.FlensburgManager;
    // but the qualified name of domain is actually somepackage.Flensburg
    // append Manager and insert lowercase cd name (e.g., flensburg) to get the result
    String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());
    String qManager = ManagerDecorator.getClassName(util.getMainDomainModelQName().get());
    String domain = StringTransformations.uncapitalize(util.getMainDomainModelSimpleName().get());

    getCD4C().addImport(clazz, insertDomain2QType(qManager, domain));
    return manager;
  }

  protected String getAndImportDomainObserver() {
    if (util.getMainDomainModelSimpleName().isEmpty() || util.getMainDomainModelQName().isEmpty()) {
      Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
    }
    String observer = DiagramObserverDecorator.PREFIX
        + util.getMainDomainModelSimpleName().get()
        + DiagramObserverDecorator.SUFFIX;
    String qObserver = observer;
    int lastDotIdx = util.getMainDomainModelQName().get().lastIndexOf(".");
    if (lastDotIdx > 0) {
      qObserver = util.getMainDomainModelQName().get().substring(0, lastDotIdx) + "." + observer;
    }
    String domain = StringTransformations.uncapitalize(util.getMainDomainModelSimpleName().get());

    getCD4C().addImport(clazz, insertDomain2QType(qObserver, domain));
    return observer;
  }

  protected String insertDomain2QType(String qType, String domain) {
    String insert = domain + ".";
    String res = qType;
    int lastIndex = res.lastIndexOf('.');

    if (lastIndex != -1) {
      StringBuilder sb = new StringBuilder(res);
      sb.insert(lastIndex + 1, insert);
      res = sb.toString();
    }
    else {
      res = insert + res;
    }
    return res;
  }

}