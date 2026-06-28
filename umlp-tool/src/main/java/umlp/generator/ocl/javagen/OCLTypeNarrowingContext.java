package umlp.generator.ocl.javagen;

import de.monticore.types.check.SymTypeExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

class OCLTypeNarrowingContext {
  private final Map<String, Stack<SymTypeExpression>> knownTypeOfVar;

  OCLTypeNarrowingContext() {
    this.knownTypeOfVar = new HashMap<>();
  }

  void push(String name, SymTypeExpression type) {
    knownTypeOfVar.computeIfAbsent(name, key -> new Stack<>()).push(type);
  }

  void pop(String name) {
    Stack<SymTypeExpression> types = knownTypeOfVar.get(name);
    if (types != null) {
      types.pop();
      if (types.isEmpty()) {
        knownTypeOfVar.remove(name);
      }
    }
  }

  Optional<SymTypeExpression> get(String name) {
    Stack<SymTypeExpression> types = knownTypeOfVar.get(name);
    if (types == null || types.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(types.peek());
  }
}
