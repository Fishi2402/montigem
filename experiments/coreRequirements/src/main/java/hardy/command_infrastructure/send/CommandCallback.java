/* (c) https://github.com/MontiCore/monticore */

package hardy.command_infrastructure.send;

public interface CommandCallback<Response> {
  void onOk(Response response);

  void onError(Exception e);
}
