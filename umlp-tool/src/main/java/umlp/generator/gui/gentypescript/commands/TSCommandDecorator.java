/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.commands.AddDecorator;
import umlp.generator.domain.decorator.commands.AggregatedCommandDeSerDecorator;
import umlp.generator.domain.decorator.commands.AssocCommandDecorator;
import umlp.generator.domain.decorator.commands.CalculateDecorator;
import umlp.generator.domain.decorator.commands.CommandDecorator;
import umlp.generator.domain.decorator.commands.CreateDecorator;
import umlp.generator.domain.decorator.commands.DeleteDecorator;
import umlp.generator.domain.decorator.commands.DerivedClassCommandDecorator;
import umlp.generator.domain.decorator.commands.GetDecorator;
import umlp.generator.domain.decorator.commands.GetFilteredDecorator;
import umlp.generator.domain.decorator.commands.ManagerSubscriptionDecorator;
import umlp.generator.domain.decorator.commands.RemoveDecorator;
import umlp.generator.domain.decorator.commands.SetDecorator;

import java.util.ArrayList;
import java.util.List;

public class TSCommandDecorator extends CommandDecorator {

  public TSCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    List<ASTCDClass> addedDeSers = new ArrayList<>();
    CreateDecorator createDecorator = new TSCreateDecorator(glex);
    createDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(createDecorator.getAddedDeSers());
    Log.info("After decorator: CreateDecorator", TSCommandDecorator.class.getName());

    DeleteDecorator deleteDecorator = new TSDeleteDecorator(glex);
    deleteDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(deleteDecorator.getAddedDeSers());
    Log.info("After decorator: DeleteDecorator", TSCommandDecorator.class.getName());

    GetDecorator getDecorator = new TSGetDecorator(glex);
    getDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(getDecorator.getAddedDeSers());
    Log.info("After decorator: GetDecorator", TSCommandDecorator.class.getName());

    SetDecorator setDecorator = new TSSetDecorator(glex, supportedDeSers);
    setDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(setDecorator.getAddedDeSers());
    Log.info("After decorator: SetDecorator", TSCommandDecorator.class.getName());

    TSOwnCommandDecorator cmdDecorator = new TSOwnCommandDecorator(glex, supportedDeSers);
    cmdDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(cmdDecorator.getAddedDeSers());
    Log.info("After decorator: TSOwnCommandDecorator", CommandDecorator.class.getName());

    CalculateDecorator calculateDecorator = new TSCalculateDecorator(glex, supportedDeSers);
    calculateDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(calculateDecorator.getAddedDeSers());
    Log.info("After decorator: CalculateDecorator", TSCommandDecorator.class.getName());

    AddDecorator addDecorator = new TSAddDecorator(glex, supportedDeSers);
    addDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(addDecorator.getAddedDeSers());
    Log.info("After decorator: AddDecorator", TSCommandDecorator.class.getName());

    RemoveDecorator removeDecorator = new TSRemoveDecorator(glex, supportedDeSers);
    removeDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(removeDecorator.getAddedDeSers());
    Log.info("After decorator: RemoveDecorator", TSCommandDecorator.class.getName());

    GetFilteredDecorator getFilteredDecorator = new TSGetFilteredDecorator(glex);
    getFilteredDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(getFilteredDecorator.getAddedDeSers());
    Log.info("After decorator: GetFilteredDecorator", TSCommandDecorator.class.getName());

    AssocCommandDecorator assocCommandDecorator = new TSAssocCommandDecorator(glex);
    assocCommandDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(assocCommandDecorator.getAddedDeSers());
    Log.info("After decorator: AssocCommandDecorator", TSCommandDecorator.class.getName());

    DerivedClassCommandDecorator derivedClassCommandDecorator = new TSDerivedClassCommandDecorator(glex);
    derivedClassCommandDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(derivedClassCommandDecorator.getAddedDeSers());
    Log.info("After decorator: DerivedClassCommandDecorator", TSCommandDecorator.class.getName());


    ManagerSubscriptionDecorator managerSubscriptionDecorator = new TSManagerSubscriptionDecorator(glex);
    managerSubscriptionDecorator.transform(cdAST, decoratedAST);
    addedDeSers.addAll(managerSubscriptionDecorator.getAddedDeSers());
    Log.info("After decorator: ManagerSubscriptionCommandDecorator", TSCommandDecorator.class.getName());

    AggregatedCommandDeSerDecorator aggregatedCommandDeSerDecorator = new TSAggregatedCommandDeSerDecorator(glex, addedDeSers);
    aggregatedCommandDeSerDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: XCommandDeserDecorator", TSCommandDecorator.class.getName());
  }

}
