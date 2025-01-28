/* (c) https://github.com/MontiCore/monticore */


import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  QueryList,
  TemplateRef,
  ViewChild,
  ViewChildren,
  ViewContainerRef
} from "@angular/core";
import {
  CommonModule,
  KeyValuePipe,
  NgComponentOutlet,
  NgFor,
  NgIf,
  AsyncPipe
} from "@angular/common";

import "@angular/compiler";
import {InspectorService} from "wizard/InspectorService";
import {Observable} from "rxjs";
import {WIZARD_DATA} from "wizard/WizardData";
import {WizComponentOutlet} from "wizard/WizComponentOutlet";
import {WizardDropComp} from "wizard/WizardDropComp";


/**
 * This component wraps a GUI component with inner components to all its use and editing in the wizard
 */
@Component({
  standalone: true,
  imports: [NgComponentOutlet, WizComponentOutlet, KeyValuePipe, NgFor, NgIf, CommonModule, AsyncPipe, WizardDropComp],
  selector: 'wizard-container',
  styles: 'button {width: 2em; height: 2em; }',
  template: `
<ng-template #everything>
<!--    @If(showControls) {-->
<!--        <div style="border: 2px black solid; margin-left: 2px; margin-top: 2px;">-->
<!--    }-->

    <ng-template #lastElem *ngFor="let innerNames of innerNames | keyvalue: identity">
        <ng-container *ngIf="showControlsObsv|async">
            <wizard-drop-comp [index]=0 [listName]="innerNames.key" defaultContent="Drag &amp; drop a component onto me" (controlEvent)="emptyAddControl(innerNames.key, $event)"/>
        </ng-container>
    </ng-template>

    @if(containerType) {
        <!--   Wizard which delegates to its inner elements -->
        <ng-container *ngComponentOutlet="containerType; inputs: containerInputs;"/>
    } @else {
        <!--  The root wizard container  -->
        <ng-container *ngFor="let e of this.innerNames | keyvalue: identity">
          <ng-container *ngFor="let templ of containerInputs[e.key]" >
              <ng-container [ngTemplateOutlet]="templ"   />
          </ng-container>
        </ng-container>

    }


    <ng-container *ngFor="let i of templateContainers">
      <ng-template #dynTemplate let-listName="i.listName" let-index="i.index" > <!--A template (which will be passed as a component)-->
          <ng-container [wizComponentOutlet]="i.type" [wizComponentOutletInputs]="i.inputs" [wizComponentOutletControls]="{listName: i.listName, index: i.index, length: getCompsLength(i.listName), maxComponents: this.innerNames[i.listName], type: i.type}" (wizComponentOutletControlEvent)="onControlEvent($event)"  />  <!--and a delegated/ngComponentOutlet of the real component -->
        </ng-template>
    </ng-container>

<!--    </div>-->
</ng-template>
  `,
})
export class WizardContainer implements AfterViewInit, OnInit {

  // Note: We can not handle this input as an immutable object, as otherwise the parent component looses the reference to this
  @Input()
  templateContainers: { index: number, listName: string, type: any, inputs: any }[] = [];

  constructor(private viewContainerRef: ViewContainerRef, private inspectorService: InspectorService) {
    this.showControlsObsv = inspectorService.showControls.asObservable();
    this.showDropObsv = inspectorService.showDropZone.asObservable();
  }

  showControlsObsv: Observable<boolean>; // Observable weather to show the controls?
  showDropObsv: Observable<boolean>; // Observable weather to show the drop zones?

  // Render without DOM element
  @ViewChild('everything', {static: true}) mainTemplate;

  ngOnInit(): void {
    this.viewContainerRef.createEmbeddedView(this.mainTemplate);
  }


  // What class the actual component is
  @Input()
  containerType: { new(): any } | undefined;

  // The names of all inner children, such as components
  @Input()
  innerNames: { [name: string]: number } = {"components": -1}


  // a human readable name to describe this component
  @Input()
  humanReadableName: string = "unknown?";


  // The inputs (sort of) passed to the real component
  _containerInputs: { [key: string]: any } = {};
  @Input()
  set containerInputs(i: { [key: string]: any }) {
    if (this.dynTemplates) {
      console.log("Updating containerInputs of me", this.templateContainers)
      // In case dynTemplates are loaded -> Insert the dynamically created template references into the inputs
      let componentIndex = 0;
      for (let componentName in this.innerNames) {
        // Create a mapping stored index -> sorted position to render
        let indizes: number[] = [];
        let indiCount = 0;
        for (let e of this.templateContainers) {
          if (e.listName == componentName) {
            indizes.push(e.index);
            indiCount++;
          } else {
            indizes.push(-1);
          }
        }
        // const allowAdd = this.innerNames[componentName] == -1 || indiCount < this.innerNames[componentName];
        const allowAdd = indiCount == 0; // TODO: include in DropComp
        // We might add a final element (the add another element button)
        let dyn = Array(indiCount + (allowAdd ? 1 : 0)).fill(undefined);
        for (let i = 0; i < this.dynTemplates.length; i++) {
          // in case the element should be used for this input -> map to visible index and add to "components"
          if (indizes[i] != -1)
            dyn[indizes[i]] = this.dynTemplates.get(i);
        }
        if (allowAdd)
          dyn[dyn.length - 1] = this.lastElem.get(componentIndex);
        if (this.innerNames[componentName] == 1) {
          // In case only one inner gui component is allowed => do not use array
          // Note: The title of a GemCard will never work due do the allowAdd here
          i[componentName] = dyn[0];
        } else
          i[componentName] = dyn;
        componentIndex++;
      }
    }
    this._containerInputs = i
  }

  get containerInputs() {
    return this._containerInputs;
  }


  // The actual rendered components
  @ViewChildren(WizComponentOutlet, { /*static: false */}) ngComponentOutlet: QueryList<WizComponentOutlet>;
  // References to the templates for child/inner components
  @ViewChildren('dynTemplate') dynTemplates: QueryList<TemplateRef<any>>;
  // References to the templates containing the add buttons
  @ViewChildren('lastElem') lastElem: QueryList<TemplateRef<any>>;


  ngAfterViewInit(): void {
    // trigger an update
    this.containerInputs = {...this.containerInputs};
  }

  /**
   * Move the visible position of an inner element
   */
  move(dir: number, pos: number, componentName: string): void {
    let x = this.getComp(componentName, pos);
    let other = this.getComp(componentName, pos + dir);
    x.index += dir;
    other.index -= dir;
    this.updateComps();
  }

  /**
   * Remove an inner element
   */
  remove(pos: number, componentName: string): void {
    let components = this.containerInputs[componentName];
    let realObj = this.getComp(componentName, pos);
    let realIndex = this.templateContainers.indexOf(realObj);
    this.templateContainers.splice(realIndex, 1);
    // let newContainers = [...this.templateContainers.slice(0, realIndex), ...this.templateContainers.slice(realIndex + 1)];
    // TODO: decrease indices
    for (let c of this.templateContainers)
      if (c.index > pos && c.listName == componentName)
        c.index--;

    // this.templateContainers = newContainers;
    this.updateComps();
  }

  constructStuff(o: {
    type: string,
    params: { [key: string]: any },
    components: { [key: string]: any }
  }) {
    let inputs: any = {...o.params};
    let clazz: any = WIZARD_DATA[o.type].new[0];
    if (clazz === WizardContainer) {
      inputs = WIZARD_DATA[o.type].new[1]();
      for (let p in o.params)
        inputs.containerInputs[p] = o.params[p];
      let map = {};
      inputs.templateContainers = [];
      for (let c in o.components) {
        for (let x of o.components[c]) {
          const inner = this.constructStuff(x);
          const index = (map.hasOwnProperty(c) ? ++map[c] : map[c] = 0);
          let newInputs = inner.inputs;
          inputs.templateContainers.push(
              {
                listName: c, index: index,
                type: inner.type, // copy the class
                inputs: newInputs, // and invoke the inputs method (to create a copy),
              });
        }
      }
    }
    return {type: clazz, inputs: inputs};
  }

  addInner(componentName: string, o: {
    type: string,
    params: { [key: string]: any },
    components: { [key: string]: any }
  }) {
    const x = this.constructStuff(o);
    console.log(x);
    this.templateContainers.push({
      listName: componentName, index: this.getCompsLength(componentName),
      type: x.type,
      inputs: x.inputs,
    })
  }

  updateComps() {
    // Trigger an immediate update
    this.containerInputs = {...this.containerInputs};
    setTimeout(() => {
      // And a second one once template references are most likely resolved
      this.containerInputs = {...this.containerInputs};
    }, 10);
  }

  /**
   * How many elements does a specific inner component have?
   */
  getCompsLength(componentName: string) {
    let ret = 0;
    for (let i of this.templateContainers)
      if (i.listName == componentName) ret++;
    return ret;
  }


  getComp(componentName: string, index: number) {
    for (let i of this.templateContainers)
      if (i.listName == componentName && i.index == index) return i;
    return undefined;
  }

  /**
   * The inspect button was clicked on an inner component
   */
  inspect(pos: number, componentName: string): void {
    let index = 0;
    for (let e of this.templateContainers) {
      if (e.listName == componentName && e.index == pos) {
        if (e.type == WizardContainer) {
          // Delegate to the real component contained within the WizardContainer
          this.inspectorService.emitInspect({
            type: e.inputs.containerType,
            inputs: e.inputs.containerInputs
          });
          console.log("inspect wc", e, e.inputs.containerInputs)
        } else {
          this.inspectorService.emitInspect({type: e.type, inputs: e.inputs});
          console.log("inspect wc", e, e.inputs.containerInputs)
        }
        return;
      }

      index++;
    }
  }

  onControlEvent(e: any) {
    if (e.type === 'inspect') {
      this.inspect(e.index, e.listName);
      return;
    } else if (e.type === 'remove') {
      this.remove(e.index, e.listName);
    } else if (e.type === 'startDrag') {
      this.inspectorService.draggedObject = {
        container: this,
        index: e.index,
        listName: e.listName,
      }
      console.log("start move", e.index, e.listName, this);

    } else if (e.type === 'drop') {
      if (this.inspectorService.draggedObject.newType) {
        var new_wiz_data = WIZARD_DATA[this.inspectorService.draggedObject.newType];
        // New Object: Move objects behind new position
        for (let tc of this.templateContainers) {
          if (tc.index >= e.index)
            tc.index++;
        }
        // and insert new element
        this.templateContainers.push({
          listName: e.listName, index: e.index,
          type: new_wiz_data.new[0], // copy the class
          inputs: new_wiz_data.new[1](), // and invoke the inputs method (to create a copy)
        })
        this.updateComps()
        return;
      }
      let sourceCon = this.inspectorService.draggedObject.container as WizardContainer;
      let src = sourceCon.getComp(this.inspectorService.draggedObject.listName, this.inspectorService.draggedObject.index);

      console.log("move", src.index + "->" + (e.index), src.listName + "->" + e.listName, sourceCon == this)

      if (sourceCon == this && e.listName == src.listName) {
        console.log("moving within same component", this)
        // Moved within same
        let tgtIndex = e.index > src.index ? e.index - 1 : e.index;
        console.log(src.index, "->", tgtIndex)
        if (src.index == tgtIndex) {
          // No change - position is the same
          return;
        }
        if (tgtIndex < src.index) {
          // move to start -> increase other indices in between
          for (let ic of this.templateContainers)
            if (ic.listName == e.listName && ic.index >= tgtIndex && ic.index < src.index) {
              ic.index++;
            }
        } else if (tgtIndex > src.index) {
          // move to end -> decrease other indices in between
          for (let ic of this.templateContainers) {
            if (ic.listName == e.listName && ic.index <= tgtIndex && ic.index > src.index) {
              ic.index--;
            }
          }
        }

        // Finally, update the moved elements position to the target position
        src.index = tgtIndex;
        this.updateComps()
        return;
      }


      // remove from old
      sourceCon.remove(src.index, src.listName);
      // increase indices of elements after this
      // let thisTC = [...this.templateContainers];
      for (let tc of this.templateContainers)
        if (tc.index >= e.index && tc.listName == e.listName)
          tc.index++;

      // Update position of element
      src.index = e.index;
      src.listName = e.listName;

      // And insert into containers
      this.templateContainers.push(src)

      this.updateComps()

      this.inspectorService.draggedObject = undefined;
    } else
      console.error("Unknown event", e);
  }

  emptyAddControl(listName: string, event: any) {
    console.log("Control event on ", listName, event, this);
    this.onControlEvent(event)
  }

  // Used to not sort the keyvalue pipe by key
  identity(a: any, b: any) {
    return 0;
  }

  // Note: HostListeners do not work here

}
