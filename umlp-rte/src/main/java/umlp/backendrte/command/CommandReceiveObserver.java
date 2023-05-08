/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import java.util.function.Consumer;

public interface CommandReceiveObserver extends Consumer<Command<Object>> {
}
