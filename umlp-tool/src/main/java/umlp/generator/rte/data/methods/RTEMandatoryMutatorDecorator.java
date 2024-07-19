package umlp.generator.rte.data.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class RTEMandatoryMutatorDecorator extends MandatoryMutatorDecorator {

  protected final ASTCDType ast;

  protected final CD4C cd4c = CD4C.getInstance();

  public RTEMandatoryMutatorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                       ASTCDType ast) {
    super(glex, service);
    this.ast = ast;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    if (!ast.getModifier().isDerived() && !ast.getModifier().isReadonly()) {
      methodList.add(createSetter(ast));
    }
    return methodList;
  }

  @Override
  protected ASTCDMethod createSetter(ASTCDAttribute ast) {
    ASTCDMethod method = super.createSetter(ast);
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getSetterTemplate(),
        service.getNativeAttributeName(ast.getName()), new CdUtilsPrinter().printType(ast.getMCType())));
    return method;
  }

  protected String getSetterTemplate() {
    return "rte.Setter";
  }

}
