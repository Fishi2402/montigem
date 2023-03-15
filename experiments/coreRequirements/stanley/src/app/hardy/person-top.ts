import { Car } from "./car";
import {Person} from "./person";

// TODO translate from Java
export class PersonTOP {

  protected nickname: string;
  protected age: number;
  protected owns: Car[];
  protected children: Person[];

  getNickname(): string {
    return this.nickname;
  }

  setNickname(v: string): void {
    this.nickname = v;
  }

  getAge(): number {
    return this.age;
  }

  setAge(v: number): void {
    this.age = v;
  }

  getOwns(): Car[] {
    return this.owns;
  }

  setOwns(v: Car[]) {
    this.owns = v;
  }
  // other list operations

  getChildren(): Person[] {
    return this.children;
  }

}
