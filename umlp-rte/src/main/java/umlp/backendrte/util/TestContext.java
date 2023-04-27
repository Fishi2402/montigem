package umlp.backendrte.util;

import umlp.backendrte.command.CommandManager;

public interface TestContext {
  void activate();
  CommandManager getCommandManager();
}
