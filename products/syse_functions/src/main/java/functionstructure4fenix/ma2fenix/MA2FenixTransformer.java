package functionstructure4fenix.ma2fenix;

import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.ArcBasisTraverser;
import de.se_rwth.commons.logging.Log;
import functionstructure4fenix.Category;
import functionstructure4fenix.Channel;
import functionstructure4fenix.Data;
import functionstructure4fenix.Function;
import functionstructure4fenix.FunctionStructure4FenixManager;
import functionstructure4fenix.Port;
import functionstructure4fenix.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class MA2FenixTransformer {
  private Stack<String> stack;
  private Map<String, Type> types;
  private Map<String, Category> categories;
  private List<Function> functions;
  private List<Port> ports;
  private List<Channel> channels;
  MA2FenixTransformer(){
    stack = new Stack();
    types = new HashMap();
    functions = new ArrayList();
    ports = new ArrayList();
    channels = new ArrayList();
  }

  private final Data data = FunctionStructure4FenixManager.dataBuilder().build().get();
  private HashMap<String, Channel> channelMap;
  public String getQname(){
    String qname = "";
    for(String name : stack){
      if(!qname.isEmpty()) qname +=".";
      qname += name;
    }
    return qname;
  }
  public Function createFunction(ASTComponentType node){
    if(this.getQname().isEmpty()) {
      stack.push(node.getName().toLowerCase());
    }
    Function function = FunctionStructure4FenixManager.functionBuilder().name(this.getQname());
    for (ASTConnector c : node.getConnectors()){
      mapConnectorToChannel(c);
    }
    if(node.getSymbol().isDecomposed()){
      for(ASTComponentInstance instance : node.getSubComponents()) {
        stack.push(instance.getName());
        createFunction(node.getSpannedScope().resolveComponentTypeLocally(instance.getSymbol().getType().getTypeInfo().getName()).get().getAstNode());
        stack.pop();
      }
    }
    Channel c = FunctionStructure4FenixManager.channelBuilder();

    for(ASTConnector connector : node.getConnectors()){
      mapConnectorToChannel(connector);
    }

    List<ASTArcPort> inports = node.getPorts().stream().filter(x -> x.getSymbol().isIncoming()).collect(Collectors.toList());
    List<ASTArcPort> outports = node.getPorts().stream().filter(x -> x.getSymbol().isOutgoing()).collect(Collectors.toList());

    for (ASTArcPort inport : inports) {
      function.addIn(createPort(inport));
    }
    for(ASTArcPort outport : outports){
      function.addOut(createPort(outport));
    }
    if(getQname().isEmpty()){
      stack.pop();
      buildFunctionStructure();
    }
    functions.add(function);
    return function;
  }

  public void buildFunctionStructure() {
    buildTypes();
    buildFunctions();
    buildChannels();
    buildPorts();
  }

  public void buildChannels(){
    for(Channel c : channels){
      c.build();
    }
  }

  public void buildTypes(){
    for(Type t : types.values()){
      t.build();
    }
  }

  public void buildFunctions(){
    for(Function f : functions){
      f.build();
    }
  }

  public void buildPorts(){
    for(Port p : ports){
      p.build();
    }
  }

  public Type createType(ASTArcPort node){

  }

  public Port createPort(ASTArcPort node){
    Port port = FunctionStructure4FenixManager.portBuilder().name(node.getName());
    if(channelMap.containsKey(getQname()+"."+node.getName())){
      port.setChannel(channelMap.get(getQname()+"."+node.getName()));
    }else{
      Log.error("Port " + this.getQname()+"."+port.getName()+ " does span a channel");
    }
    return port;
  }

  public void mapConnectorToChannel(ASTConnector connector){
    ASTPortAccess src = connector.getSource();
    for (ASTPortAccess tgt : connector.getTargetList()) {
      if (src.isPresentComponent() && tgt.isPresentComponent())
        createChannel(src, tgt);
      else if (!src.isPresentComponent() && tgt.isPresentComponent()) {
        createChannel(tgt, src);
      } else if (src.isPresentComponent() && !tgt.isPresentComponent()) {
        createChannel(src, tgt);
      }
    }
  }
  public void createChannel(ASTPortAccess key, ASTPortAccess value){
    if(channelMap.containsKey(this.getQname()+"."+value.getQName())) {
      if(channelMap.containsKey(this.getQname()+"."+key.getQName())) {
        Channel del = channelMap.get(this.getQname()+"."+value.getQName());
        //for(: )
        FunctionStructure4FenixManager.deleteChannel(channelMap.get(this.getQname()+"."+value.getQName()).getGemId());
      }else {
        channelMap.put(this.getQname()+"."+key.getQName(), channelMap.get(this.getQname()+"."+value.getQName()));
      }
    }else{
      Category category;
      key.getPortSymbol().getType().getTypeInfo().getFullName();
      //channelMap.put(this.getQname()+"."+key.getQName(), FunctionStructure4FenixManager.channelBuilder().category());
    }
  }
  public void connectors(){

  }

}
