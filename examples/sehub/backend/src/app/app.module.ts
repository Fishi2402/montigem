

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Gui_Module } from '../../build/appgui/gui/Gui.Module'


  import { routes } from './app.routing';


@NgModule({
declarations: [
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    Gui_Module,

],
providers: [
],
})

export class AppModule { }
