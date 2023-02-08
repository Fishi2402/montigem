/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.requests;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.ManagerDecorator.SUFFIX;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class GetFreeGemIdRequestDecorator extends AbstractDecorator {

    public static final String CLASS_NAME = "GetFreeGemIdRequest";

    public GetFreeGemIdRequestDecorator(GlobalExtensionManagement glex) {
        super(glex);
    }

    @Override
    public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
        ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, REQUESTS_PACKAGE);
        astcdPackage.addCDElement(createClass(origCD, astcdPackage.getName()));
        astcdPackage.addCDElement(createDeSer(origCD, astcdPackage.getName()));

    }

    protected ASTCDClass createClass(ASTCDCompilationUnit origCD, String packageName) {
        ASTCDClass ast = UMLPMill.cDClassBuilder()
                .setModifier(PUBLIC.build())
                .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("Request<Long>"))
                .setName(CLASS_NAME)
                .build();
        getCD4C().addImport(ast, "umlp.command.SimpleResult");
        getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE),
                origCD.getCDDefinition().getName()+SUFFIX));
        getCD4C().addImport(ast, "umlp.request.Request");

        getCD4C().addConstructor(ast, "umlp.requests.getfreegemid.Constructor", CLASS_NAME, getPackageName(origCD, DEFAULT_PACKAGE));
        getCD4C().addMethod(ast, "umlp.requests.getfreegemid.DoRun", origCD.getCDDefinition().getName());
        return ast;
    }

    protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, String packageName) {
        ASTCDClass ast = UMLPMill.cDClassBuilder()
                .setModifier(PUBLIC.build())
                .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + CLASS_NAME + ">"))
                .setName(CLASS_NAME + DE_SER)
                .build();

        getCD4C().addMethod(ast, "umlp.requests.getfreegemid.FromJsonObject");

        getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
        getCD4C().addImport(ast, "umlp.command.CommandDeSer");

        return ast;
    }
}
