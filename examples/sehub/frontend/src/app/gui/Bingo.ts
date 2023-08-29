import {BingoTOP} from "../../../build/frontendGen/build/appgui/gui/BingoTOP";
import {Component,} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";


@Component({
    selector: 'bingo',
    templateUrl: '../../../build/frontendGen/build/appgui/gui/Bingo.html',
    styles: [":host{ display: flex; flex-direction: column }"],
    styleUrls: ['./Bingo.scss']
})


/* Hookpoint: ClassContent:Annotations */

export  class Bingo extends BingoTOP {
    public constructor(route: ActivatedRoute, protected router: Router) {
        super(route, router);
    }

  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login']);
    }
    super.ngOnInit();
  }

}
