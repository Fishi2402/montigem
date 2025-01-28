/* (c) https://github.com/MontiCore/monticore */

import { CityInfo } from 'hardy/flensburg/CityInfo';
import { Person } from 'hardy/flensburg/Person';

// Hand-Written, before generation
export class CityInfoClientImpl extends CityInfo {

  override getNumberOfCities(): number {
    return 12;
  }

  override getTotalNumberOfCars(): number {
    return 0;
  }

  override getCityWithMostCars(): string {
    return "my";
  }

  override getAverageNumberOfCars(): number {
    return 0;
  }

  override sizeCities(): number {
    return 0;
  }

  override getCities(): Array<string> {
    return [];
  }

  override getPersonWithMostCars(): Person {
    return null;
  }

}
