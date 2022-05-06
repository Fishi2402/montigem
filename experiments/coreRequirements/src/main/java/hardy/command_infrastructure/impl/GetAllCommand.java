/* (c) https://github.com/MontiCore/monticore */


// todo: Was soll dies? Sonst streichen und was vernünftiges anbieten

package hardy.command_infrastructure.impl;
// RTE-P
public abstract class GetAllCommand extends Command {
  protected int limit = -1;

  public GetAllCommand(int id, String typeName) {
    super(id, typeName, true);
  }

  public GetAllCommand limit(int limit) {
    this.limit = limit;
    return this;
  }
}
