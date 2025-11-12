package gui.measuringvalues;

import demorec.DemoRecManager;
import demorec.Dimension;
import demorec.MeasuringValue;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class MeasuringValueEdit extends MeasuringValueEditTOP {

  Map<MeasuringValue, String> dimensionEntries = new HashMap<>();



  @Override
  public Consumer<MeasuringValue> attachMeasuringValue() {
    return (val) -> {
      this.getStep().addMeasured(val);
    };
  }

  @Override
  public Consumer<Void> deleteMeasuringValue(MeasuringValue value) {
    return $ -> {
      this.getStep().removeMeasured(value);
      DemoRecManager.deleteMeasuringValue(value.getGemId());
    };
  }

  @Override
  public List<Option> getOptions(List<Dimension> units) {
    List<Option> opts = new ArrayList<>();

    for(Dimension unit : units) {
      opts.add(new Option(String.valueOf(unit.getGemId()), unit.getDisplay()));
    }
    return opts;
  }

  @Override
  public void setSelectDimUnitEntry(String val, MeasuringValue measuringValue) {
    this.dimensionEntries.put(measuringValue, val);
    Dimension d = DemoRecManager.getDimensionSure(Long.valueOf(val));
    measuringValue.setDimension(d);
  }

  @Override
  public String getSelectDimUnitEntry(MeasuringValue measuringValue) {
    if(this.dimensionEntries.isEmpty()) {
      this.dimensionEntries.put(measuringValue, String.valueOf(measuringValue.getGemId()));
    }
    return this.dimensionEntries.get(measuringValue);
  }
}
