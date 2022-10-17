package umlp.generator.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import umlp.generator.decorator.AbstractDecorator;

public class CommandDecorator extends AbstractDecorator {

  public CommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit cdAST, ASTCDCompilationUnit decoratedAST) {
    CreateDecorator createDecorator = new CreateDecorator(glex);
    createDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: CreateDecorator", CommandDecorator.class.getName());

    DeleteDecorator deleteDecorator = new DeleteDecorator(glex);
    deleteDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: DeleteDecorator", CommandDecorator.class.getName());

    GetDecorator getDecorator = new GetDecorator(glex);
    getDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: GetDecorator", CommandDecorator.class.getName());

    SetDecorator setDecorator = new SetDecorator(glex);
    setDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: SetDecorator", CommandDecorator.class.getName());

    AssocCommandDecorator addDecorator = new AssocCommandDecorator(glex);
    addDecorator.transform(cdAST, decoratedAST);
    Log.info("After decorator: AssocCommandDecorator", CommandDecorator.class.getName());

  }

}
