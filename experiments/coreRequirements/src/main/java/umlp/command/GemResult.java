package umlp.command;

import umlp.rte.Gem;

public class GemResult<T> extends CommandResult<T> {
  private final Gem<T> result;

  public GemResult(long id, Gem<T> result) {
    super(id);
    this.result = result;
  }

  public Gem<T> getResult() {
    return result;
  }

  @Override
  public boolean isGemResult() {
    return true;
  }
}
