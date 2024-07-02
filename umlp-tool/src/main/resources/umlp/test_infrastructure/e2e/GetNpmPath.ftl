<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected String getNpmPath() throws IOException")}

try (var stream = Files.walk(Paths.get("../frontend/.gradle/nodejs/").toAbsolutePath().normalize())) {
  return stream.filter(p -> p.toFile().getName().equals(environmentIsWindows() ? "npm.cmd" : "npm"))
      .map(Path::toString)
      .min(Comparator.comparing(String::length))
      .orElseThrow();
}
