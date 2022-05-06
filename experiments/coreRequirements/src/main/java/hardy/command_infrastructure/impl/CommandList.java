/* (c) https://github.com/MontiCore/monticore */
package hardy.command_infrastructure.impl;
// RTE-P

/*
  // todo: to be handled:
  
  a) es handelt sich um RTE-Klassen, die werden nicht generiert, können
   also notwendigerweise nicht Teil eines Pakets (hardy...) sein, das 
   den CD-Namen beinhaltet.
   --> verschieben nach ./src/main/java/umlp/command; package umlp.command;
 
 b) Zwecke von CommandList erklären:
 wenn ich das richtig sehe, dann ist es ausschließlich eine Liste, die 
  hier verbreitet wird. Dafür benötigen wir diese Klasse nicht.
  Der zweck des Zählers id sist auch unklar: Entfernen oder besser im 
  CommandProcessor aufheben.
  
 c) Oder ist das eine unvollständige und daher misslungene Form für Commands ein
 Composite einzuführen? --> dann Rücksprache und Erklärung
 
*/ 

import java.util.ArrayList;
import java.util.List;

/**
 * Container for a command
 */
public class CommandList {
  /**
   * running (unique) id, to identify the sent commands
   */
  private long id;
  private List<Command> commands;

  public CommandList() {
    this.commands = new ArrayList<>();
  }

  public CommandList(List<Command> commands) {
    this.commands = commands;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Command> getCommands() {
    return commands;
  }

  public void setCommands(List<Command> commands) {
    this.commands = commands;
  }

  public void addCommand(Command command) {
    if (this.commands == null) {
      this.commands = new ArrayList<>();
    }

    this.commands.add(command);
  }
}
