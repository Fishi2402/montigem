<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature("existedModuleConfig")}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { routes } from './app.routing';
import { AppComponent } from 'src/app/app.component';
<#if existedModuleConfig >
  import { config } from 'src/app/appModuleConfig';
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
