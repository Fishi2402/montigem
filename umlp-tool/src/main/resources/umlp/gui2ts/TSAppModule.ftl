<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existedModuleConfig","existHWCRoutes","pathHWRoutes","pathModuleConfig")}

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
import { routes } from './AppGemRouting';
</#if>
import { AppComponent } from '@src/app.component';


@NgModule({
declarations: [
<#if existedModuleConfig >
  ... config.declarations,
  AppComponent,
</#if>
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),


<#if existedModuleConfig >
  ... config.imports
</#if>
],
providers: [
<#if existedModuleConfig >
  ... config.providers
</#if>
],
bootstrap: [AppComponent],
})

export class AppGemModule { }
