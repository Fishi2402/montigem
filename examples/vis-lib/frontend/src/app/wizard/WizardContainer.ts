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
import {CommonModule, KeyValuePipe, NgComponentOutlet, NgFor, NgIf, AsyncPipe} from "@angular/common";

import "@angular/compiler";
import {InspectorService} from "@src/wizard/InspectorService";
import {Observable} from "rxjs";


/**
 * This component wraps a GUI component with inner components to all its use and editing in the wizard
 */
@Component({
  standalone: true,
  imports: [NgComponentOutlet, KeyValuePipe, NgFor, NgIf, CommonModule, AsyncPipe],
  selector: 'wizard-container',
  styles: 'button {width: 2em; height: 2em; }',
  template: `
<ng-template #everything>
<!--    @If(showControls) {-->
<!--        <div style="border: 2px black solid; margin-left: 2px; margin-top: 2px;">-->
<!--    }-->

    <ng-template #lastElem *ngFor="let innerNames of innerNames | keyvalue: identity">
        <ng-container *ngIf="showControlsObsv|async">
                  <button (click)="add(innerNames.key)" title="Add inner gui {{innerNames.key}} to {{humanReadableName}}">+</button>
        </ng-container>
    </ng-template>

    @if(containerType) {
        <ng-container *ngComponentOutlet="containerType; inputs: containerInputs" />
    } @else {
        <ng-container *ngFor="let e of this.innerNames | keyvalue: identity">
          <ng-container *ngFor="let templ of containerInputs[e.key]" >
              <ng-container [ngTemplateOutlet]="templ" />
          </ng-container>
        </ng-container>

    }


    <ng-container *ngFor="let i of templateContainers">
      <ng-template #dynTemplate let-listName="i.listName" let-index="i.index"> <!--A template (which will be passed as a component)-->
              <ng-container *ngComponentOutlet="i.type; inputs: i.inputs" />  <!--and a delegated/ngComponentOutlet of the real component -->
                <!--    Followed by some controls -->
                <div *ngIf="showControlsObsv|async">
                  <button (click)="move(-1, i.index, i.listName)"  *ngIf="i.index>0 && this.innerNames[i.listName] != -1"  title="Move up">&uarr;</button>
                  <button (click)="move(1, i.index, i.listName)"  *ngIf="this.innerNames[i.listName] != -1 && i.index+1<getCompsLength(i.listName)"   title="Move down">&darr;</button>
                  <button (click)="inspect(i.index, i.listName)" title="Inspect">[i]</button>
                  <button (click)="remove(i.index, i.listName)" title="Remove">X</button>
                </div>
        </ng-template>
    </ng-container>
</ng-template>
  `,
})
export class WizardContainer implements AfterViewInit, OnInit {

  templateContainers: { index: number, listName: string, type: any, inputs: any }[] = [  ];

  constructor(private viewContainerRef: ViewContainerRef, private inspectorService: InspectorService) {
    this.showControlsObsv = inspectorService.showControls.asObservable();
  }

  showControlsObsv: Observable<boolean>; // Observable weather to show the controls?

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
  innerNames: {[name: string]: number} = {"components": -1}


  // a human readable name to describe this component
  @Input()
  humanReadableName: string = "unknown?";


  // The inputs (sort of) passed to the real component
  _containerInputs: { [key: string]: any } = {};
  @Input()
  set containerInputs(i: { [key: string]: any }) {
    if (this.dynTemplates) {
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
        const allowAdd = this.innerNames[componentName] == -1 || indiCount < this.innerNames[componentName];
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
  @ViewChildren(NgComponentOutlet, { /*static: false */}) ngComponentOutlet: QueryList<NgComponentOutlet>;
  // References to the templates for child/inner components
  @ViewChildren('dynTemplate') dynTemplates: QueryList<TemplateRef<any>>;
  // @ViewChildren('dynContainer') dynContainers: QueryList<any>;
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
    this.templateContainers = [...this.templateContainers.slice(0, realIndex), ...this.templateContainers.slice(realIndex + 1)];
    this.updateComps();
  }

  /**
   * Requests an element to be added at the final position
   */
  add(componentName: string): void {
    // Send the request to the main component (@Output does not really work with dynamic components)
    this.inspectorService.requestNewElem((clazz, inputs) => {
      if (clazz) {
        this.templateContainers.push({
          listName: componentName, index: this.getCompsLength(componentName),
          type: clazz, inputs: inputs
        })
        this.updateComps()
      }
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
          this.inspectorService.emitInspect({type: e.inputs.containerType, inputs: e.inputs.containerInputs})
        } else {
          this.inspectorService.emitInspect({type: e.type, inputs: e.inputs})
        }
        return;
      }

      index++;
    }
  }

  // Used to not sort the keyvalue pipe by key
  identity(a:any,b:any){return 0;}

  // Note: HostListeners do not work here

}
