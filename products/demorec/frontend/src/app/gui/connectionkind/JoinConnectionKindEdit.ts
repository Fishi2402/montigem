/* (c) https://github.com/MontiCore/monticore */

import { inject } from '@angular/core';
import { DemoRecManager } from 'demorec/DemoRecManager';

import { DemoRecComponent } from 'demorec/DemoRecComponent';
import { State } from 'demorec/State';
import { BatteryPack } from 'demorec/BatteryPack';

import { JoinConnectionType } from 'demorec/JoinConnectionType';

import { JoinConnectionKindEditComponent, config } from 'gui/connectionkind/JoinConnectionKindEditComponent';

import {
  Component,
  OnInit
} from '@angular/core';

import {GemTextInputValidity, Option} from '@umlp/input';
import {BatteryPackService} from "../../BatteryPackService";
import { NavigationEnd,ActivatedRoute, Router } from '@angular/router';

const newOption: Option = new Option("NEW");

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
  packService: BatteryPackService

  public constructor() {
    super();
    this.packService = inject(BatteryPackService);
  }

  public init(): void {
    this.packService.currentPack.subscribe(p => {
      this.currentPack = p
      this.filteredComponentOptions = [newOption].concat(
          this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.getValue())))
    })

    super.init();
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
      this.joinConnectionTypeOptions.push(new Option(key, JoinConnectionType[key]));
    })

    this.conditionOptions = []
    Object.keys(State).filter(s => !isNaN(+s)).forEach(key => {
      this.conditionOptions.push(new Option(key, State[key]));
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
            this.componentOptions.push(new Option(value, display));
          }
        }
        this.componentOptions = this.componentOptions.sort(
            (a,b) => {return a.getDisplay().localeCompare(b.getDisplay())}
        ).slice();
        this.filteredComponentOptions = [newOption].concat(
            this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.getValue())).sort(
                (a,b) => {return a.getDisplay().localeCompare(b.getDisplay())}
            ))
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
        this.componentOptions.filter(opt => this.getCurrentPackComponents().has(opt.getValue())).sort(
            (a,b) => {return a.getValue().localeCompare(b.getDisplay())}
        ))
    console.debug(filteredOptions)
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
      if(this.getSelectConnectsComponentEntry() === newOption.getValue()) {
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
      this.componentOptions.push(new Option(comp.getGemId()+"", comp.getName()))
      this.setSelectConnectsComponentEntry(comp.getGemId()+"")
    }
  }

  public override removeSelectedComponent(componentToRemove: DemoRecComponent): (arg: any) => void {
    return () => {
      this.getConnectionKind().removeConnects2(componentToRemove)
    }
  }

  public override getNewCompGuardCondition() :boolean {
    return this.getSelectConnectsComponentEntry() === newOption.getValue()
  }

  public checkConnectionType() : GemTextInputValidity {
    if(this.getConnectionKind()) {
      if(this.getConnectionKind().getJoinConnectionType()) {
        return new GemTextInputValidity("Success");
      }
      return new GemTextInputValidity("Error", "Please choose a connection type.");
    }
    return new GemTextInputValidity("Warning", "Wait for page init.");
  }

  public checkChosenComponents() : GemTextInputValidity {
    if(this.getConnectionKind()) {
      if(this.getConnectionKind().getConnectsList().length >= 2) {
        return new GemTextInputValidity("Success")
      }
      return new GemTextInputValidity("Warning", "Later on, you should specify at least 2 connected components.");
    }
    return new GemTextInputValidity("Warning", "Wait for page init.");
  }

}
