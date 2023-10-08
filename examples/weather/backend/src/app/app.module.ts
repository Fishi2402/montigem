

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Gui_Module } from '../../build/appgui/gui/GuiGemModule';
import { routes } from './app.routing';
import { AppComponent } from './app.component';


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
