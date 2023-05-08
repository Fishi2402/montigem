/* (c) https://github.com/MontiCore/monticore */
// GEN-P: translated from Java

import { Component, Input } from "@angular/core";
import { dummy2 } from "src/app/hardy/mock/person-dummy";
import { Person } from "src/app/hardy/person";
import { PersonErrorObserver } from "src/app/hardy/person-error-observer";

@Component({
  "selector": "person-children-edit",
  "templateUrl": "./person-children-edit.component.html",
  "styleUrls": ["./person-children-edit.component.scss"]
})
export class PersonChildrenEdit implements PersonErrorObserver {

  @Input()
  public p: Person;

  protected gemNumberInputComp1ValueOfComp0Error: Map<number, string> = new Map();

  protected numberInputError: string = "";

  protected numberInputErrorChildren: string[] = [];

  public constructor() {
    this.p = dummy2;
  }

  ngOnInit() {
  }

  // TODO: use GemList
  public getIterateComp0It(): Person[] {
    return this.p.getChildren();
  }

  public getGemNumberInputComp1ValueOfComp0(idx: number) {
    return this.getIterateComp0It()[idx].getAge();
  }

  public setGemNumberInputComp1ValueOfComp0(value: number, idx: number): void {
    this.getIterateComp0It()[idx].setAge(value);
  }

  public notifyAgeInvalid(p: Person, msg: string): void {
    this.gemNumberInputComp1ValueOfComp0Error.set(p.getGemId(), msg);
  }

  public getGemNumberInputComp1ValueOfComp0Error(gemId: number): string {
    if (this.gemNumberInputComp1ValueOfComp0Error.has(gemId)) {
      return this.gemNumberInputComp1ValueOfComp0Error.get(gemId);
    }
    return "";
  }

  notifyOwnsInvalid(person: Person, msg: string): void { }
  notifyMainResidenceInvalid(person: Person, msg: string): void { }
  notifyNicknameInvalid(person: Person, msg: string): void { }

}
