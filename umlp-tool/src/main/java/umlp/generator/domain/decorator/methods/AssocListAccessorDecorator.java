/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class AssocListAccessorDecorator extends MandatoryAccessorDecorator {

  protected final ASTCDType ast;

  protected final CD4C cd4c = CD4C.getInstance();

  public AssocListAccessorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                    ASTCDType ast) {
    super(glex, service);
    this.ast = ast;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    String targetName = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    String paramName = getParamName(ast);

    List<ASTCDMethod> methods = getMethodTemplates().stream()
        .map(template -> (ASTCDMethod) cd4c.createMethod(this.ast, template, paramName, targetName).get())
        .collect(Collectors.toList());
    methodList.addAll(methods);

    // generate getX-Method
    methodList.addAll(createGetMethod(ast));

    // generate getXGem-Method
    ASTCDMethod getGemList = createGetGemList(ast);
    if (getGemList.getModifier().isAbstract()) {
      //Add method to override
      ASTCDMethod getOverride = createGetGemListOverride(ast);
      methodList.add(getOverride);
      //modify internal getter to call override
      getGemList.getModifier().setAbstract(false);
      this.replaceTemplate(EMPTY_BODY, getGemList, new TemplateHookPoint("umlp.methods.GetDerived", getOverride.getName()));
    }
    methodList.add(getGemList);

    return methodList;
  }

  protected List<ASTCDMethod> createGetMethod(ASTCDAttribute ast) {
    String paramName = getParamName(ast);
    String name = String.format(getAssocGetListNameTemplate(), String.format(GET, paramName));
    String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    ASTCDParameter param = getCDParameterFacade().createParameter("int", "index");
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, param);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getAssocGetListTemplate(), paramName));
    return List.of(method);
  }

  private ASTCDMethod createGetGemList(ASTCDAttribute ast) {
    ASTCDMethod method = createGetter(ast);
    method.setName("_internal_" + method.getName() + "GemList");
    return method;
  }

  private ASTCDMethod createGetGemListOverride(ASTCDAttribute ast) {
    ASTCDMethod method = createGetter(ast);
    method.getModifier().setAbstract(true);
    method.setName(method.getName() + "DerivedGemList");
    return method;
  }

  protected String getParamName(ASTCDAttribute ast) {
    return StringTransformations.capitalize(service.getNativeAttributeName(ast.getName()));
  }

  protected String getAssocGetListTemplate() {
    return "umlp.methods.AssocGetList";
  }

  protected String getAssocGetListNameTemplate() {
    return "%s";
  }

  protected List<String> getMethodTemplates() {
    return List.of(
        "umlp.methods.assocs.Contains",
        "umlp.methods.assocs.ContainsAll",
        "umlp.methods.assocs.Iterator",
        "umlp.methods.assocs.Size",
        "umlp.methods.assocs.GetList",
        "umlp.methods.assocs.SubList"
    );
  }
}

