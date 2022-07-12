/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.GenService.createInitialValue;

public class ErrorCollectorDecorator extends AbstractDecorator {

  public static final String SUFFIX = "ErrorCollector";

  public ErrorCollectorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
            .forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
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

    CDSymbolTables.getAttributesInHierarchy(domainClass).stream().filter(a -> !a.getModifier().isDerived())
            .forEach(a -> {
              getCD4C().addMethod(collectorClass, "umlp.errorcollector.ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.errorcollector._ReportInvalid", a.getName(), domainClass.getName());
              getCD4C().addMethod(collectorClass, "umlp.errorcollector._BuildInvalidMsg", a.getName(), domainClass.getName());
              getCD4C().addImport(collectorClass, "umlp.commands."+domainClass.getName()+"_error_"+a.getName());
            });
    // Imports
    addImports(collectorClass);

    return collectorClass;
  }


  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.ArrayList");
    getCD4C().addImport(clazz, "umlp.command.CommandManager");
    getCD4C().addImport(clazz, "umlp.commands.*");
  }

}
