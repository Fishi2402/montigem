/* (c) https://github.com/MontiCore/monticore */

// HWC-P

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
// one of the few lines which always have to be hand-written
// TODO: find a solution to remove
import { routes } from './stanley/stanley.routing';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    // one of the few lines which always have to be hand-written
    // TODO: find a solution to remove
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
