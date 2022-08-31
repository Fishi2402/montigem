package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class SimpleMethodMutatorDecorator extends MandatoryMutatorDecorator {

  protected final ASTCDType domainAst;

  public SimpleMethodMutatorDecorator(GlobalExtensionManagement glex, ASTCDType domainAst) {
    super(glex);
    this.domainAst = domainAst;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    methodList.add(createSetter(ast));
    methodList.add(createNotificationSetter(ast, false));
    return methodList;
  }

  @Override
  protected ASTCDMethod createSetter(ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetter(ast);
    glex.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this." + method.getName() + "("
            + StringTransformations.uncapitalize(ast.getName()) + ", NotificationScope.ALL);"));
    return method;
  }

  protected ASTCDMethod createNotificationSetter(ASTCDAttribute ast, boolean isGem) {
    ASTCDMethod method = super.createSetter(ast);
    method.addCDParameter(getCDParameterFacade().createParameter("NotificationScope", "scope"));
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.methods.NotificationSetter",
            domainAst.getName(), ast.getName(), new CdUtilsPrinter().printType(ast.getMCType()), isGem));
    return method;
  }

}
