/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ErrorCollectorDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ErrorCollector";

  public ErrorCollectorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c, name)));
  }

  public static String getClassName(String domainClassName) {
    return domainClassName + SUFFIX;
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
    glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint("= new ArrayList<>()"));

    // Methods
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.Init", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.InitMe", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.GetInstance", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.AddErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector._AddErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.ContainsErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector._ContainsErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.RemoveErrorObserver", domainClass.getName());
    getCD4C().addMethod(collectorClass, "umlp.errorcollector._RemoveErrorObserver", domainClass.getName());

    // Add methods for domain attributes
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream().filter(a -> !a.getModifier().isDerived())
            .forEach(a -> {
              getCD4C().addMethod(collectorClass, "umlp.errorcollector.ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.errorcollector._ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.errorcollector._BuildInvalidMsg", a.getName(), domainClass.getName());
            });

    // Add methods for domain associations
    CDSymbolTables.getAssociationsInHierarchy(domainClass).stream().filter(a -> !a.getModifier().isDerived())
            .forEach(a -> {
              getCD4C().addMethod(collectorClass, "umlp.errorcollector.ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.errorcollector._ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.errorcollector._BuildInvalidMsg", a.getName(), domainClass.getName());
            });
    // Imports
    addImports(origCD, collectorClass);

    return collectorClass;
  }


  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
  }

}
