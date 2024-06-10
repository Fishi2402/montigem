/* (c) https://github.com/MontiCore/monticore */

import {DemoRecManager} from '@src/demorec/DemoRecManager'

import {MeasuringValue} from '@src/demorec/MeasuringValue'

import {MeasuringValueEditComponent, config} from '../../../../build/frontendGen/build/appgui/gui/step/MeasuringValueEditComponent';

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

export class MeasuringValueEdit extends MeasuringValueEditComponent implements OnInit {

  public dimensionEntry: string = ""
  public dimensionOptions: Option[] = []
  public dimensionOptionsForNewMeasuringValue: Option[] = []
  public dimensionUnitsWereDisplayed: boolean = false

  public measuringValueNumber: string = ""

  public building = false

  public override ngOnInit(): void {
    super.ngOnInit()

    // Initialize dimensionOptions
    this.initializeDimensionOptions()

    // Initialize dimensionOptions for new measuring value
    DemoRecManager.getDimensionList().then(gemPromiseRes => {
      const dimensions = gemPromiseRes;
      dimensions.loadAll().then(() => {
        this.dimensionOptionsForNewMeasuringValue = []
        for (const dimension of dimensions) {
          let value = dimension.getGemId() + "";
          this.dimensionOptionsForNewMeasuringValue.push({value: value, display: dimension.getName()+" ("+dimension.getUnit()+")"});
        }
        this.dimensionOptionsForNewMeasuringValue = this.dimensionOptionsForNewMeasuringValue.slice();
      });
    });
  }

  public initializeDimensionOptions(): void {
    DemoRecManager.getDimensionList().then(gemPromiseRes => {
      const dimensions = gemPromiseRes;
      dimensions.loadAll().then(() => {
        console.debug(dimensions)
        this.dimensionOptions = []
        for (const dimension of dimensions) {
          let value = dimension.getGemId() + "";
          this.dimensionOptions.push({value: value, display: dimension.getName()});
        }
        this.dimensionOptions = this.dimensionOptions.slice();
      });
    });
  }

  public override deleteMeasuringValue(measuringValue: MeasuringValue): (arg: void) => void {
    return $ => this.getStep().removeMeasured2(measuringValue)
  }

  // Display and change the dimension unit of a concrete existing measuring value

  public override getDisplayedMeasuringValueDimensionUnitOptions(measuringValue: MeasuringValue): Array<Option> {
    return this.dimensionOptions
  }

  public override getDisplayedMeasuringValueDimensionUnitEntry (measuringValue: MeasuringValue): string {
    let displayValue = ""
    this.dimensionOptions.forEach((option) => {
      if (+option["value"] === measuringValue.getDimension().getGemId()) {
        displayValue = option["value"]
      }
    })
    return displayValue
  }

  public override setDisplayedMeasuringValueDimensionUnitEntry (value: string, measuringValue: MeasuringValue): void {
    DemoRecManager.getDimensionSure(+value).then(dimension => {
      measuringValue.setDimension1(dimension)
    })
  }

  public override attachMeasuringValue(): (arg: MeasuringValue) => void {
    return (val: MeasuringValue) => this.getStep().addMeasured2(val)
  }

}
