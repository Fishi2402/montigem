/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp._visitor.UMLPTraverser;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.commands.AggregatedCommandDeSerDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class TSAggregatedCommandDeSerDecorator extends AggregatedCommandDeSerDecorator {

  public TSAggregatedCommandDeSerDecorator(GlobalExtensionManagement glex, List<ASTCDClass> deSers) {
    super(glex, deSers);
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.aggregatedcommanddeser.Constructor";
  }
}
