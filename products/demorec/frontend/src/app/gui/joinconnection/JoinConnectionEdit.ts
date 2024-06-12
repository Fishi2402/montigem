/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { DemoRecComponent } from '@src/demorec/DemoRecComponent';
import { State } from '@src/demorec/State';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

import { JoinConnectionEditComponent, config } from '../../../../build/frontendGen/build/appgui/gui/joinconnection/JoinConnectionEditComponent';

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

  public constructor(route :ActivatedRoute, router: Router, protected packService: BatteryPackService) {
    super(route, router);
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
      this.filteredComponentOptions = [newOption].concat(
          this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.value)))
    })

    router.events.subscribe((val) => {
      if(val instanceof NavigationEnd) {
        if(route.snapshot.paramMap.get("connectionRoutingId") != null) {
          this.connectionRoutingId = +route.snapshot.paramMap.get("connectionRoutingId");
          this.ngOnInit()
        }
      }
    });

  }

  private getCurrentPackComponents() : Set<string> {
    if(this.currentPack) {
      return new Set<string>(this.currentPack.getAllComponents().map(comp => comp.getGemId()+""))
    } else {
      return null
    }
  }

  public override ngOnInit(){
    super.ngOnInit()
    this.dummyComponentNames = [];

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


  // Select Type of connection
  public override getSelectTypeOfConnectionOptions (): Array<Option> {
    return this.joinConnectionTypeOptions
  }

  public override getSelectTypeOfConnectionEntry (): string {
    return this.getConnection()?.getJoinConnectionType()?.toString()
  }

  public override setSelectTypeOfConnectionEntry (value: string): void {
    Object.keys(JoinConnectionType).filter((v) => isNaN(Number(v))).forEach((key, index) => {
      if (+value === index) {
        this.getConnection().setJoinConnectionType(JoinConnectionType[key])
      }
    })
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

  // Select Connected Components
  public override getAddComponentDisabled(): boolean {
    return (this.getSelectConnectsComponentEntry() === newOption["value"])
  }

  public override getSelectConnectsComponentOptions(): Array<Option>{
    console.warn(this.getCurrentPackComponents())
    const filteredOptions = [newOption].concat(
        this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.value)))
    if(filteredOptions === this.filteredComponentOptions) {
      this.filteredComponentOptions = filteredOptions
    }
    return this.filteredComponentOptions
  }

  public override getSelectConnectsComponentEntry(): string{
    return this.connectsComponentEntry
  }

  public override setSelectConnectsComponentEntry(value: string): void{
    this.connectsComponentEntry = value
  }

  public override connectSelectedComponent((): (arg: void) => void {
    return $ => {
      if(this.getSelectConnectsComponentEntry()) {
        DemoRecManager.getDemoRecComponentSure(+this.getSelectConnectsComponentEntry()).then(
            demoRecComponent => {
              this.getConnection().addConnects2(demoRecComponent)
            }
        )
      }  
    }
  }

  public override addNewComponent(comp: DemoRecComponent) {
    this.getConnection().addConnects2(comp)
  }

  public override removeSelectedComponent(componentToRemove: DemoRecComponent): (arg: any) => void {
    return () => {
      this.getConnection().removeConnects2(componentToRemove)
    }
  }

  public override getNewCompGuardCondition() :boolean {
    return this.getSelectConnectsComponentEntry() === newOption.value
  }

}
