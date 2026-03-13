package sd2test.umlp.args;

import sd2test.umlp.args.Option.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SD2TestToolArgs {

  private final String domainModelPath;
  private final List<String> modelPaths;
  private final String backendSymbolDirectoryPath;
  private final String testSymbolDirectoryPath;
  private final String outputDirectoryPath;
  private final String hwcDirectoryPath;

  public SD2TestToolArgs(String domainModelPath,
      List<String> modelPaths,
      String backendSymbolDirectoryPath,
      String testSymbolDirectoryPath,
      String outputDirectoryPath,
      String hwcDirectoryPath) {
    this.domainModelPath = domainModelPath;
    this.modelPaths = modelPaths;
    this.backendSymbolDirectoryPath = backendSymbolDirectoryPath;
    this.testSymbolDirectoryPath = testSymbolDirectoryPath;
    this.outputDirectoryPath = outputDirectoryPath;
    this.hwcDirectoryPath = hwcDirectoryPath;
  }

  public static SD2TestToolArgs parse(String[] args) throws IllegalArgumentException {
    return new Parser().parse(args);
  }

  public String domainModelPath() {
    return domainModelPath;
  }

  public List<String> modelPaths() {
    return modelPaths;
  }

  public String backendSymbolDirectoryPath() {
    return backendSymbolDirectoryPath;
  }

  public String testSymbolDirectoryPath() {
    return testSymbolDirectoryPath;
  }

  public String outputDirectoryPath() {
    return outputDirectoryPath;
  }

  public String hwcDirectoryPath() {
    return hwcDirectoryPath;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    SD2TestToolArgs that = (SD2TestToolArgs) o;
    return Objects.equals(domainModelPath, that.domainModelPath) &&
           Objects.equals(modelPaths, that.modelPaths) &&
           Objects.equals(backendSymbolDirectoryPath, that.backendSymbolDirectoryPath) &&
           Objects.equals(testSymbolDirectoryPath, that.testSymbolDirectoryPath) &&
           Objects.equals(outputDirectoryPath, that.outputDirectoryPath) &&
           Objects.equals(hwcDirectoryPath, that.hwcDirectoryPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domainModelPath,
                        modelPaths,
                        backendSymbolDirectoryPath,
                        testSymbolDirectoryPath,
                        outputDirectoryPath,
                        hwcDirectoryPath);
  }

  private static class Parser {

    private final OptionParser optionParser = new OptionParser();
    private final Option<String> domainModelPath = optionParser.addOption("domainModel",
                                                                          Context::getNext);
    private final Option<List<String>> modelPath = optionParser.addOption("model", ctx -> {
      List<String> paths = new ArrayList<>(List.of(ctx.getNext()));
      while (ctx.nextIsValue()) paths.add(ctx.getNext());
      return paths;
    }).withMergeFunction((currentValue, additionalValue) -> {
      currentValue.addAll(additionalValue);
      return currentValue;
    });
    private final Option<String> backendSymbolDirectoryPath = optionParser.addOption(
        "backendSymbolDirectory",
        Context::getNext);
    private final Option<String> testSymbolDirectoryPath = optionParser.addOption(
        "testSymbolDirectory",
        Context::getNext);
    private final Option<String> outputDirectoryPath = optionParser.addOption("outputDirectory",
                                                                              Context::getNext);
    private final Option<String> hwcDirectoryPath = optionParser.addOption("hwcDirectory",
                                                                           Context::getNext);

    public SD2TestToolArgs parse(String[] args) throws IllegalArgumentException {
      optionParser.parse(args);

      return new SD2TestToolArgs(domainModelPath.expectValue(),
                                 modelPath.expectValue(),
                                 backendSymbolDirectoryPath.getValue().orElse(null),
                                 testSymbolDirectoryPath.getValue().orElse(null),
                                 outputDirectoryPath.expectValue(),
                                 hwcDirectoryPath.expectValue());
    }
  }
}
