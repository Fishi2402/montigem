package umlp._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class SymTypeExpressionDeSer4GUI extends SymTypeExpressionDeSer {

  protected SymTypeExpressionDeSer4GUI() {
    super();
    this.symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer4GUI();
  }

  public static SymTypeExpressionDeSer getInstance() {
    if (null == instance) {
      instance = new SymTypeExpressionDeSer4GUI();
    }
    return instance;
  }

  public static SymTypeExpression deserializeMember(String memberName, JsonObject json) {
    return getInstance().deserialize(json.getMember(memberName));
  }

}
