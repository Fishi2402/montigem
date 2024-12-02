import { PortalModule } from '@angular/cdk/portal';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GemDialog } from './GemDialog';

@NgModule({
  "declarations": [GemDialog], "imports": [CommonModule, PortalModule], "exports": [GemDialog]
})
export class GemdialogModule {

}
