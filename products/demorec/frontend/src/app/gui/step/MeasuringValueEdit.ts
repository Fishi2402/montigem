/* (c) https://github.com/MontiCore/monticore */

import {DemoRecManager} from '@src/demorec/DemoRecManager'

import {MeasuringValue} from '@src/demorec/MeasuringValue'

import {MeasuringValueBuilder} from '@src/demorec/MeasuringValueBuilder'

import {MeasuringValueEditComponent, config} from '../../../../build/frontendGen/build/appgui/gui/step/MeasuringValueEditComponent';

import {
  AfterViewChecked,
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

export class MeasuringValueEdit extends MeasuringValueEditComponent implements AfterViewChecked, OnInit {

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

  public ngAfterViewChecked() {
    // Initialize dimensionOptions
    /*if (this.getStep()?.getMeasured().length !== 0 && this.dimensionOptions.length === 0) {
      this.initializeDimensionOptions()
    }

    if (this.dimensionOptions.length !== 0 && !this.dimensionUnitsWereDisplayed) {
      this.getStep().getMeasured().forEach((measuredValue) => {
        //this.setDisplayedMeasuringValueDimensionUnitEntry(measuredValue.getDimension().getGemId() + "", measuredValue)
        this.getDisplayedMeasuringValueDimensionUnitEntry(measuredValue)
      })
      this.dimensionUnitsWereDisplayed = true
    }*/
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

  public override deleteMeasuringValue(measuringValue: MeasuringValue): (measuringValue: MeasuringValue) => void {
    return () => this.getStep().removeMeasured2(measuringValue)

  }

  // Display and change the dimension unit of a concrete existing measuring value

  public override getDisplayedMeasuringValueDimensionUnitOptions(measuringValue: MeasuringValue): Array<Option> {
    return this.dimensionOptions
  }

  public override getDisplayedMeasuringValueDimensionUnitEntry (measuringValue: MeasuringValue): string {
    let displayValue = ""
    this.dimensionOptions.forEach((option) => {
      if (+option["value"] === measuringValue.getDimension().getGemId()) {
        console.error("This is the displayvalue:", option["value"])
        displayValue = option["value"]
      }
    })
    console.error("Messwert mit id "+measuringValue.getGemId()+" hat die Unit "+measuringValue.getDimension().getGemId()+" und es wurde folgende Option ausgesucht:"+ displayValue)
    //console.debug("A short message")
    return displayValue
  }

  public override setDisplayedMeasuringValueDimensionUnitEntry (value: string, measuringValue: MeasuringValue): void {
    DemoRecManager.getDimensionSure(+value).then(dimension => {
      measuringValue.setDimension1(dimension)
    })
  }

  /*public override getDisplayedMeasuringValueDimensionUnitEntry(): string {
    return this.dimensionEntry
  }

  public override setDisplayedMeasuringValueDimensionUnitEntry(value: string): void {
    this.dimensionEntry = value
  }*/

  public override attachMeasuringValue(val: MeasuringValue) {
    console.error("Number of measuringValues before:", this.getStep().getMeasured().length)
    this.getStep().addMeasured2(val);
    console.error("Number of measuringValues after:", this.getStep().getMeasured().length)
  }

  public override backToStepEditing(any): void {
    this.router.navigate(["gui/step/ProcessStepEdit/"+this.getP().getGemId()+"/"+this.getStep().getGemId()])
  }

}
