/* (c) https://github.com/MontiCore/monticore */
package gui;

import functionstructure4fenix.Channel;
import functionstructure4fenix.DoubleValue;
import functionstructure4fenix.Function;
import functionstructure4fenix.Port;
import functionstructure4fenix.Value;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntry;
import mc.fenix.functiondisplay.functionstructure4fenix.Channel4Display;
import mc.fenix.functiondisplay.functionstructure4fenix.Channel4DisplayBuilder;
import mc.fenix.functiondisplay.functionstructure4fenix.Function4Display;
import mc.fenix.functiondisplay.functionstructure4fenix.Function4DisplayBuilder;
import mc.fenix.functiondisplay.functionstructure4fenix.Port4Display;
import mc.fenix.functiondisplay.functionstructure4fenix.Port4DisplayBuilder;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class FunctionDetail extends FunctionDetailTOP {

  private Map<Function, Function4Display> function2displayMap = new HashMap<>();
  private Map<Function4Display, Function> display2functionMap = new HashMap<>();
  private Map<Channel, Channel4Display> channel2displayMap = new HashMap<>();
  private Map<Channel4Display, Channel> display2channelMap = new HashMap<>();
  private Map<Port, Port4Display> port2displayMap = new HashMap<>();
  private Map<Port4Display, Port> display2portMap = new HashMap<>();


  @Override
  public Boolean getEvalguardCondition(){
    return getSelectedChannel().isBuildFinished();
  }

  @Override
  public Consumer<Void> goToFunctionDetailsSub(Function sf) {
    return goToFunctionDetails(sf);
  }

  @Override
  public Consumer<Void> goToFunctionDetailsSuper(Function f) {
    if (getF().getOwnerGem().isPresent()) {
      return goToFunctionDetails(getF().getOwnerGem().get());
    }
    else {
      return goToFunctionDetails(f);
    }
  }

  public Consumer<Void> goToFunctionDetailsSuper2(Function f) {
    return goToFunctionDetailsSuper(f);
  }

  public Consumer<Void> goToFunctionDetails(Function sf) {
    return $ -> {
      Router.navigate("gui", "FunctionDetail", "" + sf.getGemId());
    };
  }

  public Consumer<Function4Display> goToFunction4DisplayDetails(){
    return  v -> {
      Router.navigate("gui", "FunctionDetail", "" + display2functionMap.get(v).getGemId());
    };
  }

  @Override
  public String getValueTextInValue(Port p) {
    return getPortStringValue(p);
  }

  @Override
  public String getValueTextOutValue(Port p) {
    return getPortStringValue(p);
  }

  public Consumer<Channel4Display> selectChannel() {
    return c -> {
      setSelectedChannel(display2channelMap.get(c));
    };
  }

  @Override
  public String getChannelValues2Value(){
    Channel c = getSelectedChannel();
    if (c == null) {
      return "";
    }
    String res2 = "";
    for (Value value : c.getFunctionStream().getValueList()) {
      if (value instanceof DoubleValue) {
        if (value instanceof DoubleValue) {
          res2 += ((DoubleValue) value).getContent() + ", ";
        }
      }
    }
    return res2.substring(0, res2.length() - 2);

  }

  @Override
  public List<Function4Display> getSelectedFunctions4Display(){
    List<Function4Display> res = new ArrayList<>();
    for(Function f : getSelectedFunctions()){
      res.add(function2displayMap.get(f));
    }
    return res;
  }


  public Consumer<Function4Display> toggleFunctionSelection(){
    return f -> {
      if(!display2functionMap.containsKey(f)){return;}
      if( getSelectedFunctions().contains(display2functionMap.get(f))) {
        ArrayList<Function> functions = new ArrayList<>(getSelectedFunctions());
        functions.remove(display2functionMap.get(f));
        this.setSelectedFunctions(functions);
        //removeSelectedFunctions(display2functionMap.get(f).getGemId());
      }else {
        ArrayList<Function> functions = new ArrayList<>(getSelectedFunctions());
        functions.add(display2functionMap.get(f));
        this.setSelectedFunctions(functions);
        //addSelectedFunctions(display2functionMap.get(f));
      }
    };
  }

  public Consumer<Channel4Display> toggleChannelSelection(){
    return c -> {
      if(!display2channelMap.containsKey(c)){return;}
      if(getSelectedChannels().contains(display2channelMap.get(c))) {
        ArrayList<Channel> channels = new ArrayList<>(getSelectedChannels());
        channels.remove(display2channelMap.get(c));
        this.setSelectedChannels(channels);
        //removeSelectedChannels(display2channelMap.get(c).getGemId());
      }else {
        ArrayList<Channel> channels = new ArrayList<>(getSelectedChannels());
        channels.add(display2channelMap.get(c));
        this.setSelectedChannels(channels);
        //addSelectedChannels(display2channelMap.get(c));
      }
    };
  }

  public Consumer<Port4Display> togglePortSelection(){
    return p -> {
      if(!display2portMap.containsKey(p)){return;}
      if(getSelectedPorts().contains(display2portMap.get(p))) {
        ArrayList<Port> ports = new ArrayList<>(getSelectedPorts());
        ports.remove(display2portMap.get(p));
        this.setSelectedPorts(ports);
        //removeSelectedPorts(display2portMap.get(p).getGemId());
      }else {
        ArrayList<Port> ports = new ArrayList<>(getSelectedPorts());
        ports.add(display2portMap.get(p));
        this.setSelectedPorts(ports);
        //addSelectedPorts(display2portMap.get(p));
      }
    };
  }

  @Override
  public GemLineChartData getChannelLineChartData() {
    Channel c = getSelectedChannel();
    GemLineChartData res = new GemLineChartData();

    GemLineChartEntry entry = new GemLineChartEntry();
    entry.setLabel("Channel Values");
    c.getFunctionStream().getValueList().forEach(v -> {
      if (v instanceof DoubleValue) {
        entry.addData(((DoubleValue) v).getContent().intValue());
      }
    });
    ArrayList labels = new ArrayList();
    for(int i = 0; i < c.getFunctionStream().getValueList().size(); i++){
      labels.add(""+i);
    }
    res.addEntries(entry);
    res.setLabelsList(labels);
    return res;
  }

  //
  private String getPortStringValue(Port p) {
    if (p.getChannel().getFunctionStream().getValueList() == null) {
      return null;
    }
    String res = "";
    for (Value value : p.getChannel().getFunctionStream().getValueList()) {
      if (value instanceof DoubleValue) {
        if (value instanceof DoubleValue) {
          res += ((DoubleValue) value).getContent() + ", ";
        }
      }
    }
    return res.substring(0, res.length() - 2);
  }

  @Override
  public Function4Display transformData(Function function) {
    Map<Port, Port4Display> portMap = new HashMap<>();
    Map<Channel, Channel4Display> channelMap = new HashMap<>();

    return this.function2Function4Display(function,  portMap);
  }

  private Function4Display function2Function4Display(Function function,
      Map<Port, Port4Display> portMap) {
    if (function2displayMap.containsKey(function)) {
      return function2displayMap.get(function);
    }
    else if (function == null) {
      return null;
    }
    else {
      Function4DisplayBuilder function4Display = new Function4DisplayBuilder()
          .name(function.getName() == null ? "" : function.getName());
      function2displayMap.put(function, function4Display);
      display2functionMap.put(function4Display, function);
      ArrayList<Port4Display> _in = new ArrayList<>();
      for (int i = 0; i < function.getInList().size(); i++) {
        _in.add(port2Port4Display(function.getIn(i)));
      }
      function4Display.in(_in);
      ArrayList<Port4Display> out = new ArrayList<>();
      for (int i = 0; i < function.getOutList().size(); i++) {
        out.add(port2Port4Display(function.getOut(i)));
      }
      function4Display.out(out);
      ArrayList<Function4Display> subf = new ArrayList<>();
      for (int i = 0; i < function.getSubFunctionList().size(); i++) {
        subf.add(function2Function4Display(function.getSubFunction(i),  portMap));
      }
      function4Display.subFunction(subf);
      function4Display.owner(Optional.ofNullable(function.getOwner() != null
          ? this.function2Function4Display(function.getOwner(),  portMap)
          : null));
      return function4Display.build().get();
    }
  }

  public Port4Display port2Port4Display(Port port) {
    if (port2displayMap.containsKey(port)) {
      return port2displayMap.get(port);
    }
    else {
      Port4DisplayBuilder port4Display = new Port4DisplayBuilder()
          .name(port.getName());
      port2displayMap.put(port, port4Display);
      display2portMap.put(port4Display, port);
      port4Display.channel4Display(port.getChannel() != null
              ? channel2Channel4Display(port.getChannel())
              : null);
      return port4Display.build().get();
    }
  }

  public Channel4Display channel2Channel4Display(Channel channel) {
    if (channel2displayMap.containsKey(channel)) {
      return channel2displayMap.get(channel);
    }
    else {
      Channel4DisplayBuilder channel4Display = new Channel4DisplayBuilder();
      List<Port4Display> ports = new ArrayList<>();
      for (int i = 0; i < channel.getPortList().size(); i++) {
        ports.add(port2Port4Display(channel.getPort(i)));
      }
      channel4Display.port4Display(ports);
      channel2displayMap.put(channel, channel4Display);
      display2channelMap.put(channel4Display, channel);
      return channel4Display;
    }
  }
}
