package umlp.generator.gui.gentypescript.printer;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// TODO AGe: move to frontend-rte
public enum TSImportMap {
  INSTANCE;

  // key: javaPackage, value: tsPackage
  private final Map<String, String> importMap = new HashMap<>();
  public static final String JSWEET_RTE = "@umlp/commonj2ts";
  public static final String COMMON = "@umlp/common";
  public static final String BACKEND_RTE = "umlp.backendrte";

  TSImportMap() {
  }

  public void add(String javaPackage, String tsPackage) {
    if (importMap.containsKey(javaPackage)) {
      Log.warn("0x00028 Overwriting import mapping from\n"
          + javaPackage + "->" + importMap.get(javaPackage)
          + "\nto\n" + javaPackage + "->" + tsPackage
      );
    }
    importMap.put(javaPackage, tsPackage);
  }

  public Optional<String> getTSPackage(String javaPackage) {
    for (String key : importMap.keySet()) {
      if (javaPackage.equals(key)) {
        if (key.equals(BACKEND_RTE) && UMLPGeneratorConfig.getInstance().isUseJsweet()) {
          return Optional.of(JSWEET_RTE);
        }
        return Optional.of(importMap.get(key));
      }
    }
    return Optional.empty();
  }

  public boolean isTSPackageImport(String javaPackage) {
    for (String key : importMap.keySet()) {
      if (javaPackage.equals(key)) {
        return false;
      }
    }
    return true;
  }

  public void init(MCPath importedSymbolPath) {
    // default map
    add("mc.fenix.common", COMMON);
    add(BACKEND_RTE, "@umlp/common");

    // loaded from files
    Log.info("Initializing qualified name to TypeScript import map",
        TSImportMap.class.getSimpleName());
    clear();
    Set<File> mapFiles = importedSymbolPath.getEntries().stream()
        .map(x -> x.toFile().listFiles(f -> f.getName().endsWith("import.map")))
        .filter(Objects::nonNull)
        .flatMap(Arrays::stream)
        .peek(p -> Log.info("Found import map: " + p.getAbsolutePath(),
            TSImportMap.class.getSimpleName()))
        .collect(Collectors.toSet());

    if (!mapFiles.isEmpty()) {
      for (File f : mapFiles) {
        try {
          addFrom(Files.readString(f.toPath()));
        } catch (IOException ex) {
          Log.error("Failed to load import map file " + f.getName() + " :" + ex.getMessage(), ex);
        }
      }
      Log.info("Finished initializing qualified name to TypeScript import map",
          TSImportMap.class.getSimpleName());
    }

    for (Path p : importedSymbolPath.getEntries()) {
      if (p.getFileName().toString().contains(".jar")) {
        Reporting.reportOpenInputFile(p.toString());
        try (ZipFile zipFile = new ZipFile(p.toFile())) {
          Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
          while (zipEntries.hasMoreElements()) {
            ZipEntry entry = zipEntries.nextElement();
            if (entry.getName().endsWith("import.map")) {
              try (InputStream is = zipFile.getInputStream(entry)) {
                byte[] bytes = is.readAllBytes();
                addFrom(new String(bytes, StandardCharsets.UTF_8).trim());
                Log.debug("Loaded import maps from jar " + p + ":" + entry.getName(),
                    TSImportMap.class.getSimpleName());
              }
            }
          }
        } catch (IOException e) {
          Log.error("Failed to load tags from " + p, e);
          throw new RuntimeException(e);
        }
      }
    }
  }

  public void addFrom(String fileContent) {
    JsonObject obj = JsonParser.parseJsonObject(fileContent);
    for (String lib : obj.getMembers().keySet()) {
      if (obj.hasArrayMember(lib)) {
        for (JsonElement el : obj.getArrayMember(lib)) {
          if (el.isJsonString()) {
            add(el.getAsJsonString().getValue(), lib);
            Log.debug("Added mapping: " + el.getAsJsonString().getValue() + " -> " + lib,
                TSImportMap.class.getSimpleName());
          }
          else {
            Log.debug("Ignoring non-string member: " + el,
                TSImportMap.class.getSimpleName());
          }
        }
      }
      else {
        Log.debug("Ignoring non-array member: " + obj.getMember(lib).toString(),
            TSImportMap.class.getSimpleName());
      }
    }
  }

  public void clear() {
    importMap.clear();
  }

}