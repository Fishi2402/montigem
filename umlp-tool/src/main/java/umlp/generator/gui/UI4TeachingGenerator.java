/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UI4TeachingGenerator {
  protected GeneratorSetup generatorSetup;
  protected GeneratorEngine generatorEngine;
  protected Map<String, String> fqnClassNameToType;

  public UI4TeachingGenerator(GeneratorSetup generatorSetup) {
    this.generatorSetup = generatorSetup;
    this.generatorEngine = new GeneratorEngine(generatorSetup);
    generatorSetup.setTracing(false);
  }

  public void generate(ASTCDCompilationUnit cdAST){
    // into /static so that Spring will automatically host these
    generatorEngine.generate("umlp.gui4teaching.html", Paths.get("resources", "static","index.html"), cdAST);
    List<ASTCDClass> classes = cdAST.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getModifier().isAbstract())
            .collect(Collectors.toList());
    List<ASTCDEnum> enums = cdAST.getCDDefinition().getCDEnumsList();
    Map<ASTCDClass, List<ASTCDAttribute>> attributesInHierarchy = new LinkedHashMap<>();
    Map<ASTCDClass, List<ASTCDAssocSide>> associationsInHierarchy = new LinkedHashMap<>();

    for (ASTCDClass aClass : classes) {
      attributesInHierarchy.put(aClass, CDSymbolTables.getAttributesInHierarchy(aClass));
      associationsInHierarchy.put(aClass, CDSymbolTables.getAssociationsInHierarchy(aClass));
    }

    generatorEngine.generate("umlp.gui4teaching.js", Paths.get("resources", "static","index.js"), cdAST,
        classes, enums, this, attributesInHierarchy, associationsInHierarchy);
  }

  public boolean isEnum(String attrType, List<ASTCDEnum> enums) {
    for (ASTCDEnum astcdEnum : enums) {
      if (astcdEnum.getName().equals(attrType)) {
        return true;
      }
    }
    return false;
  }

}
