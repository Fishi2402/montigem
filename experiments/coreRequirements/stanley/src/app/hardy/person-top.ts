// TODO translate from Java
export class PersonTOP {

  protected nickname = '';
  protected age = 0;

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

}