/* (c) https://github.com/MontiCore/monticore */
import { Component } from "@angular/core";
import { FavoriteListComponent } from '@src/gui/widgets/navigation/FavoriteListComponent';
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[FavoriteListComponent,RouterModule,CommonModule],
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  readonly items: { url: string, title: string }[] = [
    { url: '/gui/WeatherView', title: 'Weather View' },
    { url: '/gui/WeatherApp/1', title: 'Weather App' },
  ];

}
