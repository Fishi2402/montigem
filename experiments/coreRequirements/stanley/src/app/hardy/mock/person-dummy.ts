import { Car } from "../car";
import { Person } from "../person";

export const dummy1 = new Person();
dummy1.setAge(32);
dummy1.setNickname("Stanley Hardy");
dummy1.setOwns([new Car(), new Car()]);

export const dummy2 = new Person();
dummy2.setAge(55);
dummy2.setNickname("Ultra Warrior");
dummy2.setOwns([new Car()]);

export const dummyPersons = [
  dummy1,
  dummy2
];
