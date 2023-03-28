/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import java.util.function.Consumer;

public interface CommandResultReceiveObserver extends Consumer<CommandResult<?>> {
}
