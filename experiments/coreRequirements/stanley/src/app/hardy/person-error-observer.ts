import { Person } from "./person"

export interface PersonErrorObserver {
  notifyOwnsInvalid(person: Person, msg: string): void;
  notifyAgeInvalid(person: Person, msg: string): void;
  notifyNicknameInvalid(person: Person, msg: string): void;
  notifyMainResidenceInvalid(person: Person, msg: string): void;
}