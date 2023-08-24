<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existedModuleConfig","existHWCRoutes","existAppComponent","pathHWRoutes","pathModuleConfig","pathAppComponent")}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
<#if existedModuleConfig >
  import { config } from '${pathModuleConfig}';
</#if>

<#if existHWCRoutes >
  import { routes } from '${pathHWRoutes}';
</#if>

<#if !existHWCRoutes >
  import { routes } from './app.routing';
</#if>

<#if existAppComponent >
  import { AppComponent } from '${pathAppComponent}';
</#if>

@NgModule({
declarations: [
<#if existedModuleConfig >
  ... config.declarations
</#if>
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
<#if existAppComponent >
  AppComponent,
</#if>

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
