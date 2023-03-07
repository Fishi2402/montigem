/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genhtml.decorator;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsAccessVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.stream.Collectors;

import static umlp.generator.gui.GUIGeneratorUtil.isInputGui;

public class GUI2HtmlDecorator extends GUIAbstractDecorator {

  public GUI2HtmlDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTGUICompilationUnit decoratedAST) {

    // Get all ASTGUIComponents
    GUIComponentsAccessVisitor viewComponents = new GUIComponentsAccessVisitor(guiAst);

    boolean isInputGui = isInputGui(guiAst, viewComponents.getComponents().stream().filter(GUIGeneratorUtil::isInputComponent).collect(Collectors.toList()));

  }

}
