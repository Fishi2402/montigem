package gui;/* (c) https://github.com/MontiCore/monticore */

import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class ProjectFileView extends ProjectFileViewTOP {

  public String getGemLink11Url (){
    if(this.getParent().getContainedBy() != null){
      return "/gui/ProjectFileView/"+ this.getParent().getContainedBy().getGemId();
    }
    return "";
  }

  public Consumer<Void> goToArtifactsPage() {
    return $ -> {
      Router.navigate("..", "artifacts", "ProjectPage"+ this.getParent().getGemId());};
  }

  public Boolean getParentDirGuardCondition() {
    if (this.getParent() == null) {
      return false;
    }
    return !(this.getParent().getContainedBy() == null);
  }
}
