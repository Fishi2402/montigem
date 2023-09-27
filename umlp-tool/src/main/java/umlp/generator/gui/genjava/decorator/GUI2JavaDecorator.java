/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.stream.Collectors;

public class GUI2JavaDecorator extends GUIAbstractDecorator {

  public GUI2JavaDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST, GUIComponentsVisitor viewComponents) {
    boolean isInputGui = util.isInputGui(guiAst, viewComponents.getComponents().stream()
        .filter(c -> util.isInputComponent(c)).collect(Collectors.toList()));

    DomainRefDecorator domainRefDecorator = new DomainRefDecorator(glex, isInputGui, viewComponents);
    domainRefDecorator.transform(guiAst, decoratedAST);
    Log.info("After decorator: DomainRefDecorator", DomainRefDecorator.class.getName());

    glex.replaceTemplate("cd2java.Imports", new TemplateHookPoint("umlp.Imports"));

//    GUI2JavaComponentsDecorator componentsDecorator = new GUI2JavaComponentsDecorator(glex, viewComponents);
//    componentsDecorator.transform(guiAst, domainRefDecorator.getDecoratedClass());
//    Log.info("After decorator: GUI2JavaComponentsDecorator", GUI2JavaComponentsDecorator.class.getName());
  }

}
