import {Component, OnInit} from '@angular/core';
import {FlensburgManager} from "../../build/frontendGen/build/appdomain//hardy/flensburg/FlensburgManager";
import {
  FlensburgManagerClientImpl
} from "../../build/frontendGen/build/appdomain//hardy/flensburg/FlensburgManagerClientImpl";
import {
  ClientCommandManager,
  ClientCommandWebsocketCommunication,
  CommandManager
} from "umlp-frontend-rte";
import {Address} from "../../build/frontendGen/build/appdomain//hardy/flensburg/Address";
import {Person} from "../../build/frontendGen/build/appdomain//hardy/flensburg/Person";

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
    this.initBasics();
    document.getElementById("createAddress").onclick = this.createAddress();
    document.getElementById("logAddresses").onclick = this.logAddresses();
    document.getElementById("createPerson").onclick = this.createPerson();
    document.getElementById("logPersons").onclick = this.logPersons();
  }

  private initBasics() {
    FlensburgManagerClientImpl.init();
    ClientCommandManager.init();

    FlensburgManagerClientImpl.init();
    const ws_protocol = document.location.protocol == "https:" ? "wss": "ws";
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
        ws_protocol + '://' + document.location.host + '/umlp/api/command'
    ));

    setTimeout(function(){
      console.log("Executed after 1 second");
    }, 1000);

    CommandManager.initMe(cmdManager);
  }

  private createAddress(): (MouseEvent) => Promise<void> {
    return async function (p1: MouseEvent) {
      console.log("clicked Create Address");
      let addressBuilder = await FlensburgManager.addressBuilder();
      let address = await addressBuilder.city("Aachen").build();
      console.log(address.getGemId());
    };
  }

  private logAddresses(): (MouseEvent) => Promise<void> {
    return async function (p1: MouseEvent) {
      console.log("clicked Log Addresses");
      let addresses = await FlensburgManager.getAddressList();
      let realAddresses: Array<Address> = [];
      for await (let address of addresses) {
        realAddresses.push(address);
      }
      console.log(realAddresses);
    };
  }

  private createPerson(): (MouseEvent) => Promise<void> {
    return async function(p1: MouseEvent) {
      console.log("clicked Create Person");
      let value = (<HTMLTextAreaElement>document.getElementById("addressId")).value;
      let personBuilder = await FlensburgManager.personBuilder();
      let mainResidence = await FlensburgManager.getAddressSure(+value);
      await personBuilder.age(30).nickname("Malte").mainResidence(mainResidence).build();
    }
  }

  private logPersons(): (MouseEvent) => Promise<void> {
    return async function (p1: MouseEvent) {
      console.log("clicked Log Addresses");
      let persons = await FlensburgManager.getPersonList();
      let realPersons: Array<Person> = [];
      for await (let person of persons) {
        realPersons.push(person);
      }
      console.log(realPersons);
    };
  }
}
