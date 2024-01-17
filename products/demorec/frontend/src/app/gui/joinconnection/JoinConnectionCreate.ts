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
    Object.keys(JoinConnectionType).filter(s => !isNaN(+s)).forEach(key => {
      this.joinConnectionTypeOptions.push({value: key, display: JoinConnectionType[key]});
    })

    DemoRecManager.getDemoRecComponentList().then(componentList => {

      const components = componentList;
      components.loadAll().then(() => {
        console.debug(components)
        this.componentOptions = [newOption]
        for (const component of components) {
          console.debug("Refill Join Connection Options")
          let value = component.getGemId() + "";
          this.componentOptions.push({value: value, display: component.getDescription()+" "+value});
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

  public override getBuildConnectionDisabled (): boolean {
    return this.getConnection().isBuildable();
  }

  public override createNewConnection (event: any): void {
    //
    if (JoinConnectionType.SCREW.toString() === this.getSelectTypeOfConnectionEntry()) {
      this.getConnection().joinConnectionType(JoinConnectionType.SCREW)
    }
    if (JoinConnectionType.GLUE.toString() === this.getSelectTypeOfConnectionEntry()) {
      this.getConnection().joinConnectionType(JoinConnectionType.GLUE)
    }
    if (JoinConnectionType.WELDING.toString() === this.getSelectTypeOfConnectionEntry()) {
      this.getConnection().joinConnectionType(JoinConnectionType.WELDING)
    }
    if (JoinConnectionType.PLUG.toString() === this.getSelectTypeOfConnectionEntry()) {
      this.getConnection().joinConnectionType(JoinConnectionType.PLUG)
    }
    if (JoinConnectionType.ELECTRICAL.toString() === this.getSelectTypeOfConnectionEntry()) {
      this.getConnection().joinConnectionType(JoinConnectionType.ELECTRICAL)
    }
    if (JoinConnectionType.UNDEFINED.toString() === this.getSelectTypeOfConnectionEntry()) {
      this.getConnection().joinConnectionType(JoinConnectionType.UNDEFINED)
    }

    if(this.getConnection().isBuildable()) {
      console.debug("New Connection is buildable")
      const joinConnection = this.getConnection().build()
      if(this.getConnection().isBuildFinished()) {
        DemoRecManager.getDemoRecComponentSure(+this.superComponentEntry).then(comp => {
          comp.addComposedOf2(joinConnection)
        })
      }
    } else {
      console.debug("New Connection is not buildable")
      this.getConnection().build()
    }

  }

  public override addNewComponent(comp: DemoRecComponent) {
    /*console.debug("Add Component to Connection")
    console.debug(comp)*/
    this.getConnection().build()
    this.getConnection().connectsAdd(comp)
    //this.getConnection().addConnects2(comp)
    /*console.debug("Connects List after adding Component: ", this.getConnection().getConnects())*/
    this.syncDummyComponentsWithBuilderObject(comp)
    setTimeout(() => console.debug("Connects List after adding Component: ", this.getConnection().getConnects()), 200)
  }

  dummyComponents = []
  getDummyComponents() {
    return this.dummyComponents;
  }

  syncDummyComponentsWithBuilderObject(comp: DemoRecComponent) {
    this.dummyComponents.push("Added Component:"+comp.getDescription()+"_"+comp.getGemId());
  }

  public override connectSelectedComponent(event: any) {
    if(this.getSelectConnectsComponentEntry()) {
      DemoRecManager.getDemoRecComponentSure(+this.getSelectConnectsComponentEntry()).then(
          demoRecComponent => {
            this.getConnection().connectsAdd(demoRecComponent)
            this.syncDummyComponentsWithBuilderObject(demoRecComponent)
          }
      )
    }
  }

}


