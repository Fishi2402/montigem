/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Post;
import sehub.PostAccessPolicy;
import umlp.backendrte.command.StatusResult;

public class Post_create extends Post_createTOP {

  public Post_create(Post post) {
    super(post);
  }

  public StatusResult doRun() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    if(accessPolicy.checkCreatePermission(this)){
      return super.doRun();
    }
    return StatusResult.error(getId());
  }


}


