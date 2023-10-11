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
<<<<<<< HEAD
    { url: '/basic/GemButtonExample', title: 'GemButtonExample' },
    { url: '/basic/GemAudioExample', title: 'GemAudioExample' }

=======
    { url: '/basic/GemButtonExample', title: 'GemButtonExample' }
>>>>>>> refs/remotes/origin/lisalin
  ];

}
