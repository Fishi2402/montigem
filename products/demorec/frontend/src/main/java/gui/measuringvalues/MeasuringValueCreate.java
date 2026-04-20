package gui.measuringvalues;

import demorec.DemoRecManager;
import demorec.Dimension;
import demorec.MeasuringValue;
import gui.editprocessmodels.graphicalview.wizard.DefineProcessModel;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class MeasuringValueCreate extends MeasuringValueCreateTOP {

  @Override
  public List<Option> getOpts(List<Dimension> dimensions) {
    List<Option> opts = new ArrayList<>();
    for (Dimension d: dimensions) {
      opts.add(new Option(String.valueOf(d.getGemId()), d.getDisplay()));
    }
    return opts;
  }

  @Override
  public void setDimEntry(String val) {
    super.setDimEntry(val);
    if(val == null || val.isBlank()) {
      return;
    }
    Long longValue = Long.valueOf(val);
    if(longValue != null) {
      Dimension d = DemoRecManager.getDimensionSure(Long.valueOf(val));
      this.getMeasuringValue().setDimension(d);
    }
  }

  @Override
  public Consumer<Void> createNewMeasuringValue() {
    return $ -> {
      Optional<MeasuringValue> valueOption = (Optional<MeasuringValue>) this.getMeasuringValue().build();
      if(valueOption.isPresent()) {
        this.getMeasuringValueEvent().accept(valueOption.get());
        this.setSelectDimensionUnitEntry(null);
        super.setMeasuringValue(DemoRecManager.measuringValueBuilder());
      }
    };
  }
}
