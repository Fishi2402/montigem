// always imported
import { Routes } from "@angular/router";
// imports of pages in the package
import { UserDisplayComponent } from "./UserDisplayComponent"
import { UserSettingsComponent } from "./UserSettingsComponent"

export const routes: Routes = [
  {
    path: "UserDisplay/:userRoutingId",
    component: UserDisplayComponent
  },
  {
    path: "UserSettings/:currentUserRoutingId",
    component: UserSettingsComponent
  }
]
