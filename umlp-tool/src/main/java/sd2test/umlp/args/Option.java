package sd2test.umlp.args;

import java.util.List;
import java.util.Optional;

public final class Option<Value> {

  private final String name;
  private final ParseFunction<Value> parseFunction;
  private Optional<Value> value = Optional.empty();
  private Optional<Value> defaultValue = Optional.empty();
  private Optional<MergeFunction<Value>> mergeFunction = Optional.empty();

  public Option(String name, ParseFunction<Value> parseFunction) {
    this.name = name;
    this.parseFunction = parseFunction;
  }

  public Option<Value> withDefaultValue(Value defaultValue) {
    this.defaultValue = Optional.of(defaultValue);
    return this;
  }

  public Option<Value> withMergeFunction(MergeFunction<Value> mergeFunction) {
    this.mergeFunction = Optional.of(mergeFunction);
    return this;
  }

  public boolean parse(List<String> argPtr) throws IllegalArgumentException {
    if (argPtr.isEmpty()) return false; //quick safety shortcut

    if (!isReferencedBy(argPtr.get(0))) return false; //make sure we match
    argPtr.remove(0); //update args, so we don't attempt to parse it later

    if (value.isEmpty()) {
      this.value = Optional.of(parseValue(argPtr));
      return true;
    }

    if (mergeFunction.isEmpty()) {
      throw new IllegalArgumentException("Option " + name + " can only be set once");
    }

    var currentValue = value.get();
    var mergeFunction = this.mergeFunction.get();
    var additionalValue = parseValue(argPtr);
    this.value = Optional.of(mergeFunction.merge(currentValue, additionalValue));
    return true;
  }

  public Optional<Value> getValue() {
    return value.or(() -> defaultValue);
  }

  public Value expectValue() throws IllegalArgumentException {
    return getValue().orElseThrow(() -> new IllegalArgumentException("Option " + name + " needs to be set"));
  }

  private boolean isReferencedBy(String option) {
    return option.equalsIgnoreCase("--" + name);
  }

  private Value parseValue(List<String> argPtr) throws IllegalArgumentException {
    return parseFunction.parse(new Context(argPtr, name));
  }

  @FunctionalInterface
  public interface ParseFunction<Value> {
    Value parse(Context ctx) throws IllegalArgumentException;
  }

  @FunctionalInterface
  public interface MergeFunction<Value> {
    Value merge(Value currentValue, Value additionalValue) throws IllegalArgumentException;
  }

  public static final class Context {
    private final List<String> argPtr;
    private final String optionName;

    private Context(List<String> argPtr, String optionName) {
      this.argPtr = argPtr;
      this.optionName = optionName;
    }

    public String getNext() throws IllegalArgumentException{
      expectHasNext();

      return argPtr().remove(0);
    }

    public String peekNext() throws IllegalArgumentException {
      expectHasNext();

      return argPtr().get(0);
    }

    public boolean hasNext() {
      return !argPtr().isEmpty();
    }

    public void expectHasNext() throws IllegalArgumentException {
      if (!hasNext()) throw new IllegalArgumentException("Missing value for option " + optionName);
    }

    public boolean nextIsValue() {
      return hasNext() && !peekNext().startsWith("-");
    }

    public List<String> argPtr() {
      return argPtr;
    }
  }
}
