/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.ErrorObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSErrorCollectorDecorator extends ErrorCollectorDecorator {

  protected String astcdPackage;

  public TSErrorCollectorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    super.transform(origCD, decoratedCD);
  }

  protected void addImports(ASTCDClass domainClass, ASTCDType clazz) {
    // getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
    getCD4C().addImport(clazz, this.astcdPackage + "." + domainClass.getName());
    getCD4C().addImport(clazz, this.astcdPackage + "." + domainClass.getName() + ErrorObserverDecorator.SUFFIX);
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String name) {
    String className = getClassName(domainClass.getName());

    ASTCDClass collectorClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className).build();

    // Constructors

    // Attributes
    getCD4C().addAttribute(collectorClass, "protected static " + domainClass.getName() + "ErrorCollector instance;");
    // TODO:  CD4Code kann das nicht parsen "= new ArrayList<>()". Ursache ist die fehlende Super-Grammatik
    // JavaClassExpression
    ASTCDAttribute attr = getCD4C().addAttribute(collectorClass, "protected List<" + domainClass.getName() + "ErrorObserver> observers;");
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= []"));

    // Methods
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSInit", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSInitMe", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSGetInstance", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSAddErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_AddErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSContainsErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_ContainsErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSRemoveErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_RemoveErrorObserver", domainClass.getName());

    // Add methods for domain attributes
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream().filter(a -> !a.getModifier().isDerived())
            .forEach(a -> {
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSReportInvalidWithMessage", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_BuildInvalidMsg", a.getName(), domainClass.getName());
            });
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSReportInvalidWithMessage", "thisObjectRelated", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_ReportInvalid", "thisObjectRelated", domainClass.getName());

    // Add methods for domain associations
    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream().filter(a -> !a.getSymbol().getAssoc().getAssociation().getAstNode().getModifier().isDerived())
            .forEach(a -> {
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TSReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.gui2ts.errorcollector.TS_BuildInvalidMsg", a.getName(), domainClass.getName());
            });
    // Imports
    addImports(domainClass, collectorClass);

    return collectorClass;
  }

}
