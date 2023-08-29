/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.DomainRefDecorator;
import umlp.generator.gui.genjava.decorator.GUI2JavaDecorator;

import java.nio.file.Path;
import java.util.stream.Collectors;

public class GUI2TypeScriptDecorator extends GUI2JavaDecorator {

  boolean generateTS = true;

  public GUI2TypeScriptDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public GUI2TypeScriptDecorator(final GlobalExtensionManagement glex, boolean generateTS) {
    super(glex);
    this.generateTS = generateTS;
  }

  public void transform(ASTGUICompilationUnit guiAst,
      ASTCDCompilationUnit decoratedAST, GUIComponentsVisitor viewComponents, MCPath hwPath, Path outPath) {
    boolean isInputGui = util.isInputGui(guiAst, viewComponents.getComponents().stream()
        .filter(c -> util.isInputComponent(c)).collect(Collectors.toList()));

    if (generateTS) {
      TSDomainRefDecorator domainRefDecorator = new TSDomainRefDecorator(glex, isInputGui,
          viewComponents);
      domainRefDecorator.transform(guiAst, decoratedAST);
          Log.info("After decorator: TSDomainRefDecorator", DomainRefDecorator.class.getName());
    }

    GUI2TypeScriptAngularDecorator angularDecorator = new GUI2TypeScriptAngularDecorator(glex,
        isInputGui, viewComponents, hwPath, outPath);
    angularDecorator.transform(guiAst, decoratedAST);
    Log.info("After decorator: GUI2TypeScriptAngularDecorator",
        GUI2TypeScriptAngularDecorator.class.getName());

//    GUI2TypeScriptComponentsDecorator componentsDecorator =
//        new GUI2TypeScriptComponentsDecorator(glex, viewComponents);
//    componentsDecorator.transform(guiAst, domainRefDecorator.getDecoratedClass());
//    Log.info("After decorator: GUI2TypeScriptComponentsDecorator",
//        GUI2TypeScriptComponentsDecorator.class.getName());
  }

}
