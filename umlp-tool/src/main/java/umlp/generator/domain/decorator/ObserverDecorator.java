/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GenService.createType;

public class ObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "Observer";

  private Collection<GenerationSummary> generationDetails = List.of();

  public ObserverDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.generationDetails = new ArrayList<>();

    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(ObserverDecorator::shouldCreateObserver)
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createInterface(c))));

    origCD.getCDDefinition().getCDInterfacesList().stream().filter(ObserverDecorator::shouldCreateObserver)
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createInterface(c))));
  }

  public static boolean shouldCreateObserver(ASTCDType c) {
    return !c.getModifier().isDerived();
  }

  protected ASTCDInterface createInterface(ASTCDType domainClass) {
    String className = domainClass.getName();
    ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(className + SUFFIX).build();
    List<SymTypeExpression> superTypes = domainClass.getSymbol().getSuperTypesList();
    if (!superTypes.isEmpty()) {
      ast.setCDExtendUsage(getExtendUsageFacade()
          .createCDExtendUsage(superTypes.stream().map(a -> a.print() + SUFFIX).toArray(String[]::new)));
    }
    GenerationSummary generationSummary = new GenerationSummary(domainClass,
                                                                new ArrayList<>(),
                                                                new ArrayList<>());
    this.generationDetails.add(generationSummary);
    var mem = new Object() {
      void add(ASTCDMethodSignature signature) {
        generationSummary.notifyMethodSignatures().add(signature);
      }
    };

    // Add Notify for attributes
    domainClass.getCDAttributeList().stream()
        .filter(a -> !a.getModifier().isDerived())
        .forEach(a -> {
          if (GenService.isList(a)) {
            String innerType = GenService.getInnerType(a.getMCType());
            mem.add(getCD4C().addMethod(ast, "umlp.observer.NotifyAdd", a.getName(), className, innerType));
            mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyAdd", a.getName(), className, innerType));
            mem.add(getCD4C().addMethod(ast, "umlp.observer.NotifyRemove", a.getName(), className, innerType));
            mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyRemove", a.getName(), className, innerType));
          }

          String fullType = getCDPrinter().prettyprint(createType(a));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, fullType, false));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, fullType, false));
        });

    // Add Notify for list associations
    CDSymbolTables.getAssociations(domainClass).stream()
        .filter(Predicate.not(GenService::isSymbolDerived))
        .filter(GenService::isList)
        .forEach(a -> {
          mem.add(getCD4C().addMethod(ast, "umlp.observer.NotifyAdd", a.getName(), className, "long"));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyAdd", a.getName(), className, "long"));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.NotifyRemove", a.getName(), className, "long"));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyRemove", a.getName(), className, "long"));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, "long", true));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, "long", true));
        });

    // Add Notify for associations
    CDSymbolTables.getAssociations(domainClass).stream()
        .filter(Predicate.not(GenService::isSymbolDerived))
        .filter(Predicate.not(GenService::isList))
        .forEach(a -> {
          mem.add(getCD4C().addMethod(ast, "umlp.observer.Notify", a.getName(), className, "long", false));
          mem.add(getCD4C().addMethod(ast, "umlp.observer.MaybeNotify", a.getName(), className, "long", false));
        });

    getCD4C().addMethod(ast, "umlp.observer.GetScope");
    getCD4C().addMethod(ast, "umlp.observer.NotifyBuild", className);
    getCD4C().addMethod(ast, "umlp.observer.MaybeNotifyBuild", className);

    generationSummary
        .importStatements().add(getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("NotificationScope")));
    addImportForImportedObservers(domainClass, ast, generationSummary);
    super.addImports(domainClass, ast);

    return ast;
  }

  private void addImportForImportedObservers(ASTCDType domainClass, ASTCDType observerType, GenerationSummary generationSummary) {
    CDTypeSymbol symbol = domainClass.getSymbol();
    if (symbol.isPresentSuperClass()) {
      SymTypeExpression superclass = symbol.getSuperClass();
      ASTCDCompilationUnit scCD = UMLPSymbolTables.getDiagramFor((ASTCDType) superclass.getTypeInfo().getAstNode());
      generationSummary
          .importStatements().add(getCD4C().addImport(observerType, getPackageName(scCD, DEFAULT_PACKAGE) + "." + superclass.print() + "Observer"));
    }
  }

  public Collection<GenerationSummary> getGenerationSummaries() {
    return generationDetails;
  }

  //compiler can't work with records
  public static final class GenerationSummary {

    private final ASTCDType domainClass;
    private final Collection<ASTCDMethodSignature> notifyMethodSignatures;
    private final Collection<ASTMCImportStatement> importStatements;

    public GenerationSummary(ASTCDType domainClass,
                             Collection<ASTCDMethodSignature> notifyMethodSignatures,
                             Collection<ASTMCImportStatement> importStatements) {
      this.domainClass = domainClass;
      this.notifyMethodSignatures = notifyMethodSignatures;
      this.importStatements = importStatements;
    }

    public ASTCDType domainClass() {
      return domainClass;
    }

    public Collection<ASTCDMethodSignature> notifyMethodSignatures() {
      return notifyMethodSignatures;
    }

    public Collection<ASTMCImportStatement> importStatements() {
      return importStatements;
    }
  }
}
