package umlp.args;

import umlp.args.Option.ParseFunction;

import java.util.ArrayList;
import java.util.List;

public final class OptionParser {

  private final List<Option<?>> options = new ArrayList<>();

  public <Value> Option<Value> addOption(String name, ParseFunction<Value> parseFunction) {
    var option = new Option<>(name, parseFunction);
    options.add(option);
    return option;
  }

  public void parse(String[] args) {
    var pendingArgs = new ArrayList<>(List.of(args));
    argLoop:
    while (!pendingArgs.isEmpty()) {
      for (var option : options) if (option.parse(pendingArgs)) continue argLoop;
      throw new IllegalArgumentException("Unknown argument: " + pendingArgs.get(0));
    }
  }
}
