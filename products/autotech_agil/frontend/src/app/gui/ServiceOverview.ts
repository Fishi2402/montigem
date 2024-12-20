/* (c) https://github.com/MontiCore/monticore */
import { Service } from '@src/autotechagil/Service';
import { Component } from '@angular/core';
import { ServiceOverviewComponent, config } from '@src/gui/ServiceOverviewComponent';
import { Event } from '@umlp/common';
import { Port } from '@src/autotechagil/Port';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class ServiceOverview extends ServiceOverviewComponent {

  /*
  public override goToPortDetails(port: Port): Event {
    return () => {
      this._router.navigate(["gui", "PortDetail", "" + port.getGemId()]);
    }
  }
   */

  public override selectPort(port: Port): Event {
    return () => this.setSelectedPort(port);
  }
}
