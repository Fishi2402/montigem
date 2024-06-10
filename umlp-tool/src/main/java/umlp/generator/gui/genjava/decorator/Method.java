package umlp.generator.gui.genjava.decorator;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Method {
  protected final String name;
  protected final SymTypeExpression returnType;
  protected final List<SymTypeExpression> paramTypes = new ArrayList<>();
  protected Optional<PredefinedFunction> predefined = Optional.empty();
  protected String alias = "";

  public Method(SymTypeExpression returnType, String name, List<SymTypeExpression> paramTypes) {
    this.name = name;
    this.alias = name;
    this.returnType = returnType;
    this.paramTypes.addAll(paramTypes);
  }

  public String getName() {
    return name;
  }

  public SymTypeExpression getReturnType() {
    return returnType;
  }

  public List<SymTypeExpression> getParamTypes() {
    return paramTypes;
  }

  public void setPredefined(PredefinedFunction predefined, String alias) {
    this.predefined = Optional.of(predefined);
    this.alias = alias;
  }

  public Optional<PredefinedFunction> getPredefined() {
    return predefined;
  }

  public String getAlias() {
    return alias;
  }

  public boolean sameReturnType(Method m) {
    return returnType.deepEquals(m.getReturnType());
  }

  public boolean compatibleReturnType(Method m) {
    return SymTypeRelations.isCompatible(returnType, m.getReturnType());
  }

  public boolean sameParamTypes(Method m) {
    if (paramTypes.size() == m.getParamTypes().size()) {
      for (int i = 0; i < paramTypes.size(); ++i) {
        SymTypeExpression ste1 = paramTypes.get(i);
        SymTypeExpression ste2 = m.getParamTypes().get(i);
        if (!ste1.deepEquals(ste2)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public boolean compatibleParamTypes(Method m) {
    if (paramTypes.size() == m.getParamTypes().size()) {
      for (int i = 0; i < paramTypes.size(); ++i) {
        SymTypeExpression ste1 = paramTypes.get(i);
        SymTypeExpression ste2 = m.getParamTypes().get(i);
        if (!SymTypeRelations.isCompatible(ste1, ste2)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public boolean sameParamTypesErasure(Method m) {
    if (paramTypes.size() == m.getParamTypes().size()) {
      for (int i = 0; i < paramTypes.size(); ++i) {
        SymTypeExpression ste1 = paramTypes.get(i);
        SymTypeExpression ste2 = m.getParamTypes().get(i);
        if (ste1.isGenericType() && ste2.isGenericType()) {
          return SymTypeRelations.isSubTypeOf(ste1, ste2)
              && SymTypeRelations.isSubTypeOf(ste2, ste1);
        }
        if (!ste1.deepEquals(ste2)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public boolean sameParamTypesSize(Method m) {
    return paramTypes.size() == m.getParamTypes().size();
  }

}