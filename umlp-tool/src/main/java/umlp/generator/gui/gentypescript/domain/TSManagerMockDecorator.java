/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.ManagerMockDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.GemListAssocDecorator.getName;

public class TSManagerMockDecorator extends ManagerMockDecorator {

  protected String astcdPackage;

  protected ASTCDCompilationUnit origCD;

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSManagerMockDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass managerClass = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(name + SUFFIX)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(name + ManagerDecorator.SUFFIX))
        .build();

    // attributes
    getCD4C().addAttribute(managerClass, "static protected long freeGemId = 1001");

    // methods
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSMockConstructor", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSInit", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetFreeGemId", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSErrorNotificationScope");
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSSecretlyAdaptNextGemId");

    // getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, COMMAND_PACKAGE), name + EventSendingObserverDecorator.SUFFIX));

    List<ASTCDClass> concreteDomainClasses = getConcreteDomainClasses(origCD);
    Function<ASTCDClass, Boolean> isSingleton = TagLookUp::isSingleton;
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSClearCache",
        concreteDomainClasses.stream().filter(x -> !isSingleton.apply(x)).collect(Collectors.toList()),
        concreteDomainClasses.stream().filter(isSingleton::apply).collect(Collectors.toList()));


    getNonDerivedDomainClasses(origCD).forEach(c -> addClassElements(origCD, c, managerClass, name));

    getNonDerivedDomainInterfaces(origCD).forEach(i -> addInterfaceElements(origCD, i, managerClass));

    // Imports
    addImports(managerClass);

    return managerClass;
  }

  protected void addClassElements(ASTCDCompilationUnit origCD, ASTCDClass domainClass,
      ASTCDClass managerClass, String managerName) {
    String name = domainClass.getName();
    TSImportHelper importHelper = new TSImportHelper(glex, domainClass, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + domainClass.getName();

    if (domainClass.getModifier().isDerived()) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSGetDerived", name);
      return;
    }
    String lName = StringTransformations.uncapitalize(name);
    boolean isSingleton = TagLookUp.isSingleton(domainClass);

    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociations(domainClass);
    Set<ASTCDType> concreteSubclassesInclSelf = UMLPSymbolTables.collectConcreteSubclassesInclSelf(origCD, domainClass);
    if (isSingleton) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoOptSingletonElement", name);
    }
    else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoOpt", name, concreteSubclassesInclSelf);
    }
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetSure", name);
    if (!isSingleton) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSIsLoaded", name, concreteSubclassesInclSelf);
    }
    importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"), importerQName);

    if (isSingleton) {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetSingletonElement", name);
    }


    if (!domainClass.getModifier().isAbstract()) {
      if (isSingleton) {
        getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetListSingletonElement", name);
      }
      else {
        getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetList", name, concreteSubclassesInclSelf);
      }

      if (isSingleton) {
        addAttribute(managerClass, "protected Optional<" + name + "> " + lName + "Instance", "null");
      } else {
        addAttribute(managerClass, "protected Map<number, " + name + "> " + lName + "s", "new Map()");
      }
      addAttribute(managerClass, "protected GemList<" + name + "> " + lName + "GemList", "new " + name + "GemList([]).build()");
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoBuilderParam", name);
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoDeSerBuilder", name);
      if (!isSingleton) {
        getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoAdd", name, managerName,
            assocs.stream()
                .filter(GenService::isBidirectional)
                .filter(Predicate.not(GenService::isSymbolDerived))
                .collect(Collectors.toList()));
        getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoDelete", name, managerName);
      }
      // getCD4C().addImport(managerClass, Joiners.DOT.join(getPackageName(origCD, CLIENT_PACKAGE), domainClass.getName() + CommandSendingObserverDecorator.SUFFIX));
    }
    else {
      getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetListAbstract", name);
    }

    // Associations
    assocs.stream()
        .filter(GenService::isList)
        .filter(Predicate.not(GenService::isSymbolDerived))
        .forEach(a -> {
          SymTypeExpression type = a.getSymbol().getType();
          String printedType = type.print();
          ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

          importHelper.addImport(getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType, importerQName);

          getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoCreateAssoc", getAssocName(domainClass, a), printedType);
          getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoBuilderAssoc", getAssocName(domainClass, a), printedType);
          importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)), importerQName);
        });
  }

  protected void addInterfaceElements(ASTCDCompilationUnit origCD, ASTCDInterface domainClass,
      ASTCDClass managerClass) {
    String name = domainClass.getName();
    String lName = StringTransformations.uncapitalize(name);

    TSImportHelper importHelper = new TSImportHelper(glex, domainClass, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + domainClass.getName();

    addAttribute(managerClass, "protected Map<number, " + name + "> " + lName + "s", "new Map()");
    addAttribute(managerClass, "protected GemList<" + name + "> " + lName + "GemList", "new " + name + "GemList([]).build()");

    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociations(domainClass);
    Set<ASTCDType> concreteSubclassesInclSelf = UMLPSymbolTables.collectConcreteSubclassesInclSelf(origCD, domainClass);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoOpt", name, concreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetSure", name);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoGetList", name, concreteSubclassesInclSelf);
    getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSIsLoaded", name, concreteSubclassesInclSelf);

    // Imports
    importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), domainClass.getName() + "GemList"), importerQName);

    // Associations
    assocs.stream()
        .filter(GenService::isList)
        .filter(Predicate.not(GenService::isSymbolDerived))
        .forEach(a -> {
          SymTypeExpression type = a.getSymbol().getType();
          String printedType = type.print();
          ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor((ASTCDType) type.getTypeInfo().getAstNode());

          importHelper.addImport(getPackageName(cd, DEFAULT_PACKAGE) + "." + printedType, importerQName);

          getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoCreateAssoc", getAssocName(domainClass, a), printedType);
          getCD4C().addMethod(managerClass, "umlp.gui2ts.managermock.TSDoBuilderAssoc", getAssocName(domainClass, a), printedType);
          importHelper.addImport(Joiners.DOT.join(getPackageName(origCD, GEMLIST_PACKAGE), getName(domainClass, a)), importerQName);
        });
  }

  protected void addImports(ASTCDClass clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));

    TSImportHelper importHelper = new TSImportHelper(glex, clazz, hwPath, outPath);
    String importerQName = this.astcdPackage + "." + clazz.getName();

    String name = origCD.getCDDefinition().getName();
    importHelper.addImport(this.astcdPackage + "."
        + name + ManagerDecorator.SUFFIX, importerQName);
    importHelper.addImport(this.astcdPackage + "."
        + name + BaseClassObjectMapperDecorator.SUFFIX, importerQName);
    importHelper.addImport(this.astcdPackage + "."
        + name + CommandObjectMapperDecorator.SUFFIX, importerQName);
    importHelper.addImport(this.astcdPackage + "."
        + name + ObserverDecorator.SUFFIX, importerQName);

    ArrayList<ASTCDCompilationUnit> cdDefs = new ArrayList<>();
    cdDefs.add(origCD);
    cdDefs.addAll(getExtendedDefinitions(origCD));

    for (ASTCDCompilationUnit cdDef : cdDefs) {
      for (ASTCDClass c : cdDef.getCDDefinition().getCDClassesList()) {
        importHelper.addImport(getPackageName(cdDef, DEFAULT_PACKAGE) + "." + c.getName(), importerQName);
        if (!c.getModifier().isDerived() && !c.getModifier().isAbstract()) {
          importHelper.addImport(getPackageName(cdDef, DEFAULT_PACKAGE) + "."
              + c.getName() + BuilderDecorator.BUILDER_SUFFIX, importerQName);
          importHelper.addImport(getPackageName(cdDef, DEFAULT_PACKAGE) + "."
              + c.getName() + DeSerBuilderDecorator.DESER_BUILDER_SUFFIX, importerQName);
        }
      }
      for (ASTCDEnum c : cdDef.getCDDefinition().getCDEnumsList()) {
        importHelper.addImport(getPackageName(cdDef, DEFAULT_PACKAGE) + "."
            + c.getName(), importerQName);
      }
      for (ASTCDInterface c : cdDef.getCDDefinition().getCDInterfacesList()) {
        importHelper.addImport(getPackageName(cdDef, DEFAULT_PACKAGE) + "."
            + c.getName(), importerQName);
      }
    }
  }
}
