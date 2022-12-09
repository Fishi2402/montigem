package umlp.generator.decorator.commands;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.decorator.AbstractDecorator;
import umlp.generator.decorator.GenService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.commands.CommandDecorator.DE_SER;

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
        List<String> domainClassesString = domainClasses.stream().map(ASTCDClass::getName).collect(Collectors.toList());
        List<String> commandList = new ArrayList<>();
        String packageName = getPackageName(origCD , COMMAND_PACKAGE)+ ".";
        for (String domainClass : domainClassesString) {
            commandList.add(packageName + domainClass + CreateDecorator.SUFFIX);
            commandList.add(packageName + domainClass + DeleteDecorator.SUFFIX);
            commandList.add(packageName + domainClass + GetDecorator.SUFFIX);
            commandList.add(packageName + domainClass + GetFilteredDecorator.SUFFIX);
        }
        List<ASTCDAttribute> domainAttributes = domainClasses.stream().map(CDSymbolTables :: getAttributesInHierarchy).flatMap(Collection::stream).collect(Collectors.toList());


        getCD4C().addConstructor(ast, "umlp.commands.aggregatedcommanddeser.Constructor", commandList, className, getPackageName(origCD, DEFAULT_PACKAGE));
        getCD4C().addImport(ast, "umlp.rte.AggregatedCommandDeSer");
        getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+".*");


        return ast;
    }

}
