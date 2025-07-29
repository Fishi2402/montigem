/* (c) https://github.com/MontiCore/monticore */
package commands.commands;

import umlp.backendrte.command.SimpleResult;

import java.util.List;

public class DomainListParam extends DomainListParamTOP {

  public DomainListParam(List<Long> user) {
    super(user);
  }

  @Override
  public SimpleResult<String> doAction() {
    return null;
  }

}


