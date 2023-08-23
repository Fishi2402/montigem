/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocMandatoryAccessorDecorator extends MandatoryAccessorDecorator {

    public AssocMandatoryAccessorDecorator(GlobalExtensionManagement glex,
                                           CDGenService service) {
        super(glex, service);
    }

    @Override
    public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
        List<ASTCDMethod> methodList = Lists.newArrayList();

        // generate getX-Method
        methodList.add(createGetMethod(ast));

        // generate getXGem-Method
        ASTCDMethod getGem = createGetGem(ast);
        if (getGem.getModifier().isAbstract()) {
            //Add method to override
            ASTCDMethod getOverride = createGetGemOverride(ast);
            methodList.add(getOverride);
            //modify internal getter to call override
            getGem.getModifier().setAbstract(false);
            this.replaceTemplate(EMPTY_BODY, getGem, new TemplateHookPoint("umlp.methods.GetDerived", getOverride.getName()));
        }
        methodList.add(getGem);

        return methodList;
    }

    protected ASTCDMethod createGetMethod(ASTCDAttribute ast) {
        String name = getParamName(ast);
        String methodName = String.format(GET, name);
        ASTMCType type = ast.getMCType();
        if (ast.getMCType() instanceof ASTMCGenericType) {
            type = ((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get().deepClone();
        }
        ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, methodName);
        this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getGetTemplate(), name));
        return method;
    }


    protected String getGetTemplate() {
        return "umlp.methods.AssocGet";
    }

    protected ASTCDMethod createGetGem(ASTCDAttribute ast) {
        ASTCDMethod method = createGetter(ast);
        method.setName(method.getName() + "Gem");
        return method;
    }

    private ASTCDMethod createGetGemOverride(ASTCDAttribute ast) {
        ASTCDMethod method = createGetter(ast);
        method.getModifier().setAbstract(true);
        method.setName(method.getName() + "DerivedGem");
        return method;
    }

    private String getParamName(ASTCDAttribute ast) {
        return StringTransformations.capitalize(service.getNativeAttributeName(ast.getName()));
    }
}
