package gui;/* (c) https://github.com/MontiCore/monticore */

import sehub.Artifact;
import sehub.Directory;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;

@Component()
public class DirNode extends DirNodeTOP {

  @Override
  public Boolean isDirectory() {
    return this.getArtifact() instanceof Directory;
  }

  @Override
  public List<Artifact> getChildren(Artifact art) {
    return ((Directory) art).getContainsList();
  }

}


