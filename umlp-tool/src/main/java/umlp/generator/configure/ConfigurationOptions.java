/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.configure;

/**
 * The names of the specific options used in this configuration.
 */
public enum ConfigurationOptions {

  MODEL("model"), MODEL_SHORT("m"), MODELS("models"),
  OCL("ocl"),
  MODELPATH("modelPath"), MODELPATH_SHORT("mp"),
  OUT("out"), OUT_SHORT("o"),
  TEST_OUT("testOut"), TEST_OUT_SHORT("tO"),
  HANDCODEDPATH("handcodedPath"), HANDCODEDPATH_SHORT("hcp"),
  PACKAGE("package"), PACKAGE_SHORT("p"),
  TEMPLATEPATH("templatePath"), TEMPLATEPATH_SHORT("fp"),
  GRAMMARS("grammars");

  String name;

  ConfigurationOptions(String name) {
    this.name = name;
  }

  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }

}
