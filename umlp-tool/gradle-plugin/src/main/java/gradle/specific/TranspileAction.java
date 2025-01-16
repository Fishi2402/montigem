package gradle.specific;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.ProcessUtil;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transpile using JSweet
 * A WorkAction to allow different classes
 */
public abstract class TranspileAction implements WorkAction<TranspileAction.Params> {

  @Override
  public void execute() {
    Params params = getParameters();

    JSweetFactory factory = new JSweetFactory();

    JSweetTranspiler transpiler = new JSweetTranspiler(
        params.getBaseDirectory().getAsFile().get(),
        null,
        factory,
        params.getWorkingDirectory().getAsFile().get(),
        params.getOutputDir().getAsFile().get(),
        params.getOutputDir().getAsFile().get(),
        params.getExtractedCandiesJavascriptDir().getAsFile().get(),
        params.getClassPath().get());

    transpiler.setTscWatchMode(false);
    transpiler.setAutoPropagateAsyncAwaits(true);
    transpiler.setVerbose(params.getVerbose().getOrElse(false));
    transpiler.setEncoding("UTF-8");
    transpiler.setGenerateSourceMaps(true);
    transpiler.setGenerateJsFiles(false);
    transpiler.setEcmaTargetVersion(EcmaScriptComplianceLevel.ES6);
    transpiler.setModuleKind(ModuleKind.commonjs);
    transpiler.setGenerateSourceMaps(false);
    ProcessUtil.addExtraPath(".gradle/nodejs/node-v" + params.getNodeVersion().get() + "-win-x64");

    SourceFile[] sourceFiles = params.getInput().getFiles().stream()
        .map(SourceFile::new).toArray(SourceFile[]::new);


    // collect excluded sources
    Set<String> excludedSources = params.getExcludedFromTranspilation().getFiles().stream()
        .map(SourceFile::new).map(SourceFile::toString).collect(Collectors.toSet());


    ErrorCountTranspilationHandler transpilationHandler = new ErrorCountTranspilationHandler(new ConsoleTranspilationHandler());
    try {
      transpiler.transpile(transpilationHandler, excludedSources, sourceFiles);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public interface Params extends WorkParameters {
    ConfigurableFileCollection getInput();

    ConfigurableFileCollection getExcludedFromTranspilation();

    Property<Boolean> getVerbose();

    DirectoryProperty getBaseDirectory();

    DirectoryProperty getWorkingDirectory();

    DirectoryProperty getOutputDir();

    DirectoryProperty getExtractedCandiesJavascriptDir();

    Property<String> getClassPath();

    Property<String> getNodeVersion();
  }
}
