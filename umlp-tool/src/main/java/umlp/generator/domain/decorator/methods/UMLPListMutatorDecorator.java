/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class UMLPListMutatorDecorator extends ListMutatorDecorator {

  protected final ASTCDType ast;

  protected final CD4C cd4c = CD4C.getInstance();

  public UMLPListMutatorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                  ASTCDType ast) {
    super(glex, service);
    this.ast = ast;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    if (!ast.getModifier().isDerived() && !ast.getModifier().isReadonly()) {
      methodList.addAll(createSetter(ast));
      methodList.addAll(createNotificationSetter(ast));
    }
    return methodList;
  }

  @Override
  protected List<ASTCDMethod> createSetter(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = super.createSetter(ast);
    methodList.forEach(m -> {
      glex.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint(getDelegateWithScopeTemplate(), m,
          m.getMCReturnType().isPresentMCType()));
    });
    return methodList;
  }

  protected List<ASTCDMethod> createNotificationSetter(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = super.createSetter(ast);
    for (int i=0; i<methodList.size(); i++) {
      methodList.get(i).addCDParameter(getCDParameterFacade().createParameter("NotificationScope", "scope"));
      glex.replaceTemplate(EMPTY_BODY, methodList.get(i), new TemplateHookPoint(getNotificationSetterTemplate().get(i),
          this.ast.getName(), service.getNativeAttributeName(ast.getName()), methodList.get(i)));
    }
    return methodList;
  }

  protected List<String> getNotificationSetterTemplate() {
    return Arrays.asList("umlp.methods.lists.AddNotificationSetter",
        "umlp.methods.lists.RemoveNotificationSetter");
  }

  protected String getDelegateWithScopeTemplate() {
    return "umlp.methods.DelegateWithScope";
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
        String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType),
        String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS));
  }

}
