import { Car } from "./car";

// TODO translate from Java
export class PersonTOP {

  protected nickname: string;
  protected age: number;
  protected owns: Car[];

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

}