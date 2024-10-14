/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { AnimalCreateComponent,config } from '@src/testUpdateOfBuilder/AnimalCreateComponent';
import {Option} from "@umlp/input";
import {LoadingTestsManager} from "@src/loadingtests/LoadingTestsManager";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class AnimalCreate extends AnimalCreateComponent {

  private requestedBackend : boolean = false;
  private foodOptions: Option[] = [];
  private foodEntry = "";

  public override getChooseFoodToAddOptions(): Array<Option> {
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

  public override getChooseFoodToAddEntry(): string {
    return this.foodEntry;
  }

  public override setChooseFoodToAddEntry(value: string) {
    LoadingTestsManager.getFoodSure(Number(value)).then(f =>
        this.getAnimal().addFood2(f)
    )
  }
}
