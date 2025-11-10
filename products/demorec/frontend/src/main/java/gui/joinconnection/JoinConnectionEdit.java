package gui.joinconnection;

import demorec.State;
import gui.connectionkind.JoinConnectionKindEditTOP;
import mc.fenix.input.gemdropdowninputtypes.Option;

import java.util.ArrayList;
import java.util.List;

public class JoinConnectionEdit extends JoinConnectionEditTOP  {

  @Override
  public List<Option> getConditionOptions () {
    List<Option> options = new ArrayList<>();
    options.add(new Option(State.HEALTHY.toString(), State.HEALTHY.name()));
    options.add(new Option(State.OTHER.toString(), State.OTHER.name()));
    return options;
  }
}
