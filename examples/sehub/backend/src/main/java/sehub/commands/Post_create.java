/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Post;
import sehub.PostAccessPolicy;
import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Post_create extends Post_createTOP {

  public Post_create(Post post) {
    super(post);
  }

  public StatusResult doRun() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    if(accessPolicy.checkCreatePermission(this)){
      SehubManager.addPost(post, NotificationScope.LOCAL);
      return StatusResult.ok(getId());
    }
    return StatusResult.error(getId());
  }


}


