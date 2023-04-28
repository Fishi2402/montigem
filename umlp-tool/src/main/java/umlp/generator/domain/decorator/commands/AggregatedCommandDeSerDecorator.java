/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class AggregatedCommandDeSerDecorator extends AbstractDecorator {
  public AggregatedCommandDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createDeSer(origCD, decoratedCD));
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    String className = origCD.getCDDefinition().getName() + "Command" + DE_SER;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("AggregatedCommandDeSer"))
        .setName(className)
        .build();

    List<ASTCDClass> domainClasses = origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived()).collect(Collectors.toList());
    List<String> commandList = new ArrayList<>();
    String packageName = getPackageName(origCD, COMMAND_PACKAGE) + ".";
    for (ASTCDClass domainClass : domainClasses) {
      String domainClassName = domainClass.getName();
      commandList.add(packageName + domainClassName + GetDecorator.SUFFIX);
      commandList.add(packageName + domainClassName + GetFilteredDecorator.SUFFIX);
      commandList.addAll(calcAssocDeSers(packageName, domainClass));

      if (!domainClass.getModifier().isAbstract()) {
        commandList.add(packageName + domainClassName + CreateDecorator.SUFFIX);
        commandList.add(packageName + domainClassName + DeleteDecorator.SUFFIX);

        CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
            .filter(a -> !GenService.isDerived(a))
            .forEach(domainAttribute -> {
              String domainAttributeName = StringTransformations.capitalize(domainAttribute.getName());
              if (GenService.isList(domainAttribute)) {
                commandList.add(packageName + domainClassName + AddDecorator.SUFFIX + domainAttributeName);
                commandList.add(packageName + domainClassName + RemoveDecorator.SUFFIX + domainAttributeName);
              } else {
                commandList.add(packageName + domainClassName + SetDecorator.SUFFIX + domainAttributeName);
              }
            });
      }

      // Derived
      CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
          .filter(GenService::isDerived).forEach(domainAttribute -> {
            String domainAttributeName = StringTransformations.capitalize(domainAttribute.getName());
            commandList.add(packageName + domainClassName + CalculateDecorator.SUFFIX + domainAttributeName);
          });
      CDSymbolTables.getAssociations(domainClass).stream()
          .filter(GenService::isDerived)
          .filter(a -> !GenService.isBidirectional(a)).forEach(association -> {
            String roleName = StringTransformations.capitalize(association.getName());
            commandList.add(packageName + domainClassName + CalculateDecorator.SUFFIX + roleName);
          });
    }

    origCD.getCDDefinition().getCDInterfacesList().stream()
        .filter(c -> !c.getSymbol().isIsDerived())
        .forEach(i -> {
          commandList.add(packageName + i.getName() + GetDecorator.SUFFIX);
          commandList.addAll(calcAssocDeSers(packageName, i));
        });

    getCD4C().addConstructor(ast, "umlp.commands.aggregatedcommanddeser.Constructor", commandList, className, getPackageName(origCD, DEFAULT_PACKAGE));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("AggregatedCommandDeSer"));

    return ast;
  }

  private static List<String> calcAssocDeSers(String packageName, ASTCDType domainType) {
    String name = domainType.getName();
    List<String> commandList = new ArrayList<>();
    CDSymbolTables.getAssociations(domainType).stream()
        .filter(a -> !GenService.isDerived(a))
        .filter(a -> !GenService.isBidirectional(a)).forEach(association -> {
          String roleName = StringTransformations.capitalize(association.getName());
          if (GenService.isList(association)) {
            commandList.add(packageName + name + AddDecorator.SUFFIX + roleName);
            commandList.add(packageName + name + RemoveDecorator.SUFFIX + roleName);
          } else {
            commandList.add(packageName + name + SetDecorator.SUFFIX + roleName);
          }
        });

    return commandList;
  }

}
