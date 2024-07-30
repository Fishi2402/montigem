/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { DemoRecComponent } from '@src/demorec/DemoRecComponent';
import { State } from '@src/demorec/State';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

import { JoinConnectionEditComponent, config } from '@src/gui/joinconnection/JoinConnectionEditComponent';

import {
  Component,
  OnInit
} from '@angular/core';

import { Option } from '@umlp/input';
import {BatteryPackService} from "../../BatteryPackService";
import { NavigationEnd,ActivatedRoute, Router } from '@angular/router';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class JoinConnectionEdit extends JoinConnectionEditComponent implements OnInit {
  joinConnectionTypeOptions : Option[] = []
  componentOptions : Option[] = []
  filteredComponentOptions : Option[] = []
  connectsComponentEntry : string = ""
  conditionOptions : Option[] = []

  currentPack : BatteryPack

  public constructor(protected packService: BatteryPackService) {
    super();
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
      this.filteredComponentOptions = [newOption].concat(
          this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.value)))
    })

    this._router.events.subscribe((val) => {
      if(val instanceof NavigationEnd) {
        if(this._route.snapshot.paramMap.get("connectionRoutingId") != null) {
          this.connectionRoutingId = +this._route.snapshot.paramMap.get("connectionRoutingId");
          this.ngOnInit()
        }
      }
    });

  }

  private getCurrentPackComponents() : Set<string> {
    if(this.currentPack) {
      return new Set<string>(this.currentPack.getAllComponentsList().map(comp => comp.getGemId()+""))
    } else {
      return null
    }
  }

  public override ngOnInit(){
    super.ngOnInit()

    // Initialize joinConnectionTypeOptions
    this.joinConnectionTypeOptions = [];
    Object.keys(JoinConnectionType).filter(s => !isNaN(+s)).forEach(key => {
      this.joinConnectionTypeOptions.push({value: key, display: JoinConnectionType[key]});
    })

    this.conditionOptions = []
    Object.keys(State).filter(s => !isNaN(+s)).forEach(key => {
      this.conditionOptions.push({value: key, display: State[key]});
    })

    // Initialize componentOptions and superComponentsOptions
    DemoRecManager.getDemoRecComponentList().then(componentList => {
      const components = componentList;
      components.loadAll().then(() => {
        console.debug(components)
        this.componentOptions = [newOption]
        for (const component of components) {
          if(!(component instanceof BatteryPack)) {
            console.debug("Refill Join Connection Options")
            const value = component.getGemId() + "";
            const display = component.getName()? component.getName() : value
            this.componentOptions.push({value: value, display: display});
          }
        }
        this.componentOptions = this.componentOptions.slice();
        this.filteredComponentOptions = [newOption].concat(
            this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.value)))
      });
    });
  }

  // Select condition of connection
  public override getConditionOptions (): Array<Option> {
    return this.conditionOptions;
  }

  public override getConditionEntry (): string {
    return this.getConnection()?.getCondition()?.toString()
  }

  public override setConditionEntry (value: string): void {
    Object.keys(State).filter((v) => isNaN(Number(v))).forEach((key, index) => {
      if (+value === index) {
        this.getConnection()?.setCondition(State[key])
      }
    })
  }

/*  public override changeChosenKind() {
    return (jCKind) => {
      this.getConnection().setJoinConnectionKind1(jCKind)
    }
  }*/


}
