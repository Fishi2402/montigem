/* (c) https://github.com/MontiCore/monticore */
package sehub.commands.associations;

import sehub.PostAccessPolicy;

public class Resp_remove extends Resp_removeTOP {

  public Resp_remove(long parentGemId, long responsesGemId, int parentIndex, int responsesIndex) {
    super(parentGemId, responsesGemId, parentIndex, responsesIndex);
  }

  @Override
  public boolean checkAccessPolicy() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    return accessPolicy.checkWritePermission(this);
  }
}


