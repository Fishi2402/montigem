<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existModuleConfig","existHWRoutes","pathHWRoutes","pathModuleConfig")}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, RouteReuseStrategy, BaseRouteReuseStrategy } from '@angular/router';
import { CommonModule } from "@angular/common"
<#if existModuleConfig >
import { config } from '${pathModuleConfig}';
</#if>
<#if existHWRoutes >
import { routes } from '${pathHWRoutes}';
</#if>
<#if !existHWRoutes >
import { routes } from './AppGemRouting';
</#if>
import { AppComponent } from '@src/app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
declarations: [
  AppComponent,
<#if existModuleConfig>
  ...config.declarations,
</#if>
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    CommonModule,
    BrowserAnimationsModule,
<#if existModuleConfig>
  ...config.imports
</#if>
],
providers: [
  {
    provide: RouteReuseStrategy,
    useClass: class extends BaseRouteReuseStrategy { shouldReuseRoute() { return false; } }
  },
<#if existModuleConfig >
  ...config.providers
</#if>
],
bootstrap: [AppComponent],
})

export class AppGemModule { }
