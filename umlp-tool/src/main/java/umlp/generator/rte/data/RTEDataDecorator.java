package umlp.generator.rte.data;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BEDecorator;

public class RTEDataDecorator extends AbstractDecorator {

  protected MCPath hwPath;

  public RTEDataDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {

    RTEPojoDecorator RTEPojoDecorator;

    RTEPojoDecorator = new RTEPojoDecorator(glex, hwPath);
    RTEPojoDecorator.transform(origCD, decoratedCD);
    Log.info("After decorator: NoGemListDecorator", RTEDataDecorator.class.getName());

    RTEBuilderDecorator RTEBuilderDecorator;
    RTEBuilderDecorator = new RTEBuilderDecorator(glex);
    RTEBuilderDecorator.transform(origCD, decoratedCD);
    Log.info("After decorator: BuilderDecorator", RTEDataDecorator.class.getName());

    RTEDeSerDecorator RTEDeSerDecorator;
    RTEDeSerDecorator = new RTEDeSerDecorator(glex, BEDecorator.getStandardSupportedDeSers());
    RTEDeSerDecorator.transform(origCD, decoratedCD);
    Log.info("After decorator: RTEDeSerDecorator", RTEDataDecorator.class.getName());
  }
}
