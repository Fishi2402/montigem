/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ErrorObserverDecorator extends AbstractDecorator {

    public static final String SUFFIX = "ErrorObserver";

    public ErrorObserverDecorator(final GlobalExtensionManagement glex) {
        super(glex);
    }

    @Override
    public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
        ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
        astcdPackage.addCDElement(createInterface(origCD));
    }

    protected ASTCDInterface createInterface(ASTCDCompilationUnit origCD) {
        String name = origCD.getCDDefinition().getName() + SUFFIX;

        ASTCDInterface ast = UMLPMill.cDInterfaceBuilder()
                .setModifier(PUBLIC.build())
                .setName(name).build();

        origCD.getCDDefinition().getCDClassesList().stream()
                .filter(c -> !c.getModifier().isDerived())
                .forEach(c ->
                    getCD4C().addMethod(ast, "umlp.errorobserver.NotifyInvalid", c.getName()));

        return ast; //collectorInterface;
    }

}
