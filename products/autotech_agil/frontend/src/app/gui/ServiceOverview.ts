/* (c) https://github.com/MontiCore/monticore */
import { Service } from '@src/autotechagil/Service';
import { Component } from '@angular/core';
import { ServiceOverviewComponent, config } from '@src/gui/ServiceOverviewComponent';
import { Event } from '@umlp/common';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class ServiceOverview extends ServiceOverviewComponent {

  public override goToServiceDetails(service: Service): Event {
    return () => {
      this._router.navigate(["gui", "ServiceDetail", "" + service.getGemId()]);
    }
  }

}
