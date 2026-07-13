/* (c) https://github.com/MontiCore/monticore */

import {config, HeaderComponent} from "gui/base/HeaderComponent";
import {Component} from "@angular/core";
import {Location} from '@angular/common';
import {Log} from "@umlp/commonj2ts";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class Header extends HeaderComponent {
  /*private*/ timerInterval: number;

  /*private*/ passedSeconds: number;

  public constructor(protected location: Location) {
    super();
  }
  public override async init(): Promise<void> {
    await super.init();
    clearInterval(this.timerInterval);
    if(this.isTimerNeeded()) {
      this.passedSeconds = 0.0;
      const lambda: (p1: void) => void = ($) => {
        this.passedSeconds = this.passedSeconds + 0.1;
        this.setProgress((<any>Math).fround((1 - (this.passedSeconds / 60))));
        if (this.passedSeconds > 60) {
          clearInterval(this.timerInterval);
          Log.warn("User timed out go back to main page");
          this._router.navigate(["gui", "kitchen", "KitchenPage"]);
        }
      };
      this.timerInterval = setInterval(lambda, 100);
    }
    else {
      this.setProgress(0);
    }

  }

  public override navigateBack(): (arg: void) => void {
    return $ => {
      if (this.getHeadline() === "Admin Login") {
        this._router.navigate(["gui", "kitchen", "KitchenPage"]);
      } else {
        this.location.back();
      }
    };
  }

  public override destroy() {
    super.destroy();
    clearInterval(this.timerInterval);
  }
}
