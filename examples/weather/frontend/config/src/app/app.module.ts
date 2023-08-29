import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { routes } from './app.routing';
import { Navigation } from '@src/navigation/navigation.component';
import { NavigationModule } from '@src/gui/widgets/navigation/Navigation.Module';
import { GuiModule } from '@src/gui/Gui.Module';
@NgModule({
  declarations: [
    AppComponent,
    Navigation,

  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    NavigationModule,
    GuiModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
