import {BingoComponentTOP} from "../../../build/frontendGen/build/appgui/gui/BingoComponentTOP";
import {Component,} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import { SehubManager } from "@src/sehub/SehubManager";


@Component({
  selector: 'bingo',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Bingo.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./Bingo.scss']
})

export class BingoComponent extends BingoComponentTOP {
  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login']);
    } else {
      this.router.navigate(['/bingo/Bingo/'+SehubSecurityManager.getCurrentUser().getGemId()])
    }

    super.ngOnInit();

  }
}
