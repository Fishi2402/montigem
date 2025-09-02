/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.JsonString;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JSweetConfigGenerator {

  private static final String DOMAIN_CONFIG_FILE = "umlp.jsweetconfig.JSweetConfigDomainJSON";

  private static final String GUI_CONFIG_FILE = "umlp.jsweetconfig.JSweetConfigGUIJSON";
  private static final String GUI_CONFIG_NO_DOMAIN_FILE = "umlp.jsweetconfig.JSweetConfigGUINoDomainJSON";

  protected GeneratorSetup generatorSetup;
  protected GeneratorEngine generatorEngine;

  public JSweetConfigGenerator(GeneratorSetup generatorSetup) {
    this.generatorSetup = generatorSetup;
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    generatorSetup.setTracing(false);
  }

  public void generate(String outputDir, Set<String> srcDirs) {
    Path outputPath = Paths.get(outputDir, "jsweetconfig.json");

    String defaultConfigPath = "umlp/jsweetconfig/jsweetGUINoDomain.json";
    try (InputStream is = this.getClass().getClassLoader()
        .getResourceAsStream(defaultConfigPath)) {
      if (is == null) {
        Log.error("0x0002F Generating jsweetconfig.json failed:"
            + "\nResource not found: " + defaultConfigPath);
      }
      String defaultConfig = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      JsonElement jsweetconfig = JsonParser.parse(defaultConfig);
      if (jsweetconfig.isJsonObject()) {
        JsonArray dirs = new JsonArray();
        for (String path : srcDirs) {
          dirs.add(new JsonString(path));
        }
        jsweetconfig.getAsJsonObject().putMember("srcDirs", dirs);
      }
      else {
        Log.error("0x00030 Generating jsweetconfig.json failed: `" + defaultConfigPath
            + "`. Internal MontiGem error - please report to MontiGem developers.\n");
      }
      FileReaderWriter.storeInFile(outputPath, maybeTOP(jsweetconfig));
      Log.debug(outputPath + " written successfully", this.getClass().getName());
    } catch (IOException e) {
      Log.error("0x00031 Tried reading resource file `" + defaultConfigPath
          + "`, but failed with exception\n" + e);
    }
  }

  // forDomain differentiates between the config file generated for the Domain JSweet Task and the
  //   GUI JSweet task
  public void generate(String domainName, boolean forDomain, Set<String> hwSourcesPaths) {

    File jsweetConfig = calcTOPFile("jsweetconfig", "json");
    String configFile = forDomain ? DOMAIN_CONFIG_FILE : GUI_CONFIG_FILE;
    generatorEngine.generateNoA(
        configFile,
        jsweetConfig.toPath(),
        domainName
    );

    File importAdapter = calcTOPFile(Paths.get("jsweet_extension", "umlp", "jsweet", "extension", "domainAdapters"), domainName + "ImportAdapter", "java");
    generatorEngine.generateNoA(
        "umlp.jsweetconfig.JSweetDomainImportAdapter",
        importAdapter.toPath(),
        importAdapter.getName().substring(0, importAdapter.getName().lastIndexOf(".java")),
        domainName);
  }

  protected File calcTOPFile(String origName, String fileExt) {
    File f = new File(origName + "." + fileExt);
    Optional<Path> hwc = getHWC(generatorSetup.getHandcodedPath(), f.getPath());
    if (hwc.isPresent()) {
      f = new File(origName + "TOP" + "." + fileExt);
    }
    return f;
  }

  protected String maybeTOP(JsonElement jsweetconfig) {
    File f = new File("jsweetconfig.json");
    Optional<Path> hwc = getHWC(generatorSetup.getHandcodedPath(), f.getPath());
    if (hwc.isPresent()) {
      try {
        String config = Files.readString(hwc.get());
        JsonElement json = JsonParser.parse(config);
        merge(jsweetconfig, json);
        jsonString2UserJsonString(json);
        return json.print(new IndentPrinter());
      } catch (IOException e) {
        Log.error("0x0002D attempted reading JSON file "
            + hwc.get() + " but failed with the exception: " + e);
      }
    }
    jsonString2UserJsonString(jsweetconfig);
    return jsweetconfig.print(new IndentPrinter());
  }

  protected void merge(JsonElement from, JsonElement to) {
    merge(from, to, "");
  }

  protected void merge(JsonElement from, JsonElement to, String pathPrefix) {
    if (match(from, to)) {
      if (from.isJsonObject()) {
        JsonObject f = from.getAsJsonObject();
        JsonObject t = to.getAsJsonObject();
        for (String key : f.getMemberNames()) {
          JsonElement value = f.getMember(key);
          if (t.hasMember(key)) {
            boolean valueTypesMatch = match(value, t.getMember(key));
            if (valueTypesMatch) {
              merge(value, t.getMember(key), key);
            }
            else {
              warn0002E(pathPrefix);
            }
          }
          else {
            t.putMember(key, value);
          }
        }
      }
    }
    else {
      warn0002E(pathPrefix);
    }
  }

  protected void warn0002E(String pathPrefix) {
    Log.warn("0x0002E Merging jsweetconfig.json: mismatching value types for the path \n\t`"
        + pathPrefix + "`\nKeeping hand-written value.");
  }

  protected boolean match(JsonElement first, JsonElement second) {
    return first.isJsonArray() == second.isJsonArray()
        && first.isJsonObject() == second.isJsonObject()
        && first.isJsonBoolean() == second.isJsonBoolean()
        && first.isJsonNumber() == second.isJsonNumber()
        && first.isJsonNull() == second.isJsonNull()
        && first.isJsonString() == second.isJsonString();
  }

  protected void jsonString2UserJsonString(JsonElement json) {
    if (json.isJsonObject()) {
      for (String key : json.getAsJsonObject().getMemberNames()) {
        JsonElement value = json.getAsJsonObject().getMember(key);
        if (value.isJsonString()) {
          json.getAsJsonObject()
              .putMember(key, new UserJsonString(value.getAsJsonString().getValue()));
        }
        else if (value.isJsonObject() || value.isJsonArray()) {
          jsonString2UserJsonString(value);
        }
      }
    }
    else if (json.isJsonArray()) {
      for (int i = 0; i < json.getAsJsonArray().getValues().size(); ++i) {
        JsonElement value = json.getAsJsonArray().getValues().get(i);
        if (value.isJsonString()) {
          json.getAsJsonArray().getValues().set(i, new UserJsonString(value.getAsJsonString().getValue()));
        }
        else if (value.isJsonObject() || value.isJsonArray()) {
          jsonString2UserJsonString(value);
        }
      }
    }
  }

  protected File calcTOPFile(Path basePath, String origName, String fileExt) {
    return calcTOPFile(basePath.toString() + File.separator + origName, fileExt);
  }

  protected Optional<Path> getHWC(MCPath targetPath, String hwFile) {
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    if (hwFilePath.isPresent()) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      if (hwPath.isPresent()) {
        Reporting.reportUseHandwrittenCodeFile(hwPath.get(), Paths.get(hwFile));
        Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
      }
      return hwPath;
    }
    return Optional.empty();
  }

}
