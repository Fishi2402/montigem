/* (c) https://github.com/MontiCore/monticore */

import { Service } from '@src/domain/Service';
import { ServiceStatus } from '@src/domain/ServiceStatus';
import { Component } from '@angular/core';
import { GemStatusIndicatorExampleComponent, config } from '@src/example_basic/GemStatusIndicatorExampleComponent';
import { Event } from '@umlp/common';
import {GemStatus} from "@umlp/basic";
import {Option} from "@umlp/input";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class GemStatusIndicatorExample extends GemStatusIndicatorExampleComponent {

  private possibleStates = [
    {
      statusId: ServiceStatus.RED,
      imageSource: "assets/images/traffic-light-red.png",
      label: "Red",
      threshold: 30.0
    },
    {
      statusId: ServiceStatus.YELLOW,
      imageSource: "assets/images/traffic-light-yellow.png",
      label: "Yellow",
      threshold: 20.0
    },
    {
      statusId: ServiceStatus.GREEN,
      imageSource: "assets/images/traffic-light-green.png",
      label: "Green",
      threshold: 0.0
    }
  ];

  private statusOptions = Object.values(ServiceStatus)
      .filter(statusValue => isNaN(Number(statusValue)))
      .map(option => {
        const optionString: string = option.toString();
        return new Option(optionString.charAt(0).toUpperCase() +
            optionString.slice(1).toLowerCase());
      });

  public override getPossibleStates(): Array<GemStatus> {
    return this.possibleStates;
  }

  public override getStatusOptions(): Array<Option> {
    return this.statusOptions;
  }

  public override changeStatus(temp: number): GemStatus {
    if (temp >= 0 && temp < 20) {
      return this.possibleStates.find(status => status.statusId == ServiceStatus.GREEN);
    }
    if (temp >= 20 && temp < 30) {
      return this.possibleStates.find(status => status.statusId == ServiceStatus.YELLOW);
    }
    return this.possibleStates.find(status => status.statusId == ServiceStatus.RED);
  }


}
