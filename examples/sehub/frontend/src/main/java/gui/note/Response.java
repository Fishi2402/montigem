package gui.note;/* (c) https://github.com/MontiCore/monticore */

import sehub.Post;
import sehub.SehubManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class Response extends ResponseTOP {

  public Consumer<Void> deletePost() {
    return $ -> {
      Post parent = this.getPost().getParent();
      parent.removeResponses(this.getPost());
      SehubManager.deletePost(this.getPost().getGemId());
    };
  }
}
