/**
 * @license
 * Copyright Google LLC All Rights Reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be
 * found in the LICENSE file at https://angular.io/license
 *
 * Modified to allow
 */

import {
  ComponentRef,
  createNgModule,
  Directive,
  DoCheck,
  Injector,
  Input, Output, EventEmitter, OnInit,
  NgModuleFactory,
  NgModuleRef,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  Type,
  ViewContainerRef,
} from '@angular/core';
import {WizardControls} from "wizard/WizardControls";
import {WizardDropComp} from "wizard/WizardDropComp";
import {InspectorService} from "wizard/InspectorService";
import {WizardContainer} from "wizard/WizardContainer";

/**
 *
 * Modification of NGComponentOutlet with control elements
 *
 * Instantiates a {@link Component} type and inserts its Host View into the current View.
 * `NgComponentOutlet` provides a declarative approach for dynamic component creation.
 *
 * `NgComponentOutlet` requires a component type, if a falsy value is set the view will clear and
 * any existing component will be destroyed.
 *
 * @usageNotes
 *
 * ### Fine tune control
 *
 * You can control the component creation process by using the following optional attributes:
 *
 * * `ngComponentOutletInputs`: Optional component inputs object, which will be bind to the
 * component.
 *
 * * `ngComponentOutletInjector`: Optional custom {@link Injector} that will be used as parent for
 * the Component. Defaults to the injector of the current view container.
 *
 * * `ngComponentOutletContent`: Optional list of projectable nodes to insert into the content
 * section of the component, if it exists.
 *
 * * `ngComponentOutletNgModule`: Optional NgModule class reference to allow loading another
 * module dynamically, then loading a component from that module.
 *
 * * `ngComponentOutletNgModuleFactory`: Deprecated config option that allows providing optional
 * NgModule factory to allow loading another module dynamically, then loading a component from that
 * module. Use `ngComponentOutletNgModule` instead.
 *
 * ### Syntax
 *
 * Simple
 * ```
 * <ng-container *ngComponentOutlet="componentTypeExpression"></ng-container>
 * ```
 *
 * With inputs
 * ```
 * <ng-container *ngComponentOutlet="componentTypeExpression;
 *                                   inputs: inputsExpression;">
 * </ng-container>
 * ```
 *
 * Customized injector/content
 * ```
 * <ng-container *ngComponentOutlet="componentTypeExpression;
 *                                   injector: injectorExpression;
 *                                   content: contentNodesExpression;">
 * </ng-container>
 * ```
 *
 * Customized NgModule reference
 * ```
 * <ng-container *ngComponentOutlet="componentTypeExpression;
 *                                   ngModule: ngModuleClass;">
 * </ng-container>
 * ```
 *
 * ### A simple example
 *
 * {@example common/ngComponentOutlet/ts/module.ts region='SimpleExample'}
 *
 * A more complete example with additional options:
 *
 * {@example common/ngComponentOutlet/ts/module.ts region='CompleteExample'}
 *
 * @publicApi
 * @ngModule CommonModule
 */
@Directive({
  selector: '[wizComponentOutlet]',
  standalone: true,
})
export class WizComponentOutlet implements OnChanges, DoCheck, OnDestroy, OnInit {
  @Input() wizComponentOutlet: Type<any> | null = null;

  @Input() wizComponentOutletInputs?: Record<string, unknown>;
  // Custom wizard control elements
  @Input() wizComponentOutletControls?: Record<string, unknown>;
  @Input() wizComponentOutletInjector?: Injector;
  @Input() wizComponentOutletContent?: any[][];

  @Input() wizComponentOutletNgModule?: Type<any>;
  /**
   * @deprecated This input is deprecated, use `wizComponentOutletNgModule` instead.
   */
  @Input() wizComponentOutletNgModuleFactory?: NgModuleFactory<any>;

  // Notify the parent about control events
  @Output() wizComponentOutletControlEvent: EventEmitter<any> = new EventEmitter();


  private _componentRef: ComponentRef<any> | undefined;
  private _moduleRef: NgModuleRef<any> | undefined;

  // Control ComponentRefs
  private _controlComponentRef: ComponentRef<any> | undefined;
  private _firstDropComponentRef: ComponentRef<any> | undefined;
  private _dropComponentRef: ComponentRef<any> | undefined;

  /**
   * A helper data structure that allows us to track inputs that were part of the
   * wizComponentOutletInputs expression. Tracking inputs is necessary for proper removal of ones
   * that are no longer referenced.
   */
  private _inputsUsed = new Map<string, boolean>();

  private _showControls = false;
  private _showDrop = false;

  constructor(private _viewContainerRef: ViewContainerRef, private _inspectorService: InspectorService) {
    _inspectorService.showControls.subscribe(v => {
      this._showControls = v;
      this.updateControls();
    });
    _inspectorService.showDropZone.subscribe(v => {
      this._showDrop = v;
      this.updateControls();
    });
  }

  _timer: any;
  ngOnInit() {
    // Re-position this element every so often (maybe find a better way to do this)
    this._timer = setInterval(() => this.updateControlPos(), 500);
  }

  private _needToReCreateNgModuleInstance(changes: SimpleChanges): boolean {
    // Note: square brackets property accessor is safe for Closure compiler optimizations (the
    // `changes` argument of the `ngOnChanges` lifecycle hook retains the names of the fields that
    // were changed).
    return (
        changes['wizComponentOutletNgModule'] !== undefined ||
        changes['wizComponentOutletNgModuleFactory'] !== undefined
    );
  }

  private _needToReCreateComponentInstance(changes: SimpleChanges): boolean {
    // Note: square brackets property accessor is safe for Closure compiler optimizations (the
    // `changes` argument of the `ngOnChanges` lifecycle hook retains the names of the fields that
    // were changed).
    return (
        changes['wizComponentOutlet'] !== undefined ||
        changes['wizComponentOutletContent'] !== undefined ||
        changes['wizComponentOutletInjector'] !== undefined ||
        // changes['wizComponentOutletControls'] !== undefined ||
        this._needToReCreateNgModuleInstance(changes)
    );
  }

  /** @nodoc */
  ngOnChanges(changes: SimpleChanges) {
    // Method as in the original
    if (this._needToReCreateComponentInstance(changes)) {
      this._viewContainerRef.clear();
      this._inputsUsed.clear();
      this._componentRef = undefined;
      this._controlComponentRef = undefined;
      this._dropComponentRef = undefined;

      if (this.wizComponentOutlet) {
        const injector = this.wizComponentOutletInjector || this._viewContainerRef.parentInjector;

        if (changes != undefined && this._needToReCreateNgModuleInstance(changes)) {
          this._moduleRef?.destroy();

          if (this.wizComponentOutletNgModule) {
            this._moduleRef = createNgModule(
                this.wizComponentOutletNgModule,
                getParentInjector(injector),
            );
          } else if (this.wizComponentOutletNgModuleFactory) {
            this._moduleRef = this.wizComponentOutletNgModuleFactory.create(
                getParentInjector(injector),
            );
          } else {
            this._moduleRef = undefined;
          }
        }


        this._componentRef = this._viewContainerRef.createComponent(this.wizComponentOutlet, {
          injector,
          ngModuleRef: this._moduleRef,
          projectableNodes: this.wizComponentOutletContent,
        });

        // Except the call of this method
        this.updateControls()
      }
    }
  }

  updateControls() {
    // Add/Remove control elements
    if (this._showControls && this.wizComponentOutletControls) {
      const injector = this.wizComponentOutletInjector || this._viewContainerRef.parentInjector;

      // Only show a dropZone if there is space for more elements
      let showDropWithLength = this._showDrop
          && (this.wizComponentOutletControls['maxComponents'] == -1 || this.wizComponentOutletControls['maxComponents'] > this.wizComponentOutletControls['length'])
      console.log(this.wizComponentOutletControls['maxComponents'], this.wizComponentOutletControls['length'])

      if (this._firstDropComponentRef == undefined && showDropWithLength && this.wizComponentOutletControls['index'] == 0) {
        // Insert drop zone below first element
        this._firstDropComponentRef = this._viewContainerRef.createComponent(WizardDropComp, {
          index: 0,
          injector,
          ngModuleRef: this._moduleRef,
          projectableNodes: this.wizComponentOutletContent,
        });
        this._firstDropComponentRef.instance.index = 0;
        this._firstDropComponentRef.instance.listName = this.wizComponentOutletControls['listName'];
        // pass elements along
        this._firstDropComponentRef.instance.controlEvent.subscribe(v => {
          this.wizComponentOutletControlEvent.next(v)
        })
      }else if (!showDropWithLength && this._firstDropComponentRef) {
        // remove first dropzone otherwise
        this._firstDropComponentRef.destroy();
        this._firstDropComponentRef = undefined;
      }
      if (this._controlComponentRef == undefined && this.wizComponentOutletControls) {
        if (this.wizComponentOutletControls.hasOwnProperty('type')) {
          this._controlComponentRef = this._viewContainerRef.createComponent(WizardControls, {
            injector,
            ngModuleRef: this._moduleRef,
            projectableNodes: this.wizComponentOutletContent,
          });
          this._controlComponentRef.instance.controlEvent.subscribe(v => {
            this.wizComponentOutletControlEvent.next(v)
          })
        }
        this._controlComponentRef.instance.index = this.wizComponentOutletControls['index'];
        this._controlComponentRef.instance.length = this.wizComponentOutletControls['length'];
        this._controlComponentRef.instance.listName = this.wizComponentOutletControls['listName'];
      }
      if (this._dropComponentRef == undefined && showDropWithLength) {
        this._dropComponentRef = this._viewContainerRef.createComponent(WizardDropComp, {
          injector,
          ngModuleRef: this._moduleRef,
          projectableNodes: this.wizComponentOutletContent,
        });
        this._dropComponentRef.instance.index = (this.wizComponentOutletControls['index'] as number)+ 1;
        this._dropComponentRef.instance.listName = this.wizComponentOutletControls['listName'];
        this._dropComponentRef.instance.controlEvent.subscribe(v => {
          this.wizComponentOutletControlEvent.next(v)
        })
      }else if (!showDropWithLength && this._dropComponentRef) {
        this._dropComponentRef.destroy();
        this._dropComponentRef = undefined;
      }


    } else {
      if (this._controlComponentRef) {
        this._controlComponentRef.destroy();
        this._controlComponentRef = undefined;
      }
      if (this._firstDropComponentRef) {
        this._firstDropComponentRef.destroy();
        this._firstDropComponentRef = undefined;
      }
      if (this._dropComponentRef) {
        this._dropComponentRef.destroy();
        this._dropComponentRef = undefined;
      }
      // this._controlComponentRef.destroy()
    }
  }

  /** @nodoc */
  ngDoCheck() {
    if (this._componentRef) {
      if (this.wizComponentOutletInputs) {
        for (const inputName of Object.keys(this.wizComponentOutletInputs)) {
          this._inputsUsed.set(inputName, true);
        }
      }

      this._applyInputStateDiff(this._componentRef);
      // TODO _controlComponentRef?
    }
  }

  /** @nodoc */
  ngOnDestroy() {
    this._moduleRef?.destroy();
    window.clearInterval(this._timer);
  }

  private _applyInputStateDiff(componentRef: ComponentRef<unknown>) {
    for (const [inputName, touched] of this._inputsUsed) {
      if (!touched) {
        // The input that was previously active no longer exists and needs to be set to undefined.
        componentRef.setInput(inputName, undefined);
        this._inputsUsed.delete(inputName);
      } else {
        // Since touched is true, it can be asserted that the inputs object is not empty.
        componentRef.setInput(inputName, this.wizComponentOutletInputs![inputName]);
        this._inputsUsed.set(inputName, false);
      }
    }
  }

  getOffset(el) {
    var _x = 0;
    var _y = 0;
    var _width = 0;
    while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
      _x += el.offsetLeft - el.scrollLeft;
      _y += el.offsetTop - el.scrollTop;
      el = el.offsetParent;
    }
    // _width = el.offsetParent.getBoundingClientRect().width;
    return {top: _y, left: _x, width: _width};
  }


  updateControlPos() {
    if (this._controlComponentRef && this._componentRef) {
      let offset = this.getOffset(this._componentRef.location.nativeElement);
      let elem = this._componentRef.location.nativeElement;
      if (this._componentRef.instance instanceof WizardContainer) {
        elem = elem.nextSibling;
        while (!elem.getBoundingClientRect)
        elem = elem.nextSibling;
      }
      let bb = elem.getBoundingClientRect();
      let selfBB = this._controlComponentRef.location.nativeElement.getBoundingClientRect();
      if (selfBB.width + 50 > bb.width) { // element is too big?
        this._controlComponentRef.location.nativeElement.style.left = (offset.left + bb.width) + 'px';
        this._controlComponentRef.location.nativeElement.style.top = offset.top + 'px';
      }else {
        this._controlComponentRef.location.nativeElement.style.left = (offset.left + bb.width - selfBB.width - 5) + 'px';
        this._controlComponentRef.location.nativeElement.style.top = (offset.top - 0) + 'px';
      }
      this._controlComponentRef.location.nativeElement.style.position = 'absolute';
      this._controlComponentRef.location.nativeElement.style.backgroundColor = 'white';
    }
  }
}

// Helper function that returns an Injector instance of a parent NgModule.
function getParentInjector(injector: Injector): Injector {
  const parentNgModule = injector.get(NgModuleRef);
  return parentNgModule.injector;
}
