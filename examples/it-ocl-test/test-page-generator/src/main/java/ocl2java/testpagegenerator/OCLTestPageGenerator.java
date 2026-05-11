package ocl2java.testpagegenerator;

import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.ocl.ocl._ast.ASTOCLContextDefinition;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import oclwitherrormessages.OCLWithErrorMessagesMill;
import oclwitherrormessages._parser.OCLWithErrorMessagesParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class OCLTestPageGenerator {
  private static final String GUI_ROOT_PACKAGE = "ocl2java";
  private static final String GUI_DOMAIN_PACKAGE = "Mydomain";
  private static final String JAVA_DOMAIN_PACKAGE = "mydomain";
  private static final String DEFAULT_CONTENT = "Click a button to fill";
  private static final Set<String> JAVA_KEYWORDS = Set.of(
      "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
      "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
      "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
      "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
      "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
      "volatile", "while");

  private OCLTestPageGenerator() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 3) {
      throw new IllegalArgumentException(
          "Usage: OCLTestPageGenerator <oclInputDir> <guiOutputDir> <javaOutputDir> [excludeGlob...]");
    }

    Path inputDir = Path.of(args[0]);
    Path outputDir = Path.of(args[1]);
    Path javaOutputDir = Path.of(args[2]);

    if (!Files.isDirectory(inputDir)) {
      throw new IllegalArgumentException("OCL input directory does not exist: " + inputDir);
    }

    OCLWithErrorMessagesMill.init();
    recreateDirectory(outputDir);
    recreateDirectory(javaOutputDir);

    for (Path oclFile : collectOclFiles(inputDir, excludeMatchers(args))) {
      generate(parse(oclFile), outputDir, javaOutputDir);
    }
  }

  private static List<PathMatcher> excludeMatchers(String[] args) {
    List<PathMatcher> matchers = new ArrayList<>();
    for (int i = 3; i < args.length; i++) {
      matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + args[i]));
    }
    return matchers;
  }

  private static List<Path> collectOclFiles(Path inputDir, List<PathMatcher> excludeMatchers) throws IOException {
    try (Stream<Path> stream = Files.walk(inputDir)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith(".ocl"))
          .filter(path -> excludeMatchers.stream().noneMatch(matcher -> matcher.matches(inputDir.relativize(path))))
          .sorted()
          .toList();
    }
  }

  private static ASTOCLCompilationUnit parse(Path oclFile) throws IOException {
    OCLWithErrorMessagesParser parser = OCLWithErrorMessagesMill.parser();
    Optional<ASTOCLCompilationUnit> ast = parser.parse(oclFile.toString());
    if (parser.hasErrors() || ast.isEmpty()) {
      throw new IllegalStateException("Could not parse OCL file: " + oclFile);
    }
    return ast.get();
  }

  private static void generate(ASTOCLCompilationUnit ast, Path guiOutputDir, Path javaOutputDir) throws IOException {
    List<ASTOCLInvariant> invariants = ast.getOCLArtifact().streamOCLConstraints()
        .filter(ASTOCLInvariant.class::isInstance)
        .map(ASTOCLInvariant.class::cast)
        .toList();
    if (invariants.isEmpty()) {
      return;
    }

    String artifactName = ast.getOCLArtifact().getName();
    String pageName = artifactName + "Page";
    String guiPackage = guiPackage(ast).toLowerCase();
    String oclPackage = oclPackage(ast);
    Map<String, String> contextTypeToGetter = collectContextTypes(invariants);

    Path guiPackagePath = guiOutputDir.resolve(guiPackage.replace('.', '/'));
    Files.createDirectories(guiPackagePath);
    Files.writeString(guiPackagePath.resolve(pageName + ".gui"),
        renderGui(guiPackage, pageName, artifactName, contextTypeToGetter, invariants),
        StandardCharsets.UTF_8);

    Path javaPackagePath = javaOutputDir.resolve(guiPackage.replace('.', '/'));
    Files.createDirectories(javaPackagePath);
    Files.writeString(javaPackagePath.resolve(pageName + ".java"),
        renderJava(guiPackage, oclPackage, pageName, artifactName, contextTypeToGetter, invariants),
        StandardCharsets.UTF_8);
  }

  private static String guiPackage(ASTOCLCompilationUnit ast) {
    if (ast.isEmptyPackage()) {
      return GUI_ROOT_PACKAGE;
    }
    return GUI_ROOT_PACKAGE + "." + String.join(".", ast.getPackageList());
  }

  private static String oclPackage(ASTOCLCompilationUnit ast) {
    if (ast.isEmptyPackage()) {
      return "";
    }
    return String.join(".", ast.getPackageList());
  }

  private static Map<String, String> collectContextTypes(List<ASTOCLInvariant> invariants) {
    Map<String, String> result = new LinkedHashMap<>();
    for (ASTOCLInvariant invariant : invariants) {
      for (ASTOCLContextDefinition context : invariant.getOCLContextDefinitionList()) {
        contextType(context).ifPresent(type -> result.putIfAbsent(type, pluralize(lowerFirst(type))));
      }
    }
    return result;
  }

  private static Optional<String> contextType(ASTOCLContextDefinition context) {
    if (context.isPresentOCLParamDeclaration()) {
      return Optional.of(typeName(context.getOCLParamDeclaration().getMCType()));
    }
    if (context.isPresentMCType()) {
      return Optional.of(typeName(context.getMCType()));
    }
    return Optional.empty();
  }

  private static String typeName(ASTMCType type) {
    String printed = OCLWithErrorMessagesMill.prettyPrint(type, false).trim();
    int lastDot = printed.lastIndexOf('.');
    if (lastDot >= 0) {
      printed = printed.substring(lastDot + 1);
    }
    return sanitizeJavaIdentifier(printed);
  }

  private static String renderGui(
      String guiPackage,
      String pageName,
      String artifactName,
      Map<String, String> contextTypes,
      List<ASTOCLInvariant> invariants) {
    StringBuilder builder = new StringBuilder();
    builder.append("package ").append(guiPackage).append(";\n\n");
    builder.append("import mc.fenix.basic.GemText;\n");
    builder.append("import mc.fenix.basic.GemButton;\n\n");

    for (String type : contextTypes.keySet()) {
      builder.append("import ").append(GUI_DOMAIN_PACKAGE).append(".").append(type).append(";\n");
    }
    builder.append("\n");

    builder.append("page ").append(pageName).append("(");
    List<String> params = new ArrayList<>();
    contextTypes.forEach((type, name) -> params.add("List<" + type + "> " + name));
    params.add("String content = " + quote(DEFAULT_CONTENT));
    builder.append(String.join(", ", params));
    builder.append(") {\n");

    for (int i = 0; i < invariants.size(); i++) {
      String methodName = buttonMethodName(artifactName, invariants.get(i), i, invariants.size());
      String label = invariants.size() == 1 ? artifactName : artifactName + ".inv" + i;
      builder.append("  ")
          .append(methodName)
          .append("@GemButton(label=")
          .append(quote(label))
          .append(", leftClick=")
          .append(methodName)
          .append("());\n");
    }
    builder.append("  resultGemText@GemText(value=content);\n");
    builder.append("}\n");
    return builder.toString();
  }

  private static String renderJava(
      String guiPackage,
      String oclPackage,
      String pageName,
      String artifactName,
      Map<String, String> contextTypeToGetter,
      List<ASTOCLInvariant> invariants) {
    StringBuilder builder = new StringBuilder();
    builder.append("package ").append(guiPackage).append(";\n\n");

    for (String type : contextTypeToGetter.keySet()) {
      builder.append("import ").append(JAVA_DOMAIN_PACKAGE).append(".").append(type).append(";\n");
    }
    builder.append("import ").append(oclJavaPackage(oclPackage)).append(".").append(artifactName).append(";\n");
    builder.append("import umlp.jsweet.extension.annotation.Component;\n\n");
    builder.append("import java.util.HashSet;\n");
    builder.append("import java.util.Set;\n");
    builder.append("import java.util.function.Consumer;\n\n");

    builder.append("@Component\n");
    builder.append("public class ").append(pageName).append(" extends ").append(pageName).append("TOP {\n");
    for (int i = 0; i < invariants.size(); i++) {
      builder.append(renderButtonImplementation(artifactName, invariants.get(i), i, invariants.size(), contextTypeToGetter));
    }
    builder.append("}\n");
    return builder.toString();
  }

  private static String oclJavaPackage(String oclPackage) {
    if (oclPackage.isEmpty()) {
      return JAVA_DOMAIN_PACKAGE;
    }
    return JAVA_DOMAIN_PACKAGE + "." + oclPackage;
  }

  private static String renderButtonImplementation(
      String artifactName,
      ASTOCLInvariant invariant,
      int index,
      int invariantCount,
      Map<String, String> contextTypeToGetter) {
    List<ContextParameter> parameters = contextParameters(invariant);
    String methodName = buttonMethodName(artifactName, invariant, index, invariantCount);

    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    builder.append("  @Override\n");
    builder.append("  public Consumer<Void> ").append(methodName).append("() {\n");
    builder.append("    return $ -> {\n");
    builder.append("      Set<String> res = new HashSet<>();\n");
    if (parameters.isEmpty()) {
      builder.append("      if (!").append(artifactName).append(".checkInv").append(index).append("()) {\n");
      builder.append("        res.add(\"true\");\n");
      builder.append("      }\n");
    }
    else {
      for (int level = 0; level < parameters.size(); level++) {
        ContextParameter parameter = parameters.get(level);
        indent(builder, level).append("      for (").append(parameter.type()).append(" ")
            .append(parameter.name()).append(" : get")
            .append(upperFirst(contextTypeToGetter.get(parameter.type())))
            .append("()) {\n");
      }

      indent(builder, parameters.size()).append("      if (!").append(artifactName).append(".checkInv")
          .append(index).append("(");
      builder.append(String.join(", ", parameters.stream().map(ContextParameter::name).toList()));
      builder.append(")) {\n");
      indent(builder, parameters.size() + 1).append("      res.add(\"\" + ")
          .append(parameters.get(0).name()).append(".getGemId());\n");
      indent(builder, parameters.size()).append("      }\n");

      for (int level = parameters.size() - 1; level >= 0; level--) {
        indent(builder, level).append("      }\n");
      }
    }
    builder.append("      setContent(String.join(\",\", res));\n");
    builder.append("    };\n");
    builder.append("  }\n");
    return builder.toString();
  }

  private static List<ContextParameter> contextParameters(ASTOCLInvariant invariant) {
    List<ContextParameter> result = new ArrayList<>();
    int unnamedIndex = 0;
    for (ASTOCLContextDefinition context : invariant.getOCLContextDefinitionList()) {
      Optional<String> type = contextType(context);
      if (type.isEmpty()) {
        continue;
      }
      String name;
      if (context.isPresentOCLParamDeclaration()) {
        name = context.getOCLParamDeclaration().getName();
      }
      else {
        name = unnamedIndex == 0 ? lowerFirst(type.get()) : lowerFirst(type.get()) + unnamedIndex;
        unnamedIndex++;
      }
      result.add(new ContextParameter(type.get(), sanitizeJavaIdentifier(name)));
    }
    return result;
  }

  private static String buttonMethodName(
      String artifactName,
      ASTOCLInvariant invariant,
      int index,
      int invariantCount) {
    if (invariant.isPresentName()) {
      return sanitizeJavaIdentifier(lowerFirst(invariant.getName()));
    }
    if (invariantCount == 1) {
      return sanitizeJavaIdentifier(lowerFirst(artifactName));
    }
    return sanitizeJavaIdentifier(lowerFirst(artifactName) + "Inv" + index);
  }

  private static String lowerFirst(String value) {
    if (value.isEmpty()) {
      return value;
    }
    return value.substring(0, 1).toLowerCase(Locale.ROOT) + value.substring(1);
  }

  private static String upperFirst(String value) {
    if (value.isEmpty()) {
      return value;
    }
    return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
  }

  private static StringBuilder indent(StringBuilder builder, int level) {
    builder.append("  ".repeat(level));
    return builder;
  }

  private static String pluralize(String value) {
    if ("person".equals(value)) {
      return "persons";
    }
    if (value.endsWith("y")) {
      return value.substring(0, value.length() - 1) + "ies";
    }
    if (value.endsWith("s") || value.endsWith("x") || value.endsWith("z")) {
      return value + "es";
    }
    return value + "s";
  }

  private static String sanitizeJavaIdentifier(String value) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (i == 0) {
        result.append(Character.isJavaIdentifierStart(ch) ? ch : '_');
      }
      else if (Character.isJavaIdentifierPart(ch)) {
        result.append(ch);
      }
    }
    if (result.isEmpty()) {
      result.append('_');
    }
    String identifier = result.toString();
    return JAVA_KEYWORDS.contains(identifier) ? "_" + identifier : identifier;
  }

  private static String quote(String value) {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }

  private static void recreateDirectory(Path directory) throws IOException {
    if (Files.exists(directory)) {
      try (Stream<Path> stream = Files.walk(directory)) {
        List<Path> paths = stream.sorted(Comparator.reverseOrder()).toList();
        for (Path path : paths) {
          Files.delete(path);
        }
      }
    }
    Files.createDirectories(directory);
  }

  private record ContextParameter(String type, String name) {
  }
}
