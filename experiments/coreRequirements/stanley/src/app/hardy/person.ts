import { PersonTOP } from "./person-top";

export class Person extends PersonTOP {

  public decreaseAge(): void {
    this.setAge(this.getAge() - 1);
  }

}