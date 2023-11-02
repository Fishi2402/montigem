/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.util;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import umlp.TagRelated;

import java.util.ArrayList;
import java.util.List;

public class SingletonLookUp {

  private final static String SINGLETON = "singleton";

  // This list should be moved elsewhere (TagRepository)
  protected static List<ASTTagUnit> tagUnits = new ArrayList<>();

  public static void addTagFile(ASTTagUnit tagUnit) {
    tagUnits.add(tagUnit);
  }

  public static boolean isSingleton(TypeSymbol typeSymbol) {
    return tagUnits.stream().anyMatch(x -> TagRelated.getTags(typeSymbol, x).stream().anyMatch(SingletonLookUp::isSingleton));
  }

  public static boolean isSingleton(ASTCDClass astcdClass) {
    // Use UMLTagger once fixed
    return tagUnits.stream().anyMatch(x -> TagRelated.getTags(astcdClass.getSymbol(), x).stream().anyMatch(SingletonLookUp::isSingleton));
  }

  // This would be nice to have generated from a TagSchema
  protected static boolean isSingleton(ASTTag tag){
    if (tag instanceof ASTSimpleTag)
      return ((ASTSimpleTag) tag).getName().equals(SINGLETON);
    return false;
  }
}
