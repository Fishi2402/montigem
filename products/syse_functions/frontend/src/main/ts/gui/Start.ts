/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {Component, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {config, StartComponent} from '@src/gui/StartComponent';
import { ComponentObserverManager } from '@umlp/commonj2ts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: [...config.styleUrls]
})
export class Start extends StartComponent implements OnDestroy {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super(router)
  }

  public ngOnDestroy(): void {
    ComponentObserverManager.removeObservers("gui.Start");
  }

  public override getGemText3Value(): string {
    return "In diesem Projekt behandeln wir als Funktionen modellierte Systeme.";
  }
}

Start["__class"] = "gui.Start";



