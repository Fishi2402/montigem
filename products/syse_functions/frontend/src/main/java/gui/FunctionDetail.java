/* (c) https://github.com/MontiCore/monticore */
package gui;

import functionstructure4fenix.Channel;
import functionstructure4fenix.DoubleValue;
import functionstructure4fenix.Function;
import functionstructure4fenix.Port;
import functionstructure4fenix.Value;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class FunctionDetail extends FunctionDetailTOP {

  public FunctionDetail(Function f) {
    super();
    setF(f);
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

  public Consumer<Void> goToFunctionDetails(Function sf) {
    return $ -> {
      Router.navigate("gui", "FunctionDetail", "" + sf.getGemId());
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
    Map<Function, Function4Display> functionMap = new HashMap();
    Map<Port, Port4Display> portMap = new HashMap<>();
    Map<Channel, Channel4Display> channelMap = new HashMap<>();

    return this.function2Function4Display(function, functionMap, portMap, channelMap);
  }

  private Function4Display function2Function4Display(Function function, Map<Function, Function4Display> functionMap,
      Map<Port, Port4Display> portMap, Map<Channel, Channel4Display> channelMap) {
    if (functionMap.containsKey(function)) {
      return functionMap.get(function);
    }
    else if (function == null) {
      return null;
    }
    else {
      Function4DisplayBuilder function4Display = new Function4DisplayBuilder()
          .name(function.getName() == null ? "" : function.getName());
      functionMap.put(function, function4Display);
      ArrayList<Port4Display> _in = new ArrayList<>();
      for (int i = 0; i < function.getInList().size(); i++) {
        _in.add(port2Port4Display(function.getIn(i), portMap, channelMap));
      }
      function4Display.in(_in);
      ArrayList<Port4Display> out = new ArrayList<>();
      for (int i = 0; i < function.getOutList().size(); i++) {
        out.add(port2Port4Display(function.getOut(i), portMap, channelMap));
      }
      function4Display.out(out);
      ArrayList<Function4Display> subf = new ArrayList<>();
      for (int i = 0; i < function.getSubFunctionList().size(); i++) {
        subf.add(function2Function4Display(function.getSubFunction(i), functionMap, portMap, channelMap));
      }
      function4Display.subFunction(subf);
      function4Display.owner(Optional.ofNullable(function.getOwner() != null
          ? this.function2Function4Display(function.getOwner(), functionMap, portMap, channelMap)
          : null));
      return function4Display.build().get();
    }
  }

  public Port4Display port2Port4Display(Port port, Map<Port, Port4Display> portMap,
      Map<Channel, Channel4Display> channelMap) {
    if (portMap.containsKey(port)) {
      return portMap.get(port);
    }
    else {
      Port4DisplayBuilder port4Display = new Port4DisplayBuilder()
          .name(port.getName());
      portMap.put(port, port4Display);
      port4Display.channel4Display(port.getChannel() != null
              ? channel2Channel4Display(port.getChannel(), portMap, channelMap)
              : null);
      return port4Display.build().get();
    }
  }

  public Channel4Display channel2Channel4Display(Channel channel,
      Map<Port, Port4Display> portMap, Map<Channel, Channel4Display> channelMap) {
    if (channelMap.containsKey(channel)) {
      return channelMap.get(channel);
    }
    else {
      Channel4DisplayBuilder channel4Display = new Channel4DisplayBuilder();
      List<Port4Display> ports = new ArrayList<>();
      for (int i = 0; i < channel.getPortList().size(); i++) {
        ports.add(portMap.get(channel.getPort(i)));
      }
      channel4Display.port4Display(ports);
      channelMap.put(channel, channel4Display);
      return channel4Display;
    }
  }
}
