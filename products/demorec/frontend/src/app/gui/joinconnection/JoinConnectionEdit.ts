/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { NotificationScope } from '@umlp/common';

import { DemoRecComponent } from '@src/demorec/DemoRecComponent';

import { JoinConnectionType } from '@src/demorec/JoinConnectionType';

import { JoinConnectionEditComponent, config } from '../../../../build/frontendGen/build/appgui/gui/joinconnection/JoinConnectionEditComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  OnInit,
  EventEmitter,
  SimpleChanges,
  TemplateRef,
} from '@angular/core';

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

export class JoinConnectionEdit extends JoinConnectionEditComponent implements OnInit {
  joinConnectionTypeOptions : Option[] = []
  componentOptions : Option[] = []
  superComponentsOptions: Option[] =  []
  superComponentEntry : string = ""
  connectsComponentEntry : string = ""

  dummyComponents : DemoRecComponent[] = []
  dummyComponentNames : string[] = []


  public override ngOnInit(){
    super.ngOnInit()

    // Initialize joinConnectionTypeOptions
    this.joinConnectionTypeOptions = [];
    Object.keys(JoinConnectionType).filter(s => !isNaN(+s)).forEach(key => {
      this.joinConnectionTypeOptions.push({value: key, display: JoinConnectionType[key]});
    })

    // Initialize componentOptions and superComponentsOptions
    DemoRecManager.getDemoRecComponentList().then(componentList => {
      const components = componentList;
      components.loadAll().then(() => {
        console.debug(components)
        this.superComponentsOptions = []
        this.componentOptions = [newOption]
        for (const component of components) {
          console.debug("Refill Join Connection Options")
          let value = component.getGemId() + "";
          this.componentOptions.push({value: value, display: component.getName()+" "+value});
          this.superComponentsOptions.push({value: value, display: component.getName()+" "+value});
        }
        this.componentOptions = this.componentOptions.slice();
        this.superComponentsOptions = this.superComponentsOptions.slice();
      });
    });
  }

  public override getJoinConnectionEditGemCardTitle(): string {
    return "Edit Join Connection of Step " + this.getStep()?.getIndex()
  }

  // Select Type of connection
  public override getSelectTypeOfConnectionOptions (): Array<Option> {
    return this.joinConnectionTypeOptions
  }

  public override getSelectTypeOfConnectionEntry (): string {
    return this.getConnection()?.getJoinConnectionType().toString()
  }

  public override setSelectTypeOfConnectionEntry (value: string): void {
    Object.keys(JoinConnectionType).filter((v) => isNaN(Number(v))).forEach((key, index) => {
      if (+value === index) {
        this.getConnection().setJoinConnectionType(JoinConnectionType[key])
      }
    })
  }

  // Select Super Component

  public override getSelectSuperComponentOptions(): Array<Option> {
    return this.superComponentsOptions
  }

  public override getSelectSuperComponentEntry(): string {
    if (this.superComponentEntry) {
      return this.superComponentEntry
    }
    else {
      if (this.getConnection()) {
        DemoRecManager.getDemoRecComponentList().then( componentList => {
          componentList.loadAll().then(() => {
            const components = componentList.toArray()
            components.forEach((component) => {
              if (component.containsComposedOf(this.getConnection())) {
                this.setSelectSuperComponentEntry(component.getGemId()+"")
              }
            })
          })
        })
      }
      return this.superComponentEntry
    }
  }

  public override setSelectSuperComponentEntry(value: string): void {
    this.superComponentEntry = value
    DemoRecManager.getDemoRecComponentSure(+value).then(comp => {
      comp.addComposedOf2(this.getConnection())
    })
  }


  // Select Connected Components

  public override getAddComponentDisabled(): boolean {
    return (this.getSelectConnectsComponentEntry() === newOption["value"])
  }

  public override getDummyComponentNames(): Array<string> {
    if (this.dummyComponents.length !== 0) {
      return this.dummyComponentNames
    }
    else {
      if(this.getConnection() && this.dummyComponents.length === 0) {
        this.getConnection().getConnects().forEach((component) => {
          this.syncDummyComponentsWithBuilderObject(component)
        })
      }
      return this.dummyComponentNames
    }
  }

  public override getSelectConnectsComponentOptions(): Array<Option>{
    return this.componentOptions
  }

  public override getSelectConnectsComponentEntry(): string{
    return this.connectsComponentEntry
  }

  public override setSelectConnectsComponentEntry(value: string): void{
    this.connectsComponentEntry = value
  }

  public override connectSelectedComponent(event: any) {
    if(this.getSelectConnectsComponentEntry()) {
      DemoRecManager.getDemoRecComponentSure(+this.getSelectConnectsComponentEntry()).then(
          demoRecComponent => {
            this.getConnection().addConnects2(demoRecComponent)
            this.syncDummyComponentsWithBuilderObject(demoRecComponent)
          }
      )
    }
  }

  public override addNewComponent(comp: DemoRecComponent) {
    this.getConnection().build()
    this.getConnection().addConnects2(comp)
    this.syncDummyComponentsWithBuilderObject(comp)
  }

  addComponentName(comp: DemoRecComponent) {
    const compName = comp.getName()+":"+comp.getGemId()
    this.dummyComponentNames.push(compName);
  }

  syncDummyComponentsWithBuilderObject(comp: DemoRecComponent) {
    this.addComponentName(comp)
    this.dummyComponents.push(comp)
  }

  public override removeSelectedComponent(dummyComponentName: string): (arg: any) => void {
    console.debug("removeSelectedComponent: ", dummyComponentName)
    return event => {
      const nameSplitByDoubleColon = dummyComponentName.split(":")
      const len = nameSplitByDoubleColon.length
      if(len > 0) {
        const gemIdOfComponent = nameSplitByDoubleColon[len-1]
        const indexInDummyComps = this.dummyComponentNames.indexOf(dummyComponentName)
        console.debug("Removing Component from Connects by Id ", gemIdOfComponent)
        console.debug("indexInDummyComps:",indexInDummyComps)
        console.debug("this.dummyComponents:", this.dummyComponents)
        console.debug(this.getConnection().removeConnects(this.dummyComponents[indexInDummyComps],
            NotificationScope.ALL))
        DemoRecManager.getDemoRecComponentSure(+gemIdOfComponent).then( c => {
          console.log("Removing ",c)
          this.getConnection().removeConnects2(c)
        }

        )
        console.debug(this.getConnection().getConnects().map(c => c.getGemId()))
        this.dummyComponentNames.splice(indexInDummyComps, 1)
        this.dummyComponents.splice(indexInDummyComps, 1)
      }
    }
  }

  public override getNewCompGuardCondition() :boolean {
    return this.getSelectConnectsComponentEntry() === newOption.value
  }

  public override backToStepEditing(any): void {
    this.router.navigate(["gui/step/ProcessStepEdit/"+this.getP().getGemId()+"/"+this.getStep().getGemId()])
  }
}
