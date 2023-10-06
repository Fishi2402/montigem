<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existedModuleConfig","existHWCRoutes","pathHWRoutes","pathModuleConfig")}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Gui_Module } from '../../build/appgui/gui/Gui.Module'
<#if existedModuleConfig >
import { config } from '${pathModuleConfig}';
</#if>
<#if existHWCRoutes >
import { routes } from '${pathHWRoutes}';
</#if>
<#if !existHWCRoutes >
import { routes } from './app.routing';
</#if>
import { AppComponent } from './app.component';


@NgModule({
declarations: [
<#if existedModuleConfig >
  ... config.declarations
</#if>
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    Gui_Module,
    AppComponent,

<#if existedModuleConfig >
  ... config.imports
</#if>
],
providers: [
<#if existedModuleConfig >
  ... config.providers
</#if>
],
})

export class AppModule { }
