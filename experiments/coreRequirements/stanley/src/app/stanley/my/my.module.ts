

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { InfoBox } from "./info-box/info-box.component";
import { Navigation } from "./navigation/navigation.component";

@NgModule({
  declarations: [
    InfoBox,
    Navigation
  ],
  imports: [
    CommonModule,
    RouterModule,
  ],
  exports: [
    InfoBox,
    Navigation
  ]
})

export class MyModule { }
