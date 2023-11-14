import { Component } from "@angular/core";
import { Router } from '@angular/router';

@Component({
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  public constructor(protected router: Router) {
    this.router = router;
  }

  readonly items: { url: string, title: string }[] = [
    { url: '/person/Overview', title: 'Overview' },
    { url: '/basic/GemButtonExample', title: 'Button' },
    { url: '/basic/GemAudioExample', title: 'Audio' },
    { url: '/example_arrange/GemCardExample', title: 'Card' },
    { url: '/example_arrange/GemColumnExample', title: 'Column' },
    { url: '/example_arrange/GemRowExample', title: 'Row' }



  ];

}
