import {NgModule} from '@angular/core';
import {MatDialogModule} from '@angular/material/dialog';
import {DialogComponent} from "./dialog.component";
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';

@NgModule({
    declarations: [
        DialogComponent,
    ],
    imports: [
        MatDialogModule,CommonModule, BrowserModule
    ],
    exports: [
        DialogComponent
    ]
})

export class DialogModule {
}
