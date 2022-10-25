package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIComponentsAccessVisitor;

import java.util.ArrayList;
import java.util.List;

public class GUIDecorator extends GUIAbstractDecorator {

  protected List<ASTGUIComponent> inputComponents = new ArrayList<>();

  public GUIDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {

    // Get all ASTGUIComponents
    GUIComponentsAccessVisitor viewComponents = new GUIComponentsAccessVisitor(guiAst);
    List<ASTGUIComponent> allComponents = viewComponents.getComponents();

    filterComponentsWithPredicate(allComponents, PREDICATE_TEXT_INPUT).forEach(inputComponents::add);
    filterComponentsWithPredicate(allComponents, PREDICATE_NUMBER_INPUT).forEach(inputComponents::add);

    DomainRefDecorator domainRefDecorator = new DomainRefDecorator(glex, inputComponents);
    domainRefDecorator.transform(guiAst, decoratedAST);
    Log.info("After decorator: DomainRefDecorator", DomainRefDecorator.class.getName());

    GUIComponentsDecorator componentsDecorator = new GUIComponentsDecorator(glex, viewComponents, inputComponents);
    componentsDecorator.transform(guiAst, domainRefDecorator.getDecoratedClass());
    domainRefDecorator.getCreateInputValueClass().ifPresent(createClass -> componentsDecorator.transform(guiAst, createClass));
    Log.info("After decorator: GUIComponentsDecorator", GUIComponentsDecorator.class.getName());

  }

}
