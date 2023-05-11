/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import umlp.generator.domain.decorator.DeSerDecorator;

import java.util.Set;

public class TSDeSerDecorator extends DeSerDecorator {

  public TSDeSerDecorator(GlobalExtensionManagement glex, MCPath hwPath,
      Set<String> supportedDesers) {
    super(glex, hwPath, supportedDesers);
  }

  @Override
  protected String getSerializeTemplate() {
    return "umlp.gui2ts.deser.serialize";
  }

  @Override
  protected String getDeserializeTemplate() {
    return "umlp.gui2ts.deser.deserialize";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.deser.BuildJson";
  }

  @Override
  protected String getFromJsonTemplate() {
    return "umlp.gui2ts.deser.FromJson";
  }

  @Override
  protected String getSerializeSimpleFieldTemplate() {
    return "umlp.gui2ts.deser.serializeSimpleField";
  }

  @Override
  protected String getDeserializeSimpleFieldTemplate() {
    return "umlp.gui2ts.deser.deserializeSimpleField";
  }

  @Override
  protected String getSerializeGemListFieldTemplate() {
    return "umlp.gui2ts.deser.serializeGemListField";
  }

  @Override
  protected String getDeserializeGemListFieldTemplate() {
    return "umlp.gui2ts.deser.deserializeGemListField";
  }

  @Override
  protected String getSerializeGemFieldTemplate() {
    return "umlp.gui2ts.deser.serializeGemField";
  }

  @Override
  protected String getDeserializeGemFieldTemplate() {
    return "umlp.gui2ts.deser.deserializeGemField";
  }

}
