// GEN-P: translated from Java

import { Directive } from "@angular/core";
import { dummy1 } from "../../../hardy/mock/person-dummy";
import { Person } from "../../../hardy/person";
import { PersonErrorObserver } from "../../../hardy/person-error-observer";

// Info for translation:
// Instead of @Component annotation @Directive is used if translated HWC-P file exists
@Directive()
export class PersonCreateTOP implements PersonErrorObserver {

  public p: Person;

  protected gemTextInputComp1ValueError: string = '';

  protected gemNumberInputComp2ValueError: string = '';

  constructor() {
    // TODO: person loading
    this.p = new Person();
  }

  public ngOnInit(): void {
  }

  public getGemCardComp1Title(): string {
    return "Create Person";
  }

  public getGemTextInputComp3ValueError(): string {
    return this.gemTextInputComp1ValueError;
  }

  public notifyNicknameInvalid(p: Person, msg: string): void {
    this.gemTextInputComp1ValueError = msg;
  }

  public getGemTextInputComp3Value(): string {
    return this.p.getNickname();
  }

  public setGemTextInputComp3Value(nickname: string): void {
    this.p.setNickname(nickname);
  }

  public getGemNumberInputComp4ValueError(): string {
    return this.gemNumberInputComp2ValueError;
  }

  public notifyAgeInvalid(p: Person, msg: string): void {
    this.gemNumberInputComp2ValueError = msg;
  }

  public getGemNumberInputComp4Value(): number {
    return this.p.getAge();
  }

  public setGemNumberInputComp4Value(age: number): void {
    this.p.setAge(age);
  }

  public gemButtonSaveButtonLeftClick(): void {
    // return this.navigate();
  }

  public getGemButtonSaveButtonText(): string {
    return "Ok";
  }

  /*
  protected navigate(): CreateierePersonCars {
    return new CreateierePersonCars(p);
  }
  /**/

  notifyOwnsInvalid(person: Person, msg: string): void { };
  notifyMainResidenceInvalid(person: Person, msg: string): void { };
}
