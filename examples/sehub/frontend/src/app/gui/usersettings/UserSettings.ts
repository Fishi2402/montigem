/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {UserSettingsComponent,config} from '@src/gui/usersettings/UserSettingsComponent';
import {SehubRole} from '@src/sehub/SehubRole';
import {User} from '@src/sehub/User';
import {CommandManager} from '@umlp/common';
import {User_setPassword} from '@src/sehub/commands/User_setPassword';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {SehubManager} from '@src/sehub/SehubManager';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class UserSettings extends UserSettingsComponent {

  private changePasswordValue = "";

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['gui/login/LoginPage']);
    }
    this.currentUser = SehubSecurityManager.getCurrentUser();
    this.currentUserRoutingId = SehubSecurityManager.getCurrentUserIdOrNull();

    SehubManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });

    super.ngOnInit();
  }

  public inviteUserLeftClick() {
    this.router.navigate(['gui/login/RegistrationPage']);
  }

  public override getInviteUserDisabled (): boolean {
    if(this.getUsers() == null || this.getUsers() == undefined){
      return true;
    }

    let curUser: User = this.getUsers().toArray().filter(user =>
      !!user && this.getCurrentUser().getUsername() === user.getUsername())[0];

    if (!!curUser && (SehubRole[curUser.getRole()] === "OWNER" ||
                      SehubRole[curUser.getRole()] === "MAINTAINER")) {
      return false;
    }

    return true;
  }

  public  getPasswordEntry (): string {
    return this.changePasswordValue;
  }

  public  setPasswordEntry (value: string): void {
    this.changePasswordValue = value;
  }

  public  changePassword (event: any): void {
    if(this.changePasswordValue === null || this.changePasswordValue === ""){
      alert("Password can not be empty!");
    }else{
      let gemId = SehubSecurityManager.getCurrentUser().getGemId();
      if(gemId >= 0){
        CommandManager.executeCommand(new User_setPassword(gemId, this.changePasswordValue));
        this.changePasswordValue = "";
      }else{
        console.error("Can not find gemId of current user!")
        alert("Internal error. Please log out and log in again")
      }

    }
  }
}
