/* (c) https://github.com/MontiCore/monticore */
package sehub.commands.associations;

import sehub.PostAccessPolicy;

public class Discussion_remove extends Discussion_removeTOP {

  public Discussion_remove(long issueGemId, long postsGemId, int issueIndex, int postsIndex) {
    super(issueGemId, postsGemId, issueIndex, postsIndex);
  }

  @Override
  public boolean checkAccessPolicy() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    return accessPolicy.checkWritePermission(this);
  }
}


