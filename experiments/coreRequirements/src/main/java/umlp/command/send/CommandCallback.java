/* (c) https://github.com/MontiCore/monticore */

// todo: Zweck unklar; Streichen?

package umlp.command.send;
// RTE-P
public interface CommandCallback<Response> {
  void onOk(Response response);

  void onError(Exception e);
}
