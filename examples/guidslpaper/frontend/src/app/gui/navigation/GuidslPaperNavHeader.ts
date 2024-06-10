/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { GuidslpaperSecurityManager } from "@src/guidslpaper/GuidslpaperSecurityManager";
import { User } from '@src/guidslpaper/User';
import { GuidslPaperNavHeaderComponent,config } from '@src/gui/navigation/GuidslPaperNavHeaderComponent';
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[... config.imports,RouterModule,CommonModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class GuidslPaperNavHeader extends GuidslPaperNavHeaderComponent {

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return GuidslpaperSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      GuidslpaperSecurityManager.signOut();
      this._router.navigate(['gui', 'login', 'LoginPage']);
    });
  }

  isSignedIn(): boolean {
    return GuidslpaperSecurityManager.isCurrentlyLoggingIn() || GuidslpaperSecurityManager.isSignedIn();
  }
}
