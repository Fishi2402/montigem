// GEN-P: translated from Java

import { Directive } from "@angular/core";
import { dummy } from "../../../hardy/mock/person-dummy";
import { Person } from "../../../hardy/person";
import { PersonErrorObserver } from "../../../hardy/person-error-observer";

// Info for translation:
// Instead of @Component annotation @Directive is used if translated HWC-P file exists
@Directive()
export class PersonEditTOP implements PersonErrorObserver {

  public p: Person;

  protected textInputComp1Error: string = '';

  protected numberInputComp2Error: string = '';

  constructor() {
    // TODO: person loading
    this.p = dummy;
  }

  public ngOnInit(): void {
  }

  public getTextInputComp1Error(): string {
    return this.textInputComp1Error;
  }

  public notifyNicknameInvalid(p: Person, msg: string): void {
    this.textInputComp1Error = msg;
  }

  public getTextInputComp1(): string {
    return this.p.getNickname();
  }

  public setTextInputComp1(nickname: string): void {
    this.p.setNickname(nickname);
  }

  public getNumberInputComp2Error(): string {
    return this.numberInputComp2Error;
  }

  public notifyAgeInvalid(p: Person, msg: string): void {
    this.numberInputComp2Error = msg;
  }

  public getNumberInputComp2(): number {
    return this.p.getAge();
  }

  public setNumberInputComp2(age: number): void {
    this.p.setAge(age);
  }

  public leftClickButtonComp3(): void {
    // return this.navigate();
  }

  public leftClickButtonComp5(): void {
    this.p.decreaseAge();
  }

  /*
  protected navigate(): EditierePersonCars {
    return new EditierePersonCars(p);
  }
  /**/

  notifyOwnsInvalid(person: Person, msg: string): void { };
  notifyMainResidenceInvalid(person: Person, msg: string): void { };
}
