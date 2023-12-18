/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { GuidslpaperSecurityManager } from "@src/guidslpaper/GuidslpaperSecurityManager";
import { User } from '@src/guidslpaper/User';
import { GuidslPaperNavHeaderComponent } from '@src/gui/navigation/GuidslPaperNavHeaderComponent';

@Component({
  selector: "guidsl-paper-nav-header",
  templateUrl: "./GuidslPaperNavHeader.html",
  styleUrls: ["./GuidslPaperNavHeader.scss"]
})
export class GuidslPaperNavHeader extends GuidslPaperNavHeaderComponent {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super();
    this.router = router;
  }

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return GuidslpaperSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      GuidslpaperSecurityManager.signOut();
      this.router.navigate(['gui', 'login', 'LoginPage']);
    });
  }

  isSignedIn(): boolean {
    return GuidslpaperSecurityManager.isCurrentlyLoggingIn() || GuidslpaperSecurityManager.isSignedIn();
  }
}
