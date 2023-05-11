package umlp.generator.domain.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.AbstractDecorator;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDeSerDecorator extends AbstractDecorator {
  protected List<ASTCDClass> addedDeSers = new ArrayList<>();

  public AbstractDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public List<ASTCDClass> getAddedDeSers() {
    return addedDeSers;
  }
}
