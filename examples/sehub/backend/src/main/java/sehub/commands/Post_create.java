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

  @Override
  public StatusResult doAction() {
    SehubManager.addWithScopePost(post, NotificationScope.LOCAL);
    return StatusResult.ok(getId());
  }

  @Override
  public boolean checkAccessPolicy() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    return accessPolicy.checkCreatePermission(this);
  }
}


