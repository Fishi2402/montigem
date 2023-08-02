import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { routes } from './app.routing';
import { Navigation } from '@src/navigation/navigation.component';
import { FavoritelistModule } from '@src/gui/widgets/navigation/FavoriteListModule';

@NgModule({
  declarations: [
    AppComponent,
    Navigation
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    FavoritelistModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
