package de.montigem.gradle.specific.ocl;

import de.monticore.gradle.common.AToolAction;
import de.montigem.gradle.common.MGAllFilesTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class OCL2JavaTask extends MGAllFilesTask {

  @Input
  @Optional
  public abstract Property<String> getPrefixPackage();

  @Input
  public abstract ListProperty<String> getPreloadDiagrams();

  @Input
  public abstract ListProperty<String> getDomainModels();

  public OCL2JavaTask() {
    super("OCL2Java");
    getPreloadDiagrams().convention(List.of());
  }

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> args = new ArrayList<>();

    args.add("-ocl");
    getInputFilesAsStream()
        .forEach(f -> {
          args.add(handlePath.apply(f.toPath()));
        });

    args.add("-symbols");
    args.addAll(
        getSymbolPath().getFiles().stream()
            .map(File::getAbsolutePath)
            .collect(Collectors.toList())
    );

    args.add("-out");
    args.add(getOutputDir().get().getAsFile().getAbsolutePath());

    if(getPreloadDiagrams().isPresent() && !getPreloadDiagrams().get().isEmpty()){
      args.add("-preload");
      args.addAll(getPreloadDiagrams().get());
    }

    if(getDomainModels().isPresent() && !getDomainModels().get().isEmpty()){
      args.add("-domain");
      args.addAll(getDomainModels().get());
    }

    if(getPrefixPackage().isPresent() && !getPrefixPackage().get().isEmpty()){
      args.add("-prefixPackage");
      args.add(getPrefixPackage().get());
    }

    return args;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return OCL2JavaToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return OCL2JavaAction.class;
  }
}
