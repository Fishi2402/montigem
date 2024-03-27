/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;
import org.jsweet.transpiler.EcmaScriptComplianceLevel;
import org.jsweet.transpiler.JSweetFactory;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.ModuleKind;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.util.ConsoleTranspilationHandler;
import org.jsweet.transpiler.util.ErrorCountTranspilationHandler;
import org.jsweet.transpiler.util.ProcessUtil;
import umlp.generator.gui.JSweetConfigGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class UMLPJava2TypeScriptTask extends DefaultTask {

  private static final String DOMAIN_OUTPUT_DIR = "frontendGen/build/appdomain/";

  private static final String GUI_OUTPUT_DIR = "frontendGen/build/appgui/";

  private static final String NODE_VERSION = "18.13.0";

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getIncludes();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getAdditionalIncludes();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getExcludes();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getAdditionalExcludes();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getExcludeTranspilation();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract ListProperty<String> getAdditionalExcludeTranspilation();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getVerbose();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getNodeVersion();

  @Input
  public abstract Property<String> getDomainModel();

  @OutputDirectory
  public abstract DirectoryProperty getOutputDir();

  @Input
  @Optional
  public abstract Property<Boolean> getGui();

  @Input
  @Optional
  public abstract Property<Boolean> getDomain();

  @Input
  @Optional
  public abstract Property<String> getHandWrittenConfigDir();

  protected List<String> includes;

  protected List<String> excludes;

  protected List<String> excludeTranspilaitons;

  @OutputDirectory
  public abstract DirectoryProperty getJsweetDir();

  @OutputDirectory
  public abstract DirectoryProperty getJsweetWorkingDir();

  @OutputDirectory
  public abstract DirectoryProperty getCandiesJsOutDir();

  public UMLPJava2TypeScriptTask() {
    getJsweetDir().convention(getProject().getLayout().getBuildDirectory().dir("jsweet"));
    getJsweetWorkingDir().convention(getProject().getLayout().getBuildDirectory().dir("jsweetWorkingDir"));
    getCandiesJsOutDir().convention(getJsweetWorkingDir().dir("candies"));
  }

  @TaskAction
  public void run(InputChanges inputChanges) {
    init();

    File baseDirectory = new File(this.getProject().getProjectDir(), "jsweet");
    JSweetFactory factory = new JSweetFactory();
    File workingDir = getJsweetWorkingDir().get().getAsFile();
    File jSweetDir = getJsweetDir().get().getAsFile();
    File candiesJsOut = getCandiesJsOutDir().get().getAsFile();

    File hwcDir = getHandWrittenConfigDir().isPresent() ? new File(jSweetDir, getHandWrittenConfigDir().get()) : baseDirectory;


    // Get the sourceSets for this gradle plugin
    SourceSetContainer sourceSets = (SourceSetContainer) this.getProject().getProperties()
        .get("sourceSets");
    SourceSet mainSources = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);

    File outputDir = getOutputDir().get().getAsFile();

    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
    File nodeInit = new File(workingDir, ".node-init");
    if (!nodeInit.exists()) {
      nodeInit.mkdirs();
    }

    syncClasspath();

    JSweetTranspiler transpiler = new JSweetTranspiler(baseDirectory, null, factory,
        workingDir, outputDir, outputDir, candiesJsOut,
        getProject().getConfigurations().getByName("runtimeClasspath").getAsPath());

    transpiler.setTscWatchMode(false);
    transpiler.setAutoPropagateAsyncAwaits(true);
    transpiler.setVerbose(getVerbose().get());
    transpiler.setEncoding("UTF-8");
    transpiler.setGenerateSourceMaps(true);
    transpiler.setGenerateJsFiles(false);
    transpiler.setEcmaTargetVersion(EcmaScriptComplianceLevel.ES6);
    transpiler.setModuleKind(ModuleKind.commonjs);
    ProcessUtil.addExtraPath(
        ".gradle/nodejs/node-v" + getNodeVersion().orElse(NODE_VERSION) + "-win-x64");

    SourceFile[] sourceFiles = collectSourceFiles(mainSources.getAllJava().getSrcDirs());
    Set<String> excludedSources = collectExcludedSources(sourceFiles,
        mainSources.getAllJava().getSrcDirs());

    ErrorCountTranspilationHandler transpilationHandler = new ErrorCountTranspilationHandler(
        new ConsoleTranspilationHandler());
    try {
      transpiler.transpile(transpilationHandler, excludedSources, sourceFiles);
      generateJSweetConfig(jSweetDir, baseDirectory);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void init() {
    computeIncludes();
    computeExcludes();
    computeExcludeTranspilation();
  }

  private static final String PATTERN_DOMAIN_MODEL = "**/%s/**/*.java";
  private static final String PATTERN_MANAGER_IMPL = "**/%s/%sManagerImpl*.java";
  private static final String PATTERN_MOCK = "**/%s/%sMock*.java";
  private static final String PATTERN_MOCK_TEST_SETUP = "**/%s/%sMockTestSetup*.java";
  private static final String PATTERN_DUMMY_PROVIDER = "**/%s/%sDummyProvider*.java";
  private static final String PATTERN_TEST_CONTEXT = "**/%s/%sTestContext*.java";
  private static final String PATTERN_SERVER_APPLICATION = "**/%s/%sServerApplication*.java";
  private static final String PATTERN_SHELL_API_APPLICATION = "**/%s/%sShellAPIApplication*.java";
  private static final String PATTERN_DAO_FACTORY = "**/%s/%sDAOFactory*.java";
  private static final String PATTERN_TEST_INFRASTRUCTURE = "**/%s/test_infrastructure/**";
  private static final String PATTERN_SHELLAPI = "**/%s/shellapi/**";
  private static final String PATTERN_DAO = "**/%s/*DAO.java";
  private static final String PATTERN_DAO_INTERFACE = "**/%s/*DAOInterface*.java";
  private static final String PATTERN_DAO_NEO4J = "**/%s/*DAONeo4J*.java";
  private static final String PATTERN_DB_OBSERVER = "**/%s/*DbObserver*.java";
  private static final String PATTERN_DOMAIN = "**/%s/**/*.java";
  // TODO: These filter-paths can not be a proper solution
  // We should use File Properties instead!
  // https://docs.gradle.org/current/userguide/lazy_configuration.html#working_with_files_in_lazy_properties
  private static final String PATTERN_GUI = "**/appgui/**/*.java"; // appgui = cd2gui output
  private static final String PATTERN_GUI2 = "**/gui/**/*.java";

  private void computeIncludes() {
    List<String> includes = new ArrayList<>();
    if (!getIncludes().isPresent() || getIncludes().get().isEmpty()) {
      includes.add(generatePattern(PATTERN_DOMAIN_MODEL));
      if (getGui().orElse(false).get()) {
        includes.add(PATTERN_GUI);
        includes.add(PATTERN_GUI2);
      }
    }
    else {
      includes = getIncludes().get();
    }
    if (getAdditionalIncludes().isPresent()) {
      includes.addAll(getAdditionalIncludes().get());
    }
    this.includes = includes;
  }

  private void computeExcludes() {
    List<String> excludes = new ArrayList<>();
    if (!getExcludes().isPresent() || getExcludes().get().isEmpty()) {
      excludes.addAll(Arrays.asList(
          generatePattern(PATTERN_MANAGER_IMPL),
          generatePattern(PATTERN_MOCK),
          generatePattern(PATTERN_MOCK_TEST_SETUP),
          generatePattern(PATTERN_DUMMY_PROVIDER),
          generatePattern(PATTERN_TEST_CONTEXT),
          generatePattern(PATTERN_SERVER_APPLICATION),
          generatePattern(PATTERN_SHELL_API_APPLICATION),
//          generatePattern(PATTERN_DAO),
//          generatePattern(PATTERN_DAO_NEO4J),
//          generatePattern(PATTERN_DAO_INTERFACE),
//          generatePattern(PATTERN_DAO_FACTORY),
          generatePattern(PATTERN_TEST_INFRASTRUCTURE),
          generatePattern(PATTERN_SHELLAPI)
      ));
    }
    else {
      excludes = getExcludes().get();
    }
    if (getAdditionalExcludes().isPresent()) {
      excludes.addAll(getAdditionalExcludes().get());
    }
    this.excludes = excludes;
  }

  private void computeExcludeTranspilation() {
    List<String> excludeTranspilation = new ArrayList<>();
    if (!getExcludeTranspilation().isPresent() || getExcludeTranspilation().get().isEmpty()) {
      if (getGui().orElse(false).get()) {
        excludeTranspilation.addAll(Arrays.asList(
            generatePattern(PATTERN_DOMAIN)
        ));
      } else if (getDomain().orElse(false).get()) {
        excludeTranspilation.addAll(Arrays.asList(
            generatePattern(PATTERN_MANAGER_IMPL),
            generatePattern(PATTERN_MOCK),
            generatePattern(PATTERN_MOCK_TEST_SETUP),
            generatePattern(PATTERN_DUMMY_PROVIDER),
            generatePattern(PATTERN_TEST_CONTEXT),
            generatePattern(PATTERN_DAO),
            generatePattern(PATTERN_DAO_NEO4J),
            generatePattern(PATTERN_DAO_INTERFACE),
            generatePattern(PATTERN_DAO_FACTORY),
            generatePattern(PATTERN_DB_OBSERVER)
        ));
      }
    }
    else {
      excludeTranspilation = getExcludeTranspilation().get();
    }
    if (getAdditionalExcludeTranspilation().isPresent()) {
      excludeTranspilation.addAll(getAdditionalExcludeTranspilation().get());
    }
    this.excludeTranspilaitons = excludeTranspilation;
  }

  private String generatePattern(String pattern) {
    return String.format(pattern, getDomainModel().get().toLowerCase(),
        getDomainModel().get());
  }

  private void syncClasspath() {
    URLClassLoader urlClassLoader = (URLClassLoader) this.getClass().getClassLoader();

    for (File runtimeClasspath : getRuntimeClasspaths()) {
      try {
        URL url = convertToURL(runtimeClasspath);
        if (!isUrlPresent(url, urlClassLoader)) {
          addUrlToClassLoader(url, urlClassLoader);
        }
      }
      catch (URISyntaxException | NoSuchMethodException | MalformedURLException |
             InvocationTargetException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private FileCollection getRuntimeClasspaths() {
    return getProject().getConfigurations().getByName("runtimeClasspath");
  }

  private URL convertToURL(File file) throws URISyntaxException, MalformedURLException {
    return file.toURI().toURL();
  }

  private boolean isUrlPresent(URL url, URLClassLoader urlClassLoader) throws URISyntaxException {
    return Arrays.stream(urlClassLoader.getURLs())
        .anyMatch(existingUrl -> {
          try {
            return url.toURI().equals(existingUrl.toURI());
          }
          catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void addUrlToClassLoader(URL url, URLClassLoader urlClassLoader)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
    addURLMethod.setAccessible(true);
    addURLMethod.invoke(urlClassLoader, url);
  }

  private static final String SOURCE_FILE_EXTENSION = ".java";

  private static final String LOGGER_NAME = "JSweet Plugin";

  private SourceFile[] collectSourceFiles(Collection<File> sourceDirs) {
    Log.info("source includes: " + ArrayUtils.toString(this.includes), LOGGER_NAME);
    Log.info("source excludes: " + ArrayUtils.toString(this.excludes), LOGGER_NAME);
    Log.info("sources dirs: " + sourceDirs, LOGGER_NAME);
    List<SourceFile> sources = new LinkedList<>();
    for (File sourceDir : sourceDirs) {
      if (sourceDir.exists()) {
        sources.addAll(scanDirectory(sourceDir, this.includes.toArray(new String[0]),
            this.excludes.toArray(new String[0])));
      }
    }
    Log.info("sourceFiles: " + sources, LOGGER_NAME);
    return sources.toArray(new SourceFile[0]);
  }

  /**
   * Filters out files from transpilation which are still part of the source files. This allows to
   * skip RTE implementation which otherwise would lead to a symbol not found Exception during java
   * compiling.
   *
   * @param sourceFiles Previously collected source files.
   * @return A set of files for which no code is generated.
   */
  private Set<String> collectExcludedSources(SourceFile[] sourceFiles,
      Collection<File> sourceDirs) {
    Log.info("exclude transpilation: " + ArrayUtils.toString(this.excludeTranspilaitons),
        LOGGER_NAME);
    List<SourceFile> includedTranspilationSources = new LinkedList<>();
    for (File sourceDir : sourceDirs) {
      if (sourceDir.exists()) {
        includedTranspilationSources.addAll(
            scanDirectory(sourceDir, this.includes.toArray(new String[0]),
                this.excludeTranspilaitons.toArray(new String[0])));
      }
    }
    Set<String> includedTranspilationSourcesAsString = includedTranspilationSources.stream()
        .map(SourceFile::toString).collect(Collectors.toSet());
    Set<String> excludedTranspilationSources = Arrays.stream(sourceFiles)
        .map(SourceFile::toString)
        .filter(s -> !includedTranspilationSourcesAsString.contains(s))
        .collect(Collectors.toSet());
    Log.info("excludedTranspilationSources: " + excludedTranspilationSources, LOGGER_NAME);
    return excludedTranspilationSources;
  }

  private List<SourceFile> scanDirectory(File sourceDir, String[] includes, String[] excludes) {
    DirectoryScanner dirScanner = new DirectoryScanner();
    dirScanner.setBasedir(sourceDir);
    dirScanner.setIncludes(includes);
    dirScanner.setExcludes(excludes);
    dirScanner.scan();
    List<SourceFile> sources = new LinkedList<>();
    for (String includedPath : dirScanner.getIncludedFiles()) {
      if (includedPath.endsWith(SOURCE_FILE_EXTENSION)) {
        sources.add(new SourceFile(new File(sourceDir, includedPath)));
      }
    }
    return sources;
  }

  /**
   * Generates configuration files required for the JSweet transpilation.
   *
   * @param targetDir The target directory the config files are generated to.
   */
  protected void generateJSweetConfig(File targetDir, File hwcDir) {
    GeneratorSetup gs = new GeneratorSetup();
    gs.setOutputDirectory(targetDir);
    gs.setHandcodedPath(new MCPath(hwcDir.toPath()));
    JSweetConfigGenerator generator = new JSweetConfigGenerator(gs);
    generator.generate(getDomainModel().get());

  }
}
