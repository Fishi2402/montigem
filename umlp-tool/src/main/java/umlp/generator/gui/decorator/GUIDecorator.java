package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class GUIDecorator extends GUIAbstractDecorator {

  public GUIDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    DomainRefDecorator domainRefDecorator = new DomainRefDecorator(glex);
    domainRefDecorator.transform(guiAst, decoratedAST);
    Log.info("After decorator: DomainRefDecorator", DomainRefDecorator.class.getName());

    GUIComponentsDecorator componentsDecorator = new GUIComponentsDecorator(glex);
    componentsDecorator.transform(guiAst, decoratedAST);
    Log.info("After decorator: GUIComponentsDecorator", GUIComponentsDecorator.class.getName());

  }

}
