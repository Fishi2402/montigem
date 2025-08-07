package umlp.py;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.facade.CDTypeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.py.utily.Pyutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GemCommandHandlerDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2 {

  public static final String SUFFIX = "CommandHandler";

  protected Stack<String> classNameStack = new Stack<>();

  protected List<Map<String, String>> commandList = new ArrayList<>();
  protected ASTCDClass commandObserverClass;
  protected String domainName;

  @Override
  public void visit(ASTCDDefinition node) {
    this.domainName = node.getName();
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    commandList.clear();
    this.commandObserverClass = CDTypeFacade.getInstance()
        .createCDClass(node.getCDDefinition().getName() + SUFFIX);
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    decoratorData.getAsDecorated(node).getCDDefinition().addCDElement(commandObserverClass);

    var handleMethod = CDMethodFacade.getInstance()
        .createMethod(CDBasisMill.modifierBuilder().PUBLIC()
                          .build(), "handleCommand",
                      CDParameterFacade.getInstance().createParameter("typing.Any", "message"),
                      CDParameterFacade.getInstance()
                          .createParameter("typing.Any", "domain_manager")
                     );
    Pyutils.markAsAsync(handleMethod);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(Pyutils.EMPTY_TEMPLATE,
                                                   handleMethod,
                                                   new TemplateHookPoint(
                                                       "cd2python.commandconsumer.handle",
                                                       this.commandList)));
    addToClass(commandObserverClass, handleMethod);
    commandObserverClass = null;
  }

  @Override
  public void visit(ASTCDClass node) {
    classNameStack.add(node.getName());
    CD4C.getInstance().addImport(commandObserverClass, this.domainName + "." + node.getName());
  }

  @Override
  public void visit(ASTCDAttribute node) {
    if (classNameStack.isEmpty()) return;
    var info = UmlpAttrHelper.getFromSymTypeExpr(node.getSymbol().getType());

    String clazzName = classNameStack.peek();

    switch (info.getMultiplicity()) {
      case MANDATORY:
//        switch (info.getTypeClass()) {
//          case GEM:
//            addCommand("mydomain.commands." + clazzName + "_set" + StringTransformations.capitalize(node.getName()),
//                       "cd2python.commandconsumer.handleAdd", node.getName());
//            break;
//          case BUILD_IN:
        addCommand(domainName.toLowerCase()+".commands." + clazzName + "_set" + StringTransformations.capitalize(
                       node.getName()),
                   "cd2python.commandconsumer.handleSet", node.getName());
//            break;
//        }
        break;
      case SET:
//        switch (info.getTypeClass()) {
//          case GEM:
        addCommand(domainName.toLowerCase()+".commands." + clazzName + "_add" + StringTransformations.capitalize(
                       node.getName()),
                   "cd2python.commandconsumer.handleAdd", node.getName());
        break;
//          case BUILD_IN:
//            addCommand("mydomain.commands." + clazzName + "_set" + StringTransformations.capitalize(node.getName()),
//                       "cd2python.commandconsumer.handleSet", node.getName());
//            break;
//        }
      default:
        Log.warn("Unknown multiplicity: " + info.getMultiplicity(), node.get_SourcePositionStart());
    }

  }

  protected void addCommand(String command, String template, String attr) {
    var m = new HashMap<String, String>();
    m.put("command", command);
    m.put("template", template);
    m.put("clazz", classNameStack.peek());
    m.put("attr", attr);
    m.put("domain", domainName);
    this.commandList.add(m);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    classNameStack.pop();
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

  @Override
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    // TODO: Setter
    return Iterables.concat(super.getMustRunAfter(),
                            Arrays.asList(GemSetterDecorator.class));
  }


}
