/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsAccessVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.stream.Collectors;

import static umlp.generator.gui.GUIGeneratorUtil.isInputGui;

public class GUI2JavaDecorator extends GUIAbstractDecorator {

  public GUI2JavaDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {

    // Get all ASTGUIComponents
    GUIComponentsAccessVisitor viewComponents = new GUIComponentsAccessVisitor(guiAst);

    boolean isInputGui = isInputGui(guiAst, viewComponents.getComponents().stream().filter(GUIGeneratorUtil::isInputComponent).collect(Collectors.toList()));

    DomainRefDecorator domainRefDecorator = new DomainRefDecorator(glex, isInputGui);
    domainRefDecorator.transform(guiAst, decoratedAST);
    Log.info("After decorator: DomainRefDecorator", DomainRefDecorator.class.getName());

    GUI2JavaComponentsDecorator componentsDecorator = new GUI2JavaComponentsDecorator(glex, viewComponents);
    componentsDecorator.transform(guiAst, domainRefDecorator.getDecoratedClass());
    domainRefDecorator.getCreateInputValueClass().ifPresent(createClass -> componentsDecorator.transform(guiAst, createClass));
    Log.info("After decorator: GUI2JavaComponentsDecorator", GUI2JavaComponentsDecorator.class.getName());

  }

}
