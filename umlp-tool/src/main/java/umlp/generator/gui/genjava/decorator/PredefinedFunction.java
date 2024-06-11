/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum PredefinedFunction {

  DELETE("delete"),
  CREATE("create"),
  ASSOC_ADD("assoc_add"),
  ASSOC_REMOVE("assoc_remove");

  private final String name;

  PredefinedFunction(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Optional<PredefinedFunction> getMember(String name) {
    for (PredefinedFunction fun : PredefinedFunction.values()) {
      if (fun.getName().equals(name)) {
        return Optional.of(fun);
      }
    }
    return Optional.empty();
  }

}
