<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates a routes file from the given model -->
${tc.signature()}

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { routes } from './app.routing';
import { Navigation } from './navigation/navigation.component';

@NgModule({
declarations: [
    Navigation
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
],
providers: [],
})

export class AppModule { }
