package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.TypeCalculator;
import umlp.generator.gui.GUIComponentsAccessVisitor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GUIComponentsDecorator extends GUIAbstractDecorator {

  public static final Predicate<ASTGUIComponent> PREDICATE_TEXT_INPUT = c -> c.getQualifiedName().equals("umlp.gui.TextInput");
  public static final Predicate<ASTGUIComponent> PREDICATE_NUMBER_INPUT = c -> c.getQualifiedName().equals("umlp.gui.NumberInput");
  public static final Predicate<ASTGUIComponent> PREDICATE_BUTTON = c -> c.getQualifiedName().equals("umlp.gui.Button");
  public static final Predicate<ASTGUIComponent> PREDICATE_TABLE = c -> c.getQualifiedName().equals("umlp.gui.Table");

  public GUIComponentsDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    // Get all ASTGUIComponents
    GUIComponentsAccessVisitor viewComponents = new GUIComponentsAccessVisitor(guiAst);
    List<ASTGUIComponent> allComponents = viewComponents.getComponents();

    TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());

    // Find the decorated class
    Optional<ASTCDClass> decoratedClass = decoratedAST.getCDDefinition().getCDClassesList().stream().filter(c -> c.getName()
        .equals(getComponentClassName(guiAst.getGUIComponentDeclaration()))).findAny();

    IGUIDSLScope artifactScope = guiAst.getEnclosingScope();

    viewComponents.getFieldAccess().forEach(f -> {
          if (f.getExpression() instanceof ASTNameExpression) {
            Optional<GUIVariableSymbol> symbol = ((GUIDSLScope) f.getEnclosingScope()).resolveGUIVariable(((ASTNameExpression)f.getExpression()).getName());
            Optional<VariableSymbol> attr = resolveAttribute(symbol, f.getName());
          }
        }
    );

    if (decoratedClass.isPresent()) {
      for (ASTGUIComponent guiComponent : allComponents) {
        new InputValueDecorator(glex, guiAst).transform(decoratedClass.get(), guiComponent);
        new CallDecorator(glex, guiAst).transform(decoratedClass.get(), guiComponent);
      }


    /*  filterComponentsWithPredicate(allComponents, PREDICATE_TEXT_INPUT).forEach(c -> new TextInputDecorator(glex, guiAst).transform(decoratedClass.get(), c));
      filterComponentsWithPredicate(allComponents, PREDICATE_NUMBER_INPUT).forEach(c -> new NumberInputDecorator(glex, guiAst).transform(decoratedClass.get(), c));
      filterComponentsWithPredicate(allComponents, PREDICATE_BUTTON).forEach(c -> new ButtonDecorator(glex, guiAst).transform(decoratedClass.get(), c));
      filterComponentsWithPredicate(allComponents, PREDICATE_TABLE).forEach(c -> new TableDecorator(glex, guiAst).transform(decoratedClass.get(), c));*/
    }
  }

  protected static List<ASTGUIComponent> filterComponentsWithPredicate(List<ASTGUIComponent> components,
                                                                       Predicate<ASTGUIComponent> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }

}
