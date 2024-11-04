/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {config, TestAtomicUpdateComponent} from '@src/test_update_list/TestAtomicUpdateComponent';
import {ReplaceFoodItem} from '@src/loadingtests/commands/ReplaceFoodItem';
import {CommandManager, Event} from "@umlp/common";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class TestAtomicUpdate extends TestAtomicUpdateComponent {
  public replaceFirst(): Event<void> {
    return () => {
      // Send the command when the button is clicked
      CommandManager.executeCommand(new ReplaceFoodItem(this.getUlh().getTestAnimalGem().getGemId()));
    }
  }
}
