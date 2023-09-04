import {Directive} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {ResponseTOP} from '@src/gui/note/ResponseTOP';


@Directive()

export class Response extends ResponseTOP {

  deletePost(event: any){
    let gemId = this.post.getGemId();
    let gemList = this.getPost().getParent()._internal_getResponsesGemList()
    console.log("Trying to remove gemId", gemId, "from", gemList.ids)
    let idx = gemList.indexOfId(gemId)
    console.log("Idx", idx);
    if(idx >= 0){
      gemList.remove(idx).then(() => {
        SehubManager.deletePost(gemId);
      })
    }
  }
}
