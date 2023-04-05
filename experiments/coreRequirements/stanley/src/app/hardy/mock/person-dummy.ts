import { Car } from "../car";
import { Person } from "../person";

export const dummy0 = new Person();

export const dummy1 = new Person();
dummy1.setAge(32);
dummy1.setNickname("Stanley Hardy");
dummy1.setOwns([new Car(), new Car()]);

export const dummy2 = new Person();
dummy2.setAge(55);
dummy2.setNickname("Ultra Warrior");
dummy2.setOwns([new Car()]);
dummy2.setChildren([dummy0, dummy1]);

export const dummyPersons = [
  dummy0,
  dummy1,
  dummy2
];
