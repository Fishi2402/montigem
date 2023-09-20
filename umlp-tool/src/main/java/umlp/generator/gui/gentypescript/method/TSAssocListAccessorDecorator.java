/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import umlp.generator.domain.decorator.methods.AssocListAccessorDecorator;

import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSAssocListAccessorDecorator extends AssocListAccessorDecorator {

  public TSAssocListAccessorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                      ASTCDType ast) {
    super(glex, service, ast);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methods = super.decorate(ast);
    methods.forEach(m -> {
      System.err.printf("%s: %b", m.getName(), m.getModifier().isAbstract());
    });
    return methods;
  }
    /*@Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    String targetName = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());

    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSContains", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSContainsAll", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSIterator", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSSize", service.getNativeAttributeName(ast.getName())).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSGetList", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSSubList", service.getNativeAttributeName(ast.getName()), targetName).get());

    // generate getX-Method
    methodList.add(createGetMethod(ast));

    // generate getXGem-Method
    ASTCDMethod getGemList = createGetGemList(ast);
    if (getGemList.getModifier().isAbstract()){
      //Add method to override
      ASTCDMethod getOverride = createGetGemListOverride(ast);
      methodList.add(getOverride);
      //modify internal getter to call override
      getGemList.getModifier().setAbstract(false);
      this.replaceTemplate(EMPTY_BODY, getGemList, new TemplateHookPoint("umlp.methods.GetDerived", getOverride.getName()));
    }
    methodList.add(getGemList);

    return methodList;
  }*/

  /*protected ASTCDMethod createGetMethod(ASTCDAttribute ast) {
    String name = String.format(GET, StringTransformations.capitalize(service.getNativeAttributeName(ast.getName())) + "1");
    String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    ASTCDParameter param = getCDParameterFacade().createParameter("int", "index");
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, param);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getAssocGetTemplate(), ast.getName()));
    return method;
  }*/

  /*protected List<ASTCDMethod> createGetMethod(ASTCDAttribute ast) {
    //Name and type
    String paramName = getParamName(ast);
    String name = String.format(GET, paramName);
    String type = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());

    //Overload typing methods
    ASTCDParameter typeParam = getCDParameterFacade().createParameter("int", "index");
    ASTCDMethod arrayGetterTypeMethod = this.getCDMethodFacade().createMethod(PUBLIC.build(), String.format("Array<%s>", type), name);
    //arrayGetterTypeMethod.getSpannedScope().setEnclosingScope(new CD4CodeBasisScope());
    arrayGetterTypeMethod.setEnclosingScope(new CD4CodeBasisScope());
    ASTCDMethod indexGetterTypeMethod = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name, typeParam);

    //Method implementation
    ASTCDParameter param = getCDParameterFacade().createParameter("int", "index?");
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), "any", name, param);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getAssocGetListTemplate(), paramName));

    return List.of(arrayGetterTypeMethod, indexGetterTypeMethod, method);
  }*/

  @Override
  protected String getAssocGetListTemplate() {
    return "umlp.gui2ts.methods.TSAssocGetList";
  }

  @Override
  protected String getAssocGetListNameTemplate() {
    return "%s1";
  }

  @Override
  protected List<String> getMethodTemplates() {
    return List.of(
        "umlp.gui2ts.methods.assocs.TSContains",
        "umlp.gui2ts.methods.assocs.TSContainsAll",
        "umlp.gui2ts.methods.assocs.TSIterator",
        "umlp.gui2ts.methods.assocs.TSSize",
        "umlp.gui2ts.methods.assocs.TSGetList",
        "umlp.gui2ts.methods.assocs.TSSubList"
    );
  }
}
