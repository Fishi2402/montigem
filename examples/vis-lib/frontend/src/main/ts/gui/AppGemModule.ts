

//always imported
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { CommonModule } from "@angular/common"
import { routes } from './AppGemRouting';
import { AppComponent } from 'app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { config } from './AppGemModuleConfig';

@NgModule({
declarations: [
  AppComponent,
],
imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    CommonModule,
    BrowserAnimationsModule,
    ...config.imports
],
providers: [
],
bootstrap: [AppComponent],
})

export class AppGemModule { }
