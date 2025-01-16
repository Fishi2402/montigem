/* (c) https://github.com/MontiCore/monticore */

import { Service } from '@src/autotechagil/Service';
import { Status } from "@src/autotechagil/Status";
import { Component } from '@angular/core';
import { PortDetailComponent, config } from '@src/gui/PortDetailComponent';
import { Option } from "@umlp/input";
import { SteeringValue } from "@src/autotechagil/SteeringValue";


@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class PortDetail extends PortDetailComponent {

  public override getSteeringValues(): SteeringValue[] {
    return this.getPort().getFunctionStream().getValueList() as SteeringValue[];
  }

  /*
  private possibleStates: Status[] = [];

  private statusOptions = Object.values(ServiceStatus)
      .filter(statusValue => isNaN(Number(statusValue)))
      .map(option => {
        const optionString: string = option.toString();
        return new Option(optionString.charAt(0).toUpperCase() +
            optionString.slice(1).toLowerCase());
      });

  public override init(): void {
    super.init();
    const statusRed: Status = new Status();
    statusRed.setStatusId(ServiceStatus.RED);
    statusRed.setImageSource("assets/images/traffic-light-red.png");
    statusRed.setLabel("Red");
    statusRed.setThreshold(30.0);
    this.possibleStates.push(statusRed);
    const statusYellow: Status = new Status();
    statusYellow.setStatusId(ServiceStatus.YELLOW);
    statusYellow.setImageSource("assets/images/traffic-light-yellow.png");
    statusYellow.setLabel("Yellow");
    statusYellow.setThreshold(20.0);
    this.possibleStates.push(statusYellow);
    const statusGreen: Status = new Status();
    statusGreen.setStatusId(ServiceStatus.GREEN);
    statusGreen.setImageSource("assets/images/traffic-light-green.png");
    statusGreen.setLabel("Green");
    statusGreen.setThreshold(0.0);
    this.possibleStates.push(statusGreen);
  }

  public override getPossibleStatuses(): Array<Status> {
    return this.possibleStates;
  }

  public override getStatusOptions(): Array<Option> {
    return this.statusOptions;
  }

  public override changeStatus(temp: number): Status {
    if (temp >= 0 && temp < 20) {
      return this.possibleStates.find(status => status.getStatusId() == ServiceStatus.GREEN);
    }
    if (temp >= 20 && temp < 30) {
      return this.possibleStates.find(status => status.getStatusId() == ServiceStatus.YELLOW);
    }
    return this.possibleStates.find(status => status.getStatusId() == ServiceStatus.RED);
  }
   */

}
