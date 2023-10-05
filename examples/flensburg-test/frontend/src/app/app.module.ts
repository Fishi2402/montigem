

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Gui_Module } from '../../build/appgui/gui/Gui.Module'

  import { routes } from '../../src/app/app.routing';


  import { AppComponent } from '../../src/app/app.component';

@NgModule({
declarations: [
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    Gui_Module,
  AppComponent,

],
providers: [
],
})

export class AppModule { }
