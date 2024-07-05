/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { FinanzverwaltungSecurityManager } from "@src/finanzverwaltung/FinanzverwaltungSecurityManager";
import { User } from '@src/finanzverwaltung/User';
import { FinanzverwaltungNavHeaderComponent,config } from '@src/gui/navigation/FinanzverwaltungNavHeaderComponent';
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[... config.imports,RouterModule,CommonModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class FinanzverwaltungNavHeader extends FinanzverwaltungNavHeaderComponent {

  readonly items: { url: string, title: string }[] = [];

  getCurrentUser(): User {
    return FinanzverwaltungSecurityManager.getCurrentUser();
  }

  signOutCurrentUser(): void {
    fetch("/umlp/api/logout").finally(() => {
      FinanzverwaltungSecurityManager.signOut();
      this._router.navigate(['gui', 'login', 'LoginPage']);
    });
  }

  isSignedIn(): boolean {
    return FinanzverwaltungSecurityManager.isCurrentlyLoggingIn() || FinanzverwaltungSecurityManager.isSignedIn();
  }
}
