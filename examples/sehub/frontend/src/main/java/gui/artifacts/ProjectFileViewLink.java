package gui.artifacts;/* (c) https://github.com/MontiCore/monticore */

import umlp.jsweet.extension.annotation.Component;

@Component()
public class ProjectFileViewLink extends ProjectFileViewLinkTOP {

  @Override
  public String getGemLink1Url()  {
     return "/gui/ProjectFileView/"+this.getArtifact().getGemId();
  }
}


