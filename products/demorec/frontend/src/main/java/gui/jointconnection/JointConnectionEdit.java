package gui.jointconnection;

import demorec.State;
import gui.connectionkind.JointConnectionKindEditTOP;
import mc.fenix.input.gemdropdowninputtypes.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class JointConnectionEdit extends JointConnectionEditTOP  {

  @Override
  public List<Option> getConditionOptions () {
    List<Option> options = new ArrayList<>();
    options.add(new Option(State.HEALTHY.toString(), State.HEALTHY.name()));
    options.add(new Option(State.OTHER.toString(), State.OTHER.name()));
    return options;
  }

/*  public Consumer<String> updatePosition() {
    return (String coords) -> {
      String[] parts = coords.split(",");
      if (parts.length == 3) {
        this.getConnection().setXPos(Optional.of(Double.parseDouble(parts[0])));
        this.getConnection().setYPos(Optional.of(Double.parseDouble(parts[1])));
        this.getConnection().setZPos(Optional.of(Double.parseDouble(parts[2])));
      }
    };
  }*/

  public Consumer<String> selectImage() {
    return (String url) -> {
      this.setImageUrl(url);
    };
  }
}
