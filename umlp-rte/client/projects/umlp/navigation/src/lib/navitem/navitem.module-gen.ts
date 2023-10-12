import {NgModule} from '@angular/core';
import {NavItem} from "./nav-item/nav-item.component-gen";
import {CommonModule} from "@angular/common";

@NgModule(
  {
    "declarations": [NavItem],
    "imports": [CommonModule],
    "exports": [NavItem]
  })
export class NavitemModule {

}
