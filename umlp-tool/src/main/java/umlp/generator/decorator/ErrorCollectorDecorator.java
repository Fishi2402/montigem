/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;

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

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass, String name) {
    String className = domainClass.getName() + SUFFIX;

    ASTCDClass collectorClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(className).build();

    // Constructors

    // Attributes
    getCD4C().addAttribute(collectorClass, "protected static " + domainClass.getName() + "ErrorCollector instance;");
    // TODO:  CD4Code kann das nicht parsen "= new ArrayList<>()". Ist das erlaubt?
    getCD4C().addAttribute(collectorClass, "protected List<" + domainClass.getName() + "ErrorObserver> observers;");

    // Methods
    getCD4C().addMethod(collectorClass, "umlp.errorcollector.Init", domainClass.getName());
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
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.ArrayList");
    getCD4C().addImport(clazz, "umlp.command.CommandManager");
    getCD4C().addImport(clazz, getPackageName(origCD, COMMAND_PACKAGE)+".*");
  }

}
