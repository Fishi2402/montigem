package umlp.py;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.DecoratorData;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.facade.CDTypeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.py.utily.Pyutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class GemSetterDecorator extends AbstractDecorator<GemSetterDecorator.SetterData>
    implements CDBasisVisitor2 {

  protected SetterData setterData;
  public static final String SCOPE_PARAM_NAME = "scope";

  @Override
  public void init(DecoratorData util, Optional<GlobalExtensionManagement> glexOpt) {
    super.init(util, glexOpt);
    this.setterData = this.decoratorData.createDataIfAbsent(this.getClass(), SetterData::new);
  }

  @Override
  public void visit(ASTCDAttribute node) {
    var info = UmlpAttrHelper.getFromSymTypeExpr(node.getSymbol().getType());

    String templateName;

    String setterName = "set" + StringTransformations.capitalize(node.getName());
    String attrName = node.getName();

    var parentDecClass = decoratorData.getAsDecorated(
        (ASTCDClass) decoratorData.getParent(node).get());

    var scopeParam = CDParameterFacade.getInstance()
        .createParameter("montigem_runtime.gembase.NotificationScope", SCOPE_PARAM_NAME);

    glexOpt.ifPresent(glex -> glex.replaceTemplate("PyParam::Initial",
                                                   scopeParam,
                                                   new StringHookPoint(
                                                       "= montigem_runtime.gembase.NotificationScope.ALL")));

    switch (info.getMultiplicity()) {
      case MANDATORY:
        String paramType;
        if (info.getTypeClass() == UmlpAttrHelper.RealType.BUILD_IN) {
          paramType = "typing.Any";
          templateName = "cd2python.gem.ManSetterBuiltIn";
        } else if (info.getTypeClass() == UmlpAttrHelper.RealType.GEM) {
          templateName = "cd2python.gem.ManSetterGem";
          paramType = "montigem_runtime.gembase.GEM_ID";
        } else {
          Log.warn("Unhandled type in GemSetterDecorator: " + info.getTypeClass(),
                   node.get_SourcePositionStart());
          break;
        }
        var setter = CDMethodFacade.getInstance()
            .createMethod(CDBasisMill.modifierBuilder().PUBLIC().build(), setterName,
                          CDParameterFacade.getInstance().createParameter(paramType, attrName),
                          scopeParam);

        Pyutils.markAsAsync(setter);
        glexOpt.ifPresent(glex -> glex.replaceTemplate(Pyutils.EMPTY_TEMPLATE,
                                                       setter,
                                                       new TemplateHookPoint(templateName,
                                                                             attrName)));

        this.setterData.attributesData.computeIfAbsent(node, x -> new ArrayList<>())
            .add(new MethodInformation(SetterMethodKind.SET_MANDATORY, setter, templateName,
                                       attrName));
        addToClass(parentDecClass,
                   setter);

        break;
      case SET:
        if (info.getTypeClass() != UmlpAttrHelper.RealType.GEM) {
          Log.warn("Unhandled kind: " + info.getTypeClass(), node.get_SourcePositionStart());
          break;
        }
      {
        String addMethodName =  node.getName() + "_add";
        var addMethod = CDMethodFacade.getInstance()
            .createMethod(CDBasisMill.modifierBuilder().PUBLIC().build(), addMethodName,
                          CDParameterFacade.getInstance().createParameter("int", "index_in_list"),
                          CDParameterFacade.getInstance().createParameter("montigem_runtime.gembase.GEM_ID", attrName),
                          scopeParam);

        Pyutils.markAsAsync(addMethod);
        glexOpt.ifPresent(glex -> glex.replaceTemplate(Pyutils.EMPTY_TEMPLATE,
                                                       addMethod,
                                                       new TemplateHookPoint("cd2python.gem.ListSetterGem",
                                                                             attrName)));

        this.setterData.attributesData.computeIfAbsent(node, x -> new ArrayList<>())
            .add(new MethodInformation(SetterMethodKind.ADD_LIST, addMethod, "cd2python.gem.ListSetterGem",
                                       attrName));
        addToClass(parentDecClass,
                   addMethod);
      }
        break;
      default:
        Log.warn("Unhandled multiplicity: " + info.getMultiplicity(), node.get_SourcePositionStart());
    }

  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

  @Override
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    return Iterables.concat(super.getMustRunAfter(), List.of(GemBaseDecorator.class));
  }

  public static class SetterData {
    protected final Map<ASTCDAttribute, List<MethodInformation>> attributesData = new HashMap<>();

    public List<MethodInformation> getMethods(ASTCDAttribute node) {
      return attributesData.get(node);
    }
  }

  public static class MethodInformation {
    private final SetterMethodKind kind;
    private final ASTCDMethod setMethod;
    private final String templateName;
    private final String paramName;

    public MethodInformation(SetterMethodKind kind, ASTCDMethod setMethod, String templateName,
                             String paramName) {
      this.kind = kind;
      this.setMethod = setMethod;
      this.templateName = templateName;
      this.paramName = paramName;
    }

    public SetterMethodKind getKind() {
      return kind;
    }

    public ASTCDMethod getSetMethod() {
      return setMethod;
    }

    public String getTemplateName() {
      return templateName;
    }

    public String getParamName() {
      return paramName;
    }
  }

  public enum SetterMethodKind {
    SET_MANDATORY,
    ADD_LIST
  }
}
