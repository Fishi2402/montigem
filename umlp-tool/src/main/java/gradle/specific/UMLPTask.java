/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.Incremental;

@CacheableTask
@Deprecated // See output from prepareRun();
public abstract class UMLPTask extends UMLPSingleFileTask {

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental  // This Annotation requires custom java-logic to run correctly. See "getOtherInputDirectories()"
  public abstract DirectoryProperty getModelDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  // No @Incremental, changes of the Symbols lead to full rebuild
  public abstract DirectoryProperty getSymbolDir();

  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  // Do not add "@Incremental" (unless you really know what you are doing)
  public abstract DirectoryProperty getTmplDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getConfigTemplate();


  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getCdToGui();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getForClient();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUseSpring();


  public UMLPTask() {
    super("UMLPTask");

    getCdToGui().convention(false);
    getForClient().convention(false);
    getUseSpring().convention(false);
  }

  @Override
  protected void prepareRun(){
    throw new RuntimeException("Using <UMLPTask> is deprecated! The functionality will be removed after 20.07.2023!\n" +
        "Instead use these alternatives: \n" +
        "\t <UMLPBackendTask> to generate java backend\n" +
        "\t <UMLPCdToGuiTask> for cd2gui\n" +
        "\t <UMLPFrontendTask> to generate typescript frontend");
  }
}
