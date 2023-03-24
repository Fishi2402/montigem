/* (c) https://github.com/MontiCore/monticore */

// HWC-P

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { ConfigModule } from './stanley/config.module';
import { MyModule } from './stanley/my/my.module';
// one of the few lines which always have to be hand-written
// TODO: find a solution to remove
import { routes } from './stanley/stanley.routing';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    ConfigModule,
    MyModule,
    // one of the few lines which always have to be hand-written
    // TODO: find a solution to remove
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
