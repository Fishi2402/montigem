/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.GenService;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocMandatoryMutatorDecorator extends SimpleMethodMutatorDecorator {

  public AssocMandatoryMutatorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                        ASTCDType domainAst) {
    super(glex, service, domainAst);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    ASTMCType simpleType = ast.getMCType();
    if (ast.getMCType() instanceof ASTMCGenericType) {
      simpleType = ((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get().deepClone();
    }
    List<ASTCDMethod> methodList = Lists.newArrayList();

    // Generate setX-Methods
    ASTCDMethod method = createSetter(ast);
    method.getCDParameter(0).setMCType(simpleType);
    methodList.add(method);

    ASTCDMethod method2 = createNotificationSetter(ast, false);
    method2.getCDParameter(0).setMCType(simpleType);
    glex.replaceTemplate(EMPTY_BODY, method2, new TemplateHookPoint(getGemNotificationSetterTemplate(),
            this.ast.getName(), GenService.getNativeName(ast.getName()), new CdUtilsPrinter().printType(ast.getMCType())));
    methodList.add(method2);

    // Generate setXGem-Methods
    ASTCDMethod method3 = createGemSetter(ast);
    methodList.add(method3);

    ASTCDMethod method4 = createNotificationSetter(ast, true);
    method4.setName(method4.getName()+"Gem");
    methodList.add(method4);

    ASTCDMethod method5 = (ASTCDMethod) cd4c.createMethod(this.ast, getInternalSetOneTemplate(),
            service.getNativeAttributeName(ast.getName()), CD4CodeMill.prettyPrint(simpleType, false), ast.getName()).get();
    methodList.add(method5);

    return methodList;
  }

  protected String getGemNotificationSetterTemplate() {
    return "umlp.methods.GemNotificationSetter";
  }

  protected String getInternalSetOneTemplate() {
    return "umlp.methods.assocs.InternalSetOne";
  }

}
