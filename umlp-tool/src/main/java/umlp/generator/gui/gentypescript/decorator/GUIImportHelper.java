package umlp.generator.gui.gentypescript.decorator;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.gentypescript.printer.TSImportMap;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GUIImportHelper {

  protected Set<String> importSet = new HashSet<>();
  protected final GlobalExtensionManagement glex;
  protected final ASTCDClass clazz;
  protected final String importerPackage;
  public GUIImportHelper(GlobalExtensionManagement glex, ASTCDClass clazz, String importerPackage) {
    this.glex = glex;
    this.clazz = clazz;
    this.importerPackage = importerPackage;
  }

  public void addImport(String qType) {
    Optional<String> tsImportPath = TypeScriptTypes.getTsImportPath(qType);
    // Optional doesn't exist in TypeScript
    if (qType.equals("java.util.Optional") || qType.isBlank()) {
      return;
    }
    if (tsImportPath.isPresent()) {
      String importStr = "import { " + TypeScriptTypes.toTSType(qType)
          + " } from '" + tsImportPath.get() + "';";
      addImportString(importStr);
    }
    else if (!TypeScriptTypes.isDefault(qType)) {
      StringBuilder importBuilder = new StringBuilder();
      importBuilder.append("import { ");

      List<String> parts = Lists.newArrayList(Splitter.on('.').split(qType));
      String baseName = Iterables.getLast(Splitter.on('.').split(qType), qType);
      importBuilder.append(baseName);
      if (TypeScriptTypes.TEMPLATE_REF.getMCType().equals(qType)) {
        // GUIViewElement is TemplateRef<any>
        importBuilder.append("<any>");
      }

      importBuilder.append(" }");
      importBuilder.append(" from '");

      // generated path doesn't match package for domain classes
      // hardy.Flensburg.Human -> hardy/flensburg/Human
      int index = parts.size() - 2;
      if (index >= 0) {
        parts.set(index, parts.get(index).toLowerCase());
      }
      // case: a component in an app imports a component from the app
      String from = "@src/" + GUIGeneratorUtil.getInstance().qName2path(String.join(".", parts));
      tsImportPath = TSImportMap.getTSPackage(qType);
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

  public void addImportString(String importStr) {
    if (!importSet.contains(importStr)) {
      importSet.add(importStr);
      glex.addAfterTemplate("umlp.gui2ts.TSEmptyImports", clazz,
          new StringHookPoint(importStr));
    }
  }

}
