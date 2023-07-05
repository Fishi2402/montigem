/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.requests;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

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
        astcdPackage.addCDElement(withOrigin(createClass(origCD, astcdPackage.getName())));
        astcdPackage.addCDElement(withOrigin(createDeSer(origCD, astcdPackage.getName())));

    }

    protected ASTCDClass createClass(ASTCDCompilationUnit origCD, String packageName) {
        ASTCDClass ast = UMLPMill.cDClassBuilder()
                .setModifier(PUBLIC.build())
                .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(extendedClass()))
                .setName(CLASS_NAME)
                .build();
        getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("SimpleResult"));
        getCD4C().addImport(ast, Joiners.DOT.join(getPackageName(origCD, DEFAULT_PACKAGE),
                origCD.getCDDefinition().getName()+SUFFIX));
        getCD4C().addImport(ast, PathUtil.getBackendRTERequestPath("Request"));

        addConstructor(ast, origCD);
        getCD4C().addMethod(ast, getDoRunTemplate(), origCD.getCDDefinition().getName());
        return ast;
    }

    protected String extendedClass() {
       return "Request<Long>";
    }

    protected void addConstructor(ASTCDClass ast, ASTCDCompilationUnit origCD) {
        getCD4C().addConstructor(ast, getConstructorTemplate(),
            CLASS_NAME, getPackageName(origCD, DEFAULT_PACKAGE));
    }

    protected String getDoRunTemplate() {
        return "umlp.requests.getfreegemid.DoRun";
    }

    protected String getConstructorTemplate() {
        return "umlp.requests.getfreegemid.Constructor";
    }

    protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, String packageName) {
        ASTCDClass ast = UMLPMill.cDClassBuilder()
                .setModifier(PUBLIC.build())
                .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("CommandDeSer<" + CLASS_NAME + ">"))
                .setName(CLASS_NAME + DE_SER)
                .build();

        getCD4C().addMethod(ast, getFromJsonObjectTemplate());

        addDeSerImport(ast, origCD);

        return ast;
    }

    protected void addDeSerImport(ASTCDClass ast, ASTCDCompilationUnit origCD) {
        getCD4C().addImport(ast, "de.monticore.symboltable.serialization.json.JsonObject");
        getCD4C().addImport(ast, PathUtil.getBackendRTECommandPath("CommandDeSer"));
    }

    protected String getFromJsonObjectTemplate() {
        return "umlp.requests.getfreegemid.FromJsonObject";
    }
}
