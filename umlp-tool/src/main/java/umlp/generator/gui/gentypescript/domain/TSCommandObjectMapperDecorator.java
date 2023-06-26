/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;
import umlp.generator.domain.decorator.commands.AggregatedCommandDeSerDecorator;
import umlp.generator.util.PathUtil;

public class TSCommandObjectMapperDecorator extends CommandObjectMapperDecorator {

  protected String astcdPackage;
  protected ASTCDCompilationUnit origCD;

  public TSCommandObjectMapperDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  @Override
  protected String getInitTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSInit";
  }

  @Override
  protected String getSerializeCommandTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSSerializeCommand";
  }

  @Override
  protected String getDeserializeCommandTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSDeserializeCommand";
  }

  @Override
  protected String getSerializeResponseTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSSerializeResponse";
  }

  @Override
  protected String getDeserializeResponseTemplate() {
    return "umlp.gui2ts.commandobjectmapper.TSDeserializeResponse";
  }

  @Override
  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandObjectMapper"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("Command"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandResult"));
    String name = origCD.getCDDefinition().getName();
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + name + CommandResultDeSerDecorator.SUFFIX);
    getCD4C().addImport(clazz, this.astcdPackage + "."
        + name + AggregatedCommandDeSerDecorator.SUFFIX);
  }


}
