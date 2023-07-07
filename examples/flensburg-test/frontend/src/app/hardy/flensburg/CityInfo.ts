/* (c) https://github.com/MontiCore/monticore */

import { CityInfoTOP } from '@src/hardy/flensburg/CityInfoTOP';
import { Person } from '@src/hardy/flensburg/Person';

export class CityInfo extends CityInfoTOP {

  override getNumberOfCities(): number {
    return 0;
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