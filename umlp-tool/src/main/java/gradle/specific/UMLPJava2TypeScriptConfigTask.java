/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import umlp.generator.gui.JSweetConfigGenerator;

public abstract class UMLPJava2TypeScriptConfigTask extends DefaultTask {

  @Input
  public abstract Property<String> getDomainModel();

  @Input
  @Optional
  public abstract Property<Boolean> getGenerateForDomain();

  @Input
  @Optional
  public abstract Property<Boolean> getGenerateForGui();

  @OutputDirectory
  public abstract DirectoryProperty getJsweetConfigOut();

  @Optional
  @InputDirectory
  public abstract DirectoryProperty getHwcDir();

  @TaskAction
  public void run() {
    generateJSweetConfig();
  }

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() {
    GeneratorSetup gs = new GeneratorSetup();
    gs.setOutputDirectory(getJsweetConfigOut().get().getAsFile());
    if (getHwcDir().isPresent()) {
      gs.setHandcodedPath(new MCPath(getHwcDir().get().getAsFile().toPath()));
    }
    JSweetConfigGenerator generator = new JSweetConfigGenerator(gs);
    generator.generate(getDomainModel().get(), getGenerateForDomain().getOrElse(!getGenerateForGui().getOrElse(true)));
  }
}
