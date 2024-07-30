/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { DemoRecComponent } from '@src/demorec/DemoRecComponent';
import { State } from '@src/demorec/State';
import { BatteryPack } from '@src/demorec/BatteryPack';

import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

import { JoinConnectionKindEditComponent, config } from '@src/gui/connectionkind/JoinConnectionKindEditComponent';

import {
  Component,
  OnInit
} from '@angular/core';

import {GemTextInputValidity, Option} from '@umlp/input';
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

export class JoinConnectionKindEdit extends JoinConnectionKindEditComponent implements OnInit {
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
          this.connectionKindRoutingId = +this._route.snapshot.paramMap.get("connectionRoutingId");
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
            console.debug("Refill Component Options for Join Connection Kinds")
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
    return this.getConnectionKind()?.getJoinConnectionType()?.toString()
  }

  public override setSelectTypeOfConnectionEntry (value: string): void {
    Object.keys(JoinConnectionType).filter((v) => isNaN(Number(v))).forEach((key, index) => {
      if (+value === index) {
        this.getConnectionKind().setJoinConnectionType(JoinConnectionType[key])
      }
    })
  }

  // Select Connected Components
/*  public getAddComponentDisabled(): boolean {
    return (this.getSelectConnectsComponentEntry() === newOption["value"])
  }*/

  public override getSelectConnectsComponentOptions(): Array<Option>{
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
    this.connectSelectedComponent()()
  }


  public connectSelectedComponent(): (arg: void) => void {
    return $ => {
      if(this.getSelectConnectsComponentEntry() === newOption.value) {
        return;
      }
      if(this.getSelectConnectsComponentEntry()) {
        DemoRecManager.getDemoRecComponentSure(+this.getSelectConnectsComponentEntry()).then(
            demoRecComponent => {
              this.getConnectionKind().addConnects2(demoRecComponent)
            }
        )
      }
    }
  }

  public override addNewComponent(): (comp: DemoRecComponent) => void {
    return (comp: DemoRecComponent) => {
      this.componentOptions.push({value: comp.getGemId()+"", display: comp.getName()})
      this.setSelectConnectsComponentEntry(comp.getGemId()+"")
    }
  }

  public override removeSelectedComponent(componentToRemove: DemoRecComponent): (arg: any) => void {
    return () => {
      this.getConnectionKind().removeConnects2(componentToRemove)
    }
  }

  public override getNewCompGuardCondition() :boolean {
    return this.getSelectConnectsComponentEntry() === newOption.value
  }

  public checkConnectionType() : GemTextInputValidity {
    if(this.getConnectionKind()) {
      if(this.getConnectionKind().getJoinConnectionType()) {
        return {status: "Success"}
      }
      return {status: "Error", message: "Please choose a connection type."}
    }
    return {status: "Warning", message: "Wait for page init."}
  }

  public checkChosenComponents() : GemTextInputValidity {
    if(this.getConnectionKind()) {
      if(this.getConnectionKind().getConnectsList().length >= 2) {
        return {status: "Success"}
      }
      return {status: "Warning", message: "Later on, you should specify at least 2 connected components."}
    }
    return {status: "Warning", message: "Wait for page init."}
  }

}
