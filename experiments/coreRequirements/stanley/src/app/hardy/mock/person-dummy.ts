import { Car } from "../car";
import { Person } from "../person";

export const dummy = new Person();
dummy.setAge(32);
dummy.setNickname("Stanley Hardy");
dummy.setOwns([new Car(), new Car()]);