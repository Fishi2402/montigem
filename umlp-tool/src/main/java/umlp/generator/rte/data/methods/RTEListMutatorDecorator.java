/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.rte.data.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class RTEListMutatorDecorator extends ListMutatorDecorator {

  protected final ASTCDType ast;

  protected final CD4C cd4c = CD4C.getInstance();

  public RTEListMutatorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                 ASTCDType ast) {
    super(glex, service);
    this.ast = ast;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    if (!ast.getModifier().isDerived() && !ast.getModifier().isReadonly()) {
      List<ASTCDMethod> setters = createSetter(ast);
      glex.replaceTemplate(EMPTY_BODY, setters.get(0), new TemplateHookPoint(
          getAddSetterTemplate(),
          service.getNativeAttributeName(ast.getName()), setters.get(0)));

      glex.replaceTemplate(EMPTY_BODY, setters.get(1), new TemplateHookPoint(
          getAddIndexSetterTemplate(),
          service.getNativeAttributeName(ast.getName()), setters.get(1)));

      glex.replaceTemplate(EMPTY_BODY, setters.get(2), new TemplateHookPoint(getRemoveSetterTemplate(),
          service.getNativeAttributeName(ast.getName()), setters.get(2)));

      methodList.addAll(setters);

      methodList.add(createSetListMethod(ast));
    }
    return methodList;
  }

  protected String getAddIndexSetterTemplate() {
    return "rte.methods.lists.AddIndexSetter";
  }

  protected String getAddSetterTemplate() {
    return "rte.methods.lists.AddSetter";
  }

  protected String getRemoveSetterTemplate() {
    return "rte.methods.lists.RemoveSetter";
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
        String.format(ADD, capitalizedAttributeNameWithOutS, attributeType),
        String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType),
        String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS));
  }

}
