/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.Joiners;
import umlp.generator.domain.decorator.methods.UMLPMandatoryMutatorDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class TSAssocListMutatorDecorator extends UMLPMandatoryMutatorDecorator {

  public TSAssocListMutatorDecorator(GlobalExtensionManagement glex, CDGenService service,
                                   ASTCDType ast) {
    super(glex, service, ast);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    if(ast.getModifier().isDerived()){
      return methodList;
    }
    String targetName = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    String sourceName = this.ast.getName();
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSAdd", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSAddId", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSAddAll", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSAddAllId", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSInternalAdd", service.getNativeAttributeName(ast.getName()), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSInternalAddId", service.getNativeAttributeName(ast.getName()), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSInternalSet", service.getNativeAttributeName(ast.getName()), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSRemove", service.getNativeAttributeName(ast.getName()), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSRemove_Id", service.getNativeAttributeName(ast.getName()), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSInternalRemove", service.getNativeAttributeName(ast.getName()), targetName, sourceName).get());
    List<ASTCDMethod> notifyMethods = createNotifyMethods(methodList);

    methodList.addAll(notifyMethods);
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSRemoveByIdSure", service.getNativeAttributeName(ast.getName())).get());

    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSClear", service.getNativeAttributeName(ast.getName()), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.gui2ts.methods.assocs.TSRetainAll", service.getNativeAttributeName(ast.getName()), targetName).get());

    return methodList;
  }

  protected List<ASTCDMethod> createNotifyMethods(List<ASTCDMethod> methodList) {
    ArrayList<ASTCDMethod> retList = Lists.newArrayList();
    for (ASTCDMethod method : methodList) {
      ASTCDMethod newMethod = method.deepClone();
      newMethod.setName(newMethod.getName() + "2");
      newMethod.removeCDParameter(newMethod.sizeCDParameters() - 1);
      String body = (newMethod.getMCReturnType().isPresentMCVoidType()) ? "" : "return ";
      body += "this." + method.getName() + "(" +
              Joiners.COMMA.join(newMethod.streamCDParameters().map(s -> s.getName()).collect(Collectors.toList())) +
              ", NotificationScope.ALL);";
      glex.replaceTemplate(EMPTY_BODY, newMethod, new StringHookPoint(body));
      retList.add(newMethod);
    }
    return retList;
  }
}
