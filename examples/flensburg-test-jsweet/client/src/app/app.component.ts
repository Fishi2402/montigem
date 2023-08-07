import {Component, OnInit} from '@angular/core';
import {FlensburgManager} from "../../target/generated-sources/ts/hardy/flensburg/FlensburgManager";
import {
  FlensburgManagerClientImpl
} from "../../target/generated-sources/ts/hardy/flensburg/FlensburgManagerClientImpl";
import {PersonBuilder} from "../../target/generated-sources/ts/hardy/flensburg/PersonBuilder";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'client';

  constructor() {
  }

  ngOnInit(): void {
    console.log("started App");
    document.getElementById("myBox").innerText = "hallo";
    FlensburgManagerClientImpl.init();
    this.init();
  }

  async init() {
    let addressBuilder = await FlensburgManager.addressBuilder();
    let address = await addressBuilder.city("Aachen").build();
  }
}
