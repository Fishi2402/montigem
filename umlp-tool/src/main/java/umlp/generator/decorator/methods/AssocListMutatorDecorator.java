package umlp.generator.decorator.methods;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class AssocListMutatorDecorator extends SimpleMethodMutatorDecorator {

  public AssocListMutatorDecorator(GlobalExtensionManagement glex, ASTCDType ast) {
    super(glex, ast);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    String targetName = (new CdUtilsPrinter()).printType(((ASTMCGenericType) ast.getMCType()).getMCTypeArgument(0).getMCTypeOpt().get());
    String sourceName = this.ast.getName();
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Add", ast.getName(), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.AddAll", ast.getName(), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.InternalAdd", ast.getName(), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.InternalSet", ast.getName(), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Remove", ast.getName(), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Remove_Id", ast.getName(), targetName, sourceName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.InternalRemove", ast.getName(), targetName, sourceName).get());
    List<ASTCDMethod> notifyMethods = createNotifyMethods(methodList);

    methodList.addAll(notifyMethods);
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.RemoveByIdSure", ast.getName()).get());

    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.Clear", ast.getName(), targetName).get());
    methodList.add((ASTCDMethod) cd4c.createMethod(this.ast, "umlp.methods.assocs.RetainAll", ast.getName(), targetName).get());

    cd4c.addImport(this.ast, "com.google.common.collect.Sets");

    return methodList;
  }

  protected List<ASTCDMethod> createNotifyMethods(List<ASTCDMethod> methodList) {
    ArrayList<ASTCDMethod> retList = Lists.newArrayList();
    for (ASTCDMethod method : methodList) {
      ASTCDMethod newMethod = method.deepClone();
      newMethod.removeCDParameter(newMethod.sizeCDParameters() - 1);
      String body = (newMethod.getMCReturnType().isPresentMCVoidType()) ? "" : "return ";
      body += method.getName() + "(" +
              Joiners.COMMA.join(newMethod.streamCDParameters().map(s -> s.getName()).collect(Collectors.toList())) +
              ", NotificationScope.ALL);";
      glex.replaceTemplate(EMPTY_BODY, newMethod, new StringHookPoint(body));
      retList.add(newMethod);
    }
    return retList;
  }
}
