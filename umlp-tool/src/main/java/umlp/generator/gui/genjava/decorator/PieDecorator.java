/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PieDecorator extends GUI2JavaComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_ENTRIES = c -> c.getName().equals("entries");

  protected final ASTGUIComponent guiComponent;
  protected final String componentName;

  protected List<String> pieEntriesArgs = new ArrayList<>();

  public PieDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst, ASTGUIComponent guiComponent, String componentName) {
    super(glex, guiAst);
    this.guiComponent = guiComponent;
    this.componentName = componentName;
  }

  public void transform(ASTCDClass decoratedClass) {
    Optional<ASTGUINamedArg> pie = getComponentArgOptional(guiComponent, PREDICATE_ENTRIES);

    MethodDecorator decorator = new MethodDecorator(glex);

    if (pie.isPresent()) {
      ASTCDAttribute entriesAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
          "List<PieEntry>", "pieEntries" + componentName);
      glex.replaceTemplate("cd2java.Value", entriesAttribute, new StringHookPoint(" = new ArrayList<>()"));
      getCD4C().addImport(decoratedClass, "java.util.List");
      getCD4C().addImport(decoratedClass, "java.util.ArrayList");

      decoratedClass.addCDMember(entriesAttribute);
      decoratedClass.addAllCDMembers(decorator.decorate(entriesAttribute));

      if (pie.get().getGUIArgValue().getExpression() instanceof ASTCallExpression) {
        pieEntriesArgs.addAll(((ASTCallExpression) pie.get().getGUIArgValue().getExpression()).getArguments().getExpressionList().stream().map(e
            -> guiDSLPrinter.prettyprint(e)).collect(Collectors.toList()));
      }

    }
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    transform(decoratedClass);
  }

  public List<String> getInitPieEntriesArguments() {
    return pieEntriesArgs;
  }

  public String getComponentName() {
    return componentName;
  }
}

