// GEN-P: translated from Java

import { Directive } from "@angular/core";
import { dummy1 } from "../../../hardy/mock/person-dummy";
import { Person } from "../../../hardy/person";
import { PersonErrorObserver } from "../../../hardy/person-error-observer";

// Info for translation:
// Instead of @Component annotation @Directive is used if translated HWC-P file exists
@Directive()
export class PersonEditTOP implements PersonErrorObserver {

  public p: Person;

  protected gemTextInputComp1ValueError: string = '';

  protected gemNumberInputComp2ValueError: string = '';

  constructor() {
    // TODO: person loading
    this.p = dummy1;
  }

  public ngOnInit(): void {
  }

  public getGemTextInputComp1ValueError(): string {
    return this.gemTextInputComp1ValueError;
  }

  public notifyNicknameInvalid(p: Person, msg: string): void {
    this.gemTextInputComp1ValueError = msg;
  }

  public getGemTextInputComp1Value(): string {
    return this.p.getNickname();
  }

  public setGemTextInputComp1Value(nickname: string): void {
    this.p.setNickname(nickname);
  }

  public getGemNumberInputComp2ValueError(): string {
    return this.gemNumberInputComp2ValueError;
  }

  public notifyAgeInvalid(p: Person, msg: string): void {
    this.gemNumberInputComp2ValueError = msg;
  }

  public getGemNumberInputComp2Value(): number {
    return this.p.getAge();
  }

  public setGemNumberInputComp2Value(age: number): void {
    this.p.setAge(age);
  }

  public gemButtonComp3LeftClick(): void {
    // return this.navigate();
  }

  public getGemButtonComp3Text(): string {
    return "Ok";
  }

  public gemButtonComp4LeftClick(): void {
    this.p.decreaseAge();
  }

  public getGemButtonComp4Text(): string {
    return "Decrease";
  }

  /*
  protected navigate(): EditierePersonCars {
    return new EditierePersonCars(p);
  }
  /**/

  notifyOwnsInvalid(person: Person, msg: string): void { };
  notifyMainResidenceInvalid(person: Person, msg: string): void { };
}
