package umlp.generator.gui.gentypescript.decorator;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.printer.TSImportMap;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TSImportHelper {

  protected Set<String> importSet = new HashSet<>();
  protected final GlobalExtensionManagement glex;
  protected final ASTCDClass clazz;
  protected final MCPath hwPath;
  protected final Path outPath;
  protected final MCPath importsPath;

  public TSImportHelper(GlobalExtensionManagement glex, ASTCDClass clazz, MCPath hwPath, Path outPath, MCPath importsPath) {
    this.glex = glex;
    this.clazz = clazz;
    this.hwPath = hwPath;
    this.outPath = outPath;
    this.importsPath = importsPath;
  }

  public void addImport(String qType, Optional<String> as, boolean importIntermediateAngularClass, String importerQName) {
    String alias = "";
    if (as.isPresent()) {
      alias = " as " + as.get();
    }
    Optional<String> tsImportPath = TypeScriptTypes.getTsImportPath(qType);
    // Optional doesn't exist in TypeScript
    if (qType.equals("java.util.Optional") || qType.isBlank()) {
      return;
    }
    if (tsImportPath.isPresent()) {
      String importStr = "import { " + TypeScriptTypes.toTSType(qType) + alias
          + " } from '" + tsImportPath.get() + "';";
      addImportString(importStr);
    }
    else if (!TypeScriptTypes.isDefault(qType)) {
      StringBuilder importBuilder = new StringBuilder();
      importBuilder.append("import { ");

      List<String> parts = Lists.newArrayList(Splitter.on('.').split(qType));
      String baseName = Iterables.getLast(Splitter.on('.').split(qType), qType);
      importBuilder.append(baseName);
      importBuilder.append(alias);

      importBuilder.append(" }");
      importBuilder.append(" from '");

      // generated path doesn't match package for domain classes
      // hardy.Flensburg.Human -> hardy/flensburg/Human
      int index = parts.size() - 2;
      if (index >= 0) {
        parts.set(index, parts.get(index).toLowerCase());
      }
      // case: a component in an app imports a component from the app
      String from = GUIGeneratorUtil.getInstance().qName2path(String.join(".", parts));
      if (importIntermediateAngularClass) {
        from += "Component";
      }
      Optional<String> relativeFrom = getImportPathForComponent(
          hwPath, outPath, importsPath, importerQName, qType);
      if (relativeFrom.isPresent()) {
        from = relativeFrom.get();
      }
      tsImportPath = TSImportMap.getTSPackage(qType);

      int lastDotIndex = importerQName.lastIndexOf('.');
      String importerPackage = lastDotIndex != -1 ? importerQName.substring(0, lastDotIndex) : "";
      Optional<String> componentImportPath = TSImportMap.getTSPackage(importerPackage);
      if (tsImportPath.isPresent() &&
          (componentImportPath.isEmpty() || !componentImportPath.get().equals(tsImportPath.get()))) {
        from = tsImportPath.get();
      }
      importBuilder.append(from);

      importBuilder.append("';");

      addImportString(importBuilder.toString());
    }
  }

  public void addImport(String qType, String importerQName) {
    addImport(qType, Optional.empty(), false, importerQName);
  }

  public void addImportString(String importStr) {
    if (!importSet.contains(importStr)) {
      importSet.add(importStr);
      glex.addAfterTemplate("umlp.gui2ts.TSEmptyImports", clazz,
          new StringHookPoint(importStr));
    }
  }

  protected Optional<String> getImportPathForComponent(
      MCPath hwPath, Path outPath, MCPath importsPath, String importerQName, String importedQName) {
    String importedQualifier = Names.getQualifier(importedQName);
    String importedSimpleName = Names.getSimpleName(importedQName);
    String importedMaybeCDName;

    if (!importedQualifier.isBlank()) {
      List<String> parts = Splitter.on('.').splitToList(importedQualifier);
      // Lowercase the last part
      int lastIndex = parts.size() - 1;
      String lastPart = parts.get(lastIndex).toLowerCase();
      // Rejoin the parts with the modified last part
      importedMaybeCDName = Joiner.on('/').join(parts.subList(0, lastIndex))
          + (lastIndex > 0 ? "/" : "") + lastPart
          + "/" + importedSimpleName;
    }
    else {
      importedMaybeCDName = importedQualifier;
    }

    String importedMaybeCD = importedMaybeCDName + ".ts";
    String imported = Names.getPathFromPackage(importedQName) + ".ts";
    String importer = Names.getPathFromPackage(importerQName) + ".ts";

    Optional<URL> hwFilePath = hwPath.find(imported);
    if (hwFilePath.isEmpty() && UMLPGeneratorConfig.getInstance().isUseJsweet()) {
      importedMaybeCD = importedMaybeCDName + ".java";
      imported = Names.getPathFromPackage(importedQName) + ".java";
      importer = Names.getPathFromPackage(importerQName) + ".java";
      hwFilePath = hwPath.find(imported);
    }

    // case: TOP is used, use path to HW file, e.g.,
    // "gen/gui/ExampleComponent.ts" imports "hw/gui/Example.ts" -> "../../hw/gui/Example"
    if (hwFilePath.isPresent()) {
      if (UMLPGeneratorConfig.getInstance().isGenerate4TSLib()) {
        Optional<Path> hw = MCPath.toPath(hwFilePath.get());
        if (hw.isPresent()) {
          return Optional.of(path2TSRelativeImport(outPath.resolve(importer).getParent()
              .toAbsolutePath()
              .normalize()
              .relativize(hw.get().toAbsolutePath())
              .toString()
              .replaceAll("\\\\", "/")));
        }
      }
      else {
        return Optional.of(path2TSRootImport(imported));
      }
    }
    else {
      Optional<URL> importedURLOpt = importsPath.find(imported);
      Optional<URL> importedMaybeCDURLOpt = importsPath.find(importedMaybeCD);
      // case: external (generated) file must be imported, e.g.,
      // "gen/gui/Example.ts" imports "gen/cd/fetypes/Option.ts" -> "../cd/fetypes/Option"
      if (importedURLOpt.isPresent() || importedMaybeCDURLOpt.isPresent()) {
        if (UMLPGeneratorConfig.getInstance().isGenerate4TSLib()) {
          Optional<Path> importedPath;
          // prefer "maybe CD" option
          if (importedMaybeCDURLOpt.isPresent()) {
            importedPath = MCPath.toPath(importedMaybeCDURLOpt.get());
          } else {
            importedPath = MCPath.toPath(importedURLOpt.get());
          }
          if (importedPath.isPresent()) {
            Path fromPath = outPath.resolve(importer);
            // Calculate the relative path
            return Optional.of(path2TSRelativeImport(fromPath.getParent()
                .normalize()
                .relativize(importedPath.get())
                .toString()
                .replaceAll("\\\\", "/")));
          }
        }
        else {
          if (importedMaybeCDURLOpt.isPresent()) {
            return Optional.of(path2TSRootImport(importedMaybeCD));
          } else {
            return Optional.of(path2TSRootImport(imported));
          }
        }
      }
      // case: a component imports currently generated other component, e.g.
      // "gen/gui/foo/ExampleComponent.ts" imports "gen/gui/foo/Example.ts" -> "./Example"
      else if (UMLPGeneratorConfig.getInstance().isGenerate4TSLib()
          && Names.getQualifier(importerQName).equals(Names.getQualifier(importedQName))) {
        String res = "./" + Names.getSimpleName(importedQName);
        return Optional.of(res);
      }
    }
    return Optional.empty();
  }

  protected String path2TSRelativeImport(String relativePath) {
    if (!relativePath.contains("/")) {
      // Same folder, prepend "./"
      relativePath = "./" + relativePath;
    }
    return path2TSRootImport(relativePath);
  }

  protected String path2TSRootImport(String rootPath) {
    if (rootPath.endsWith(".ts")) {
      // Remove file extension
      rootPath = rootPath.replace(".ts", "");
    }
    if (rootPath.endsWith(".java")) {
      // Remove file extension
      rootPath = rootPath.replace(".java", "");
    }
    return rootPath.replaceAll("\\\\", "/");
  }

}
