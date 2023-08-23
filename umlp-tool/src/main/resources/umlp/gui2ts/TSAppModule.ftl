<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existedModuleConfig","existHWCRoutes")}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { AppComponent } from 'src/app/app.component';
<#if existedModuleConfig >
  import { config } from 'src/app/app.module.config';
</#if>

<#if existHWCRoutes >
  import { routes } from './app.routingTOP';
</#if>

<#if !existHWCRoutes >
  import { routes } from './app.routing';
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
