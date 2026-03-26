package umlp.generator.ocl;

import de.monticore.types.check.SymTypeExpression;
import java.util.ArrayList;
import java.util.List;

public class TypeUtil {

  private static TypeUtil instance;

  private List<String> domainModels = new ArrayList<>();

  private TypeUtil() {}

  public static TypeUtil getInstance() {
    if (instance == null) {
      instance = new TypeUtil();
    }
    return instance;
  }

  public void init(List<String> domainModels) {
    this.domainModels = domainModels;
  }

  public List<String> getDomainModels() {
    return domainModels;
  }

  public boolean isCollectionType(SymTypeExpression type) {
    if (type.hasTypeInfo()) {
      return "List".equals(type.getTypeInfo().getName())
          || "Set".equals(type.getTypeInfo().getName())
          || "Collection".equals(type.getTypeInfo().getName());
    } else {
      return false;
    }
  }

  public boolean isDomainType(SymTypeExpression symTypeExpression) {
    if (symTypeExpression == null) {
      return false;
    }

    boolean isDomain = false;
    if (symTypeExpression.isObjectType()) {
      String fullName = symTypeExpression.asObjectType().printFullName();
      for (String model : domainModels) {
        if (fullName.startsWith(model)) {
          isDomain = true;
          break;
        }
      }
    }

    return isDomain || (symTypeExpression.isGenericType() && isDomainType(symTypeExpression.asGenericType().getArgument(0)));
  }
}
