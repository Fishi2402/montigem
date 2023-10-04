/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.domain.decorator.methods.UMLPListMutatorDecorator;

import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class TSListMutatorDecorator extends UMLPListMutatorDecorator {

  protected final ASTCDType ast;

  protected final CD4C cd4c = CD4C.getInstance();

  public static final String SUFFIX = "1";

  public TSListMutatorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                ASTCDType ast) {
    super(glex, service, ast);
    this.ast = ast;
  }

  @Override
  protected List<ASTCDMethod> createNotificationSetter(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = super.createNotificationSetter(ast);
    methodList.forEach(m -> m.setName(m.getName()+SUFFIX));
    return methodList;
  }

  protected List<String> getNotificationSetterTemplate() {
    return Arrays.asList("umlp.gui2ts.methods.lists.AddNotificationSetter",
        "umlp.gui2ts.methods.lists.RemoveNotificationSetter");
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
        String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType),
        String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS));
  }

  @Override
  protected String getDelegateWithScopeTemplate() {
    return "umlp.gui2ts.methods.DelegateWithScope";
  }

}
