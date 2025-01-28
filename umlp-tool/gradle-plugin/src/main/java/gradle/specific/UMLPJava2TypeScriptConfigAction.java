/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import umlp.generator.gui.JSweetConfigGenerator;

public abstract class UMLPJava2TypeScriptConfigAction implements WorkAction<UMLPJava2TypeScriptConfigAction.Params> {


  @Override
  public void execute() {
    this.generateJSweetConfig();
  }

  /**
   * Generates configuration files required for the JSweet transpilation.
   */
  protected void generateJSweetConfig() {
    GeneratorSetup gs = new GeneratorSetup();
    gs.setOutputDirectory(getParameters().getJsweetConfigOut().get().getAsFile());
    if (getParameters().getHwcDir().isPresent()) {
      gs.setHandcodedPath(new MCPath(getParameters().getHwcDir().get().getAsFile().toPath()));
    }
    JSweetConfigGenerator generator = new JSweetConfigGenerator(gs);
    generator.generate(getParameters().getDomainModel().get(), getParameters().getGenerateForDomain().getOrElse(!getParameters().getGenerateForGui().getOrElse(true)));
  }

  interface Params extends WorkParameters  {

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
  }
}
