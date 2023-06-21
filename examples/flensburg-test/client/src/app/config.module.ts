
import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { GemNavigation } from "./gem-navigation/gem-navigation.component";
import { Welcome } from "./welcome/welcome.component";

@NgModule({
  declarations: [
    Welcome,
    GemNavigation
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    Welcome,
    GemNavigation
  ]
})
export class ConfigModule { }

