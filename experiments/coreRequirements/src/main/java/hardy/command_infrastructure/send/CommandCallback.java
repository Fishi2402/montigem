/* (c) https://github.com/MontiCore/monticore */

// todo: Zweck unklar; Streichen?

package hardy.command_infrastructure.send;
// RTE-P
public interface CommandCallback<Response> {
  void onOk(Response response);

  void onError(Exception e);
}
