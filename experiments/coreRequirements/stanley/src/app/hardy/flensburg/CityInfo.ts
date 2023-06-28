/* (c) https://github.com/MontiCore/monticore */

import { CityInfoTOP } from '@src/hardy/flensburg/CityInfoTOP';
import { Person } from '@src/hardy/flensburg/Person';
export class CityInfo extends CityInfoTOP{
  protected init(): void {
  }
  getNumberOfCities(): number {
    return 0;
  }
  getTotalNumberOfCars(): number {
    return 0;
  }
  getCityWithMostCars(): string {
    return "";
  }

  getAverageNumberOfCars(): number {
    return 0;
  }
  sizeString(): number {
    return 0;
  }
  getString(): Array<string> {
    return [];
  }
  getPersonWithMostCars(): Person {
    return new Person();
  }
}