/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.generating.GeneratorSetup;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class JSweetConfigGeneratorTest {

  @Test
  public void testGenerate() {
    GeneratorSetup gs = new GeneratorSetup();
    gs.setOutputDirectory(new File("target/generated-test-sources/jsweet/"));
    JSweetConfigGenerator generator = new JSweetConfigGenerator(gs);
    generator.generate("MyDom", Collections.emptyList());

    File json = new File("target/generated-test-sources/jsweet/jsweetconfig.json");
    File adapter = new File("target/generated-test-sources/jsweet/jsweet_extension/umlp/jsweet/extension/domainAdapters/MyDomImportAdapter.java");

    assertTrue(json.exists());
    assertTrue(adapter.exists());
  }

}
