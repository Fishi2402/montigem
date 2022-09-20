package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GUIComponentsDecorator extends GUIAbstractDecorator {

  public static final Predicate<ASTGUIComponent> PREDICATE_TEXT_INPUT = c -> c.getName().equals("TextInput");
  public static final Predicate<ASTGUIComponent> PREDICATE_NUMBER_INPUT = c -> c.getName().equals("NumberInput");
  public static final Predicate<ASTGUIComponent> PREDICATE_BUTTON = c -> c.getName().equals("Button");

  public GUIComponentsDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    // Get all ASTGUIComponents
    GUIComponentsVisitor viewComponents = new GUIComponentsVisitor(guiAst);
    List<ASTGUIComponent> allComponents = viewComponents.getComponents();

    // Find the decorated class
    Optional<ASTCDClass> decoratedClass = decoratedAST.getCDDefinition().getCDClassesList().stream().filter(c -> c.getName()
        .equals(getComponentClassName(guiAst.getGUIComponentDeclaration()))).findAny();

    if (decoratedClass.isPresent()) {
      filterComponentsWithPredicate(allComponents, PREDICATE_TEXT_INPUT).forEach(c -> new TextInputDecorator(glex).transform(decoratedClass.get(), c));
      filterComponentsWithPredicate(allComponents, PREDICATE_NUMBER_INPUT).forEach(c -> new NumberInputDecorator(glex).transform(decoratedClass.get(), c));
      filterComponentsWithPredicate(allComponents, PREDICATE_BUTTON).forEach(c -> new ButtonInputDecorator(glex).transform(decoratedClass.get(), c));
    }
  }

  protected static List<ASTGUIComponent> filterComponentsWithPredicate(List<ASTGUIComponent> components,
                                                                       Predicate<ASTGUIComponent> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }

}
