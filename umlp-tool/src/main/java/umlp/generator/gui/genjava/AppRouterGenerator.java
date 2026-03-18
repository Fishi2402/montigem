package umlp.generator.gui.genjava;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.util.GUIPageRoute;
import umlp.generator.util.TagLookUp;

import java.nio.file.Path;
import java.util.*;

public class AppRouterGenerator {
  
  public static final String APP_ROUTER_TEMPLATE = "umlp.gui2java.GUI2JavaAppRouter";
  public static final String APP_ROUTER_FILE = "app/AppRouter.java";
  
  protected GeneratorSetup setup;
  protected MCPath hwcPath;
  protected GUIGeneratorUtil util;
  
  public AppRouterGenerator(GeneratorSetup setup) {
    this.setup = setup;
    this.hwcPath = setup.getHandcodedPath();
    this.util = GUIGeneratorUtil.getInstance();
  }
  
  public void generate(List<ASTGUICompilationUnit> astCompUnits) {
    List<GUIPageRoute> pageRoutes = getPageRoutes(astCompUnits);
    GeneratorEngine genEngine = new GeneratorEngine(setup);
    if (!pageRoutes.isEmpty()) {
      genEngine.generateNoA(
          APP_ROUTER_TEMPLATE,
          Path.of(APP_ROUTER_FILE),
          pageRoutes
      );
    }
  }
  
  private List<GUIPageRoute> getPageRoutes(List<ASTGUICompilationUnit> astCDCompUnits) {
    List<GUIPageRoute> pageRoutes = new ArrayList<>();
    if (!astCDCompUnits.isEmpty()) {
      for (ASTGUICompilationUnit astCDCompUnit : astCDCompUnits) {
        if (!astCDCompUnit.getGUIComponentDeclaration().isPage()) {
          continue;
        }
        String pageName = astCDCompUnit.getComponentName();
        String path = Names.getPathFromQualifiedName(astCDCompUnit.getComponentQName());
        int routingIdCount = 0;
        if (!util.isCreateModel(astCDCompUnit.getComponentQName())) {
          for (ASTGUIParam p : astCDCompUnit.getGUIComponentDeclaration().getGUIParamList()) {
            if (p.isPresentSymbol() &&
                !GenService.isList(p.getSymbol().getType()) &&
                util.isDomainType(p) &&
                !TagLookUp.isSingleton(p.getSymbol().getType().getTypeInfo()) &&
                !TagLookUp.isDerived(p.getSymbol().getType().getTypeInfo())) {
              routingIdCount++;
            }
          }
        }
        pageRoutes.add(new GUIPageRoute(pageName, path, routingIdCount));
      }
    }
    return pageRoutes;
  }
}
