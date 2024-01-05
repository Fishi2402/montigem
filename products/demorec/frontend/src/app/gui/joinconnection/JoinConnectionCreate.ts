/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';
import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { JoinConnectionCreateComponent, config } from '../../../../build/frontendGen/build/appgui/gui/joinconnection/JoinConnectionCreateComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

import { Option } from '@umlp/input';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class JoinConnectionCreate extends JoinConnectionCreateComponent {

  componentOptions : Option[] = [newOption]
  joinConnectionTypeOptions : Option[] = []

  superComponentEntry : string = "";
  connectsComponentEntry : string = "";
  connectionTypeEntry : string = "";

  public override ngOnInit(){
    super.ngOnInit()

    this.joinConnectionTypeOptions = [];
    for (let val of Object.values(JoinConnectionType)) {
      let str = <string>val;
      this.joinConnectionTypeOptions.push({value: str, display: str});
    }

    DemoRecManager.getDemoRecComponentList().then(componentList => {

      const components = componentList;
      components.loadAll().then(() => {
        console.debug(components)
        this.componentOptions = [newOption]
        for (const component of components) {
          console.debug("Refill Join Connection Options")
          let value = component.getGemId() + "";
          this.componentOptions.push({value: value, display: value});
        }
        this.componentOptions = this.componentOptions.slice();
      });
    });
  }

  // Select Type of connection
  public override getSelectTypeOfConnectionOptions (): Array<Option> {
    return this.joinConnectionTypeOptions;
  }

  public override getSelectTypeOfConnectionEntry (): string {
    return this.connectionTypeEntry;
  }

  public override setSelectTypeOfConnectionEntry (value: string): void {
    this.connectionTypeEntry = value;
  }

  // Select Super component
  public override getSelectSuperComponentOptions (): Array<Option> {
    return this.componentOptions;
  }

  public override getSelectSuperComponentEntry (): string {
    return this.superComponentEntry;
  }

  public override setSelectSuperComponentEntry (value: string): void {
    this.superComponentEntry = value;
  }

  // Select Connected Components
  public override getSelectConnectsComponentOptions (): Array<Option>{
    return this.componentOptions;
  }

  public override getSelectConnectsComponentEntry (): string{
    return this.connectsComponentEntry;
  }

  public override setSelectConnectsComponentEntry (value: string): void{
    this.connectsComponentEntry = value;
  }

  public override addNewComponent(comp: DemoRecComponent) {
    console.debug("Add Component to Connection")
    console.debug(comp)
    this.getConnection().build()
    this.getConnection().connectsAdd(comp)
    //this.getConnection().addConnects2(comp)
    console.debug("Connects List after adding Component: ", this.getConnection().getConnects())
    this.dummyComponents.push("Hallo");
    setTimeout(() => console.debug("Connects List after adding Component: ", this.getConnection().getConnects()), 2000)
  }

  dummyComponents = []
  getDummyComponents() {
    return this.dummyComponents;
  }

}


