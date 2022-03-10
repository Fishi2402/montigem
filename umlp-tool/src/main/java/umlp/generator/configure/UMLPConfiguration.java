/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.configure;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static umlp.generator.configure.ConfigurationOptions.*;

/**
 * Provides access to the aggregated configuration of a DEx instance derived
 * from (1) its command line arguments, and (2) system properties (not
 * implemented yet).
 *
 * @author (last commit) $Author$
 */
public final class UMLPConfiguration implements Configuration {

  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";

  public static final String DEFAULT_TEST_OUTPUT_DIRECTORY = "test";

  private final Configuration configuration;

  /**
   * Constructor for {@link UMLPConfiguration}
   */
  private UMLPConfiguration(Configuration internal) {
    //    HWC_EXTENSIONS.addAll(FRONTEND_HWC_EXTENSIONS);
    this.configuration = internal;
  }

  /**
   * Factory method for {@link UMLPConfiguration}.
   */
  public static UMLPConfiguration withConfiguration(Configuration configuration) {
    return new UMLPConfiguration(configuration);
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<Path> toFileList(List<String> files) {
    return files.stream().collect(
        Collectors.mapping(
            file -> new File(file).getAbsoluteFile().toPath(), Collectors.toList())
    );
  }


  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(java.lang.String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }

  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(java.lang.String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }

  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(java.lang.String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }

  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(java.lang.String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }

  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(java.lang.String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }

  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(java.lang.String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }

  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(java.lang.String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }

  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(java.lang.String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }

  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(java.lang.String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }

  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(java.lang.String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    return this.configuration.getValues(key);
  }

  @Override
  public boolean hasProperty(String s) {
    return this.configuration.hasProperty(s);
  }

  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }

  public File getFileFromOptions(ConfigurationOptions option, ConfigurationOptions shortOption, String name) {
    Optional<String> model = getAsString(option);
    if (model.isPresent()) {
      return new File(model.get());
    }
    model = getAsString(shortOption);
    if (model.isPresent()) {
      return new File(model.get());
    }

    // no default; must specify model file to process
    Log.error("Please specify the " + name + " file.");
    // this return statement is bogus; hopefully fail quick is never deactivated
    // here!
    return new File("");
  }

  public File getFileFromOptionWithDefault(ConfigurationOptions option, ConfigurationOptions shortOption, String defaultValue) {
    Optional<String> out = getAsString(option);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(shortOption);
    if (out.isPresent()) {
      return new File(out.get());
    }
    return new File(defaultValue);
  }

  /**
   * Getter for the model file to parse as stored in this configuration.
   *
   * @return input model file
   */
  public File getModel() {
    return getFileFromOptions(MODEL, MODEL_SHORT, "model");
  }

  /**
   * Getter for the {@link MCPath} consisting of model files stored in
   * this configuration.
   *
   * @return iterable model files
   */
  public MCPath getModels() {
    Optional<List<String>> modelFiles = getAsStrings(MODELS);
    if (modelFiles.isPresent() && checkPath(modelFiles.get())) {
      return new MCPath(toFileList(modelFiles.get()));
    }
    // no default; must specify model files/directories to process
    Log.warn("0xA1013 Please specify the model file(s).");
    return new MCPath();
  }

  private boolean checkPath(List<String> models) {
    for (String g : models) {
      Path p = Paths.get(g);
      Log.info("model " + p, "DexScript");
      if (!Files.exists(p)) {
        Log.error("0xA1019 The requested path " + p.toString() + " does not exist.");
        return false;
      }
    }
    return true;
  }

  /**
   * Getter for the actual value of the model argument. This is not the prepared
   * {@link File} as in {@link UMLPConfiguration#getModel()} but the raw input
   * argument.
   *
   * @return the path and name of the model
   */
  public String getModelAsString() {
    Optional<String> model = getAsString(MODEL);
    if (model.isPresent()) {
      return model.get();
    }
    model = getAsString(MODEL_SHORT);
    if (model.isPresent()) {
      return model.get();
    }
    // no default; must specify model file to process
    Log.error("Please specify the model file.");
    return "";
  }

  public String getOCL() {
    Optional<String> model = getAsString(OCL);
    return model.orElse("");
  }

  /**
   * Getter for the list of model path elements (files and directories) stored
   * in this configuration.
   *
   * @return list of model path files
   */
  public MCPath getModelPath() {
    Optional<MCPath> modelPath = getAsStrings(MODELPATH)
        .map(this::convertEntryNamesToMCPath);

    if (modelPath.isPresent()) {
      return modelPath.get();
    }
    modelPath = getAsStrings(MODELPATH_SHORT)
        .map(this::convertEntryNamesToMCPath);
    if (modelPath.isPresent()) {
      return modelPath.get();
    }
    // default model path is empty
    return new MCPath();
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "dex".
   *
   * @return output directory file
   */
  public File getOut() {
    return getFileFromOptionWithDefault(OUT, OUT_SHORT, DEFAULT_OUTPUT_DIRECTORY);
  }

  /**
   * Getter for the output directory for the tests stored in this configuration. A fallback
   * default is "dex".
   *
   * @return output directory file
   */
  public File getTestOut() {
    return getFileFromOptionWithDefault(TEST_OUT, TEST_OUT_SHORT, DEFAULT_TEST_OUTPUT_DIRECTORY);
  }

  /**
   * Getter for the handcoded path directories stored in this configuration.
   *
   * @return iterable handcoded files
   */
  public MCPath getHandcodedPath() {
    Optional<List<String>> handcodedPath = getAsStrings(HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return new MCPath(toFileList(handcodedPath.get()));
    }
    handcodedPath = getAsStrings(HANDCODEDPATH_SHORT);
    if (handcodedPath.isPresent()) {
      return new MCPath(toFileList(handcodedPath.get()));
    }
    // default handcoded path is empty
    return new MCPath();
  }

  /**
   * Getter for the actual value of the handcoded path argument. This is not the
   * prepared {@link MCPath} as in
   * {@link UMLPConfiguration#getHandcodedPath()} but the raw input arguments.
   *
   * @return the list of handcoded paths
   */
  public List<String> getHandcodedPathAsStrings() {
    Optional<List<String>> handcodedPath = getAsStrings(HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return handcodedPath.get();
    }
    handcodedPath = getAsStrings(HANDCODEDPATH_SHORT);
    if (handcodedPath.isPresent()) {
      return handcodedPath.get();
    }
    // default handcoded path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the package path stored in this configuration.
   *
   * @return iterable package files
   */
  public List<String> getPackagePath() {
    Optional<String> packagePath = getAsString(PACKAGE);
    if (packagePath.isPresent()) {
      return Arrays.asList(packagePath.get().split("/"));
    }
    packagePath = getAsString(PACKAGE_SHORT);
    if (packagePath.isPresent()) {
      return Arrays.asList(packagePath.get().split("/"));
    }
    // default handcoded path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the target path directories stored in this configuration.
   *
   * @return iterable template files
   */
  public MCPath getTemplatePath() {
    Optional<List<String>> templatePath = getAsStrings(TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return new MCPath(toFileList(templatePath.get()));
    }
    templatePath = getAsStrings(TEMPLATEPATH_SHORT);
    if (templatePath.isPresent()) {
      return new MCPath(toFileList(templatePath.get()));
    }
    // default template path is empty
    return new MCPath();
  }

  /**
   * Getter for the actual value of the template path argument. This is not the
   * prepared {@link MCPath} as in
   * {@link UMLPConfiguration#getTemplatePath()} but the raw input arguments.
   *
   * @return the paths of the templates
   */
  public List<String> getTemplatePathAsStrings() {
    Optional<List<String>> templatePath = getAsStrings(TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    templatePath = getAsStrings(TEMPLATEPATH_SHORT);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    // default template path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the actual value of the grammars. This is not the
   * provided String but the extracted GrammarConfig.
   *
   * @return the list of grammars
   */
  public List<String> getGrammarConfig() {
    Optional<List<String>> grammarPath = getAsStrings(GRAMMARS);
    if (grammarPath.isPresent()) {
      return grammarPath.get();
    }
    // default template path is empty
    return Collections.emptyList();
  }

  protected MCPath convertEntryNamesToMCPath(List<String> modelPathEntryNames) {
    List<Path> modelPathFiles = toFileList(modelPathEntryNames);
    List<Path> modelPathEntries = modelPathFiles.stream()
        .map(Path::toAbsolutePath)
        .collect(Collectors.toList());
    return new MCPath(modelPathEntries);
  }

}

