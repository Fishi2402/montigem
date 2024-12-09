/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { AnimalAddFoodComponent,config } from '@src/test_update_of_builder/AnimalAddFoodComponent';
import {Option} from "@umlp/input";
import {LoadingTestsManager} from "@src/loadingtests/LoadingTestsManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class AnimalAddFood extends AnimalAddFoodComponent {

  private requestedBackend : boolean = false;
  private foodOptions: Option[] = [];

  public override getOptions(): Array<Option> {
    if(!this.requestedBackend) {
      console.warn("Requesting Options")
      LoadingTestsManager.getFoodList().then(foods => {
         foods.loadAll().then(
            foodies => {
              this.foodOptions = foodies.map(f => new Option(f.getGemId()+"", f.getName()))
            }
        )
        console.warn("Updating food options", foods.size())
      })
      this.requestedBackend=true
    }

    return this.foodOptions;
  }

  public override setFoodEntry(value: string) {
    super.setFoodEntry(value);
    LoadingTestsManager.getFoodSure(Number(value)).then(f =>
    {
      this.getAnimal().addFood2(f)
      this.getAnimal()._internal_getFoodGemList().loadAll()

      // Set all other values to default stuff
      this.getAnimal().setName("New Animal")
      this.getAnimal().setLegs(0)
    }
    )
  }
}
