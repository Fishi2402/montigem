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
    { url: '/example_basic/GemButtonExample', title: 'Button' },
    { url: '/example_basic/GemAudioExample', title: 'Audio' },
    { url: '/example_basic/GemImageExample', title: 'Image' },
    { url: '/example_basic/GemLinkExample', title: 'Link' },
    { url: '/example_basic/GemTextExample', title: 'Text' },
    { url: '/example_table/GemTableExample', title: 'Table' },
    { url: '/example_arrange/GemCardExample', title: 'Card' },
    { url: '/example_arrange/GemColumnExample', title: 'Column' },
    { url: '/example_arrange/GemRowExample', title: 'Row' }



  ];

}
