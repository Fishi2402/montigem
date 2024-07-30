/* (c) https://github.com/MontiCore/monticore */

import {DemoRecManager} from '@src/demorec/DemoRecManager';

import {MeasuringValue} from '@src/demorec/MeasuringValue'

import {MeasuringValueCreateComponent, config} from '@src/gui/measuringvalues/MeasuringValueCreateComponent';

import {
  Component,
  Input,
  Output,
  OnInit,
  EventEmitter
} from '@angular/core';

import { Option } from '@umlp/input';

const newOption: Option = { value: "NEW", display: "NEW" };

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class MeasuringValueCreate extends MeasuringValueCreateComponent implements OnInit {

  public dimensionEntry: string = "";
  public dimensionOptions: Option[] = [];

  public building = false;

  @Output()
  public measuringValueEvent: EventEmitter<MeasuringValue> = new EventEmitter<MeasuringValue>();

  public override ngOnInit(): void {
    super.ngOnInit()

    DemoRecManager.getDimensionList().then(gemPromiseRes => {
      const dimensions = gemPromiseRes;
      dimensions.loadAll().then(() => {
        this.dimensionOptions = []
        for (const dimension of dimensions) {
          let value = dimension.getGemId() + "";
          this.dimensionOptions.push({value: value, display: dimension.getName()+" ("+dimension.getUnit()+")"});
        }
        this.dimensionOptions = this.dimensionOptions.slice();
      });
    });

  }

  // Select Dimensions
  public override getSelectDimensionUnitOptions(): Array<Option> {
    return this.dimensionOptions;
  }

  public override getSelectDimensionUnitEntry(): string {
    return this.dimensionEntry;
  }

  public override setSelectDimensionUnitEntry(value: string): void {
    /*if(ProcessStepType.DECONSTRCUTION.toString() == value) {
      this.typeEntry = ProcessStepType.DECONSTRCUTION
    }*/
    this.dimensionEntry = value;
  }

  public override createNewMeasuringValue(): (arg: void) => void {
    return $ => {
      const waitingFor = [];
      waitingFor.push(DemoRecManager.getDimensionSure(+this.getSelectDimensionUnitEntry())
          .then((dimension) => {
            this.getMeasuringValue().setDimension1(dimension)
          }))
      this.building = true;
      Promise.all(waitingFor).then(x => {

        if (this.getMeasuringValue().isBuildable()) {
          const measuringValue = this.getMeasuringValue().build()
          this.measuringValueEvent.emit(measuringValue);

          // Execute superNgOnit again i.e. get a new builder from the backend
          DemoRecManager.measuringValueBuilder().then(gemPromiseRes => {
            this.measuringValue = gemPromiseRes;
            this.building = false;
          })
        } else {
          this.building = false;
        }
      })
    }
  }
}
