package umlp.generator.domain.decorator.commands;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;

import java.util.ArrayList;
import java.util.Arrays;
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
