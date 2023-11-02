/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.util;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

public class SingletonLookUp {

  private final static String SINGLETON = "singleton";

  public static boolean isSingleton(TypeSymbol typeSymbol) {
    // TODO: Ensure the stereotype is stored
    return typeSymbol.getName().equals("App");
  }

  public static boolean isSingleton(ASTCDClass astcdClass) {
    if (astcdClass.getName().equals("App"))
      return true; //TODO: Only check the stereotype
    return astcdClass.getModifier().isPresentStereotype() && astcdClass.getModifier().getStereotype().contains(SINGLETON);
  }
}
