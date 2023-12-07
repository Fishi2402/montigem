<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existedModuleConfig","existHWCRoutes","pathHWRoutes","pathModuleConfig")}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { CommonModule } from "@angular/common"
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
  AppComponent,
<#if existedModuleConfig >
  ... config.declarations,
</#if>
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    CommonModule,


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
