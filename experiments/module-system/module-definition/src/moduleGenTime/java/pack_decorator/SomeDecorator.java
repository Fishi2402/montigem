package pack_decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDClass;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

public class SomeDecorator extends AbstractDecorator {
  public SomeDecorator(de.monticore.generating.templateengine.GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(de.monticore.cdbasis._ast.ASTCDCompilationUnit origCD, de.monticore.cdbasis._ast.ASTCDCompilationUnit decoratedCD) {
    System.err.println("Adding class AddedByDecorator");
    decoratedCD.getCDDefinition().addCDElement(withOrigin(createClass()));
  }

  private ASTCDClass createClass() {
    return UMLPMill.cDClassBuilder()
        .setName("AddedByDecorator")
        .setModifier(CDModifier.PUBLIC.build())
        .build();
  }
}
