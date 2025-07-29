package gui.artifacts;/* (c) https://github.com/MontiCore/monticore */

import umlp.jsweet.extension.annotation.Component;

@Component()
public class ArtifactTypeIcon extends ArtifactTypeIconTOP {


  public Boolean getGuard0Condition() {
    return this.getType().equals("Directory");
  }

  public Boolean getGuard2Condition() {
    return !(this.getType().equals("Directory"));
  }


}


