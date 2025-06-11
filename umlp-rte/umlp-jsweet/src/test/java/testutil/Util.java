package testutil;

import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.util.ProcessUtil;

import java.io.File;
import java.io.IOException;

public class Util {

  public static final String domainConfig = "src/test/resources/defaultJsweetConfig/domainConfig.json";
  public static final String defaultDomainOutput = "build/transpile/domainout";

  public static final String guiConfig = "src/test/resources/defaultJsweetConfig/guiConfig.json";
  public static final String defaultGUIOutput = "build/transpile/guiout";

  public static JSweetTranspiler getGUITranspiler() {
    return getGUITranspiler(new File(defaultGUIOutput));
  }

  public static JSweetTranspiler getDomainTranspiler() {
    return getDomainTranspiler(new File(defaultDomainOutput));
  }

  public static JSweetTranspiler getGUITranspiler(File outputDir) {
    return getTranspilerFromConfigFile( new File(guiConfig), outputDir);
  }

  public static JSweetTranspiler getDomainTranspiler(File outputDir) {
    return getTranspilerFromConfigFile(new File(domainConfig), outputDir);
  }

  public static JSweetTranspiler getTranspilerFromConfigFile(File configuration, File outputDir) {
    // TODO do not hardcode node
    ProcessUtil.addExtraPath(".gradle/nodejs/node-v" + "18.13.0" + "-win-x64");
    JSweetFactory factory = new JSweetFactory();
    JSweetTranspiler transpiler = null;
    try {
      transpiler = new JSweetTranspiler(
          configuration,
          factory,
          null,
          outputDir,
          null,
          null,
          System.getProperty("java.class.path")
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Set according to our JSweet Plugin
    transpiler.setTscWatchMode(false);
    transpiler.setGenerateJsFiles(false);
    transpiler.setModuleKind(ModuleKind.commonjs);
    transpiler.setAutoPropagateAsyncAwaits(true);
    transpiler.setVerbose(true);
    transpiler.setEncoding("UTF-8");
    transpiler.setGenerateSourceMaps(true);
    transpiler.setEcmaTargetVersion(EcmaScriptComplianceLevel.ES6);

    return transpiler;
  }
}
