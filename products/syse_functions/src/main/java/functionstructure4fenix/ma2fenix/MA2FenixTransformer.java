package functionstructure4fenix.ma2fenix;

import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import functionstructure4fenix.Category;
import functionstructure4fenix.Channel;
import functionstructure4fenix.Data;
import functionstructure4fenix.Function;
import functionstructure4fenix.FunctionStructure4FenixManagerImpl;
import functionstructure4fenix.Port;
import functionstructure4fenix.PortBuilder;
import functionstructure4fenix.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class MA2FenixTransformer {
  private Stack<String> stack;
  private List<Type> types;
  private Map<String, Category> categories;
  private List<Function> functions;
  private List<Port> ports;
  private final Data data;

  MA2FenixTransformer(){
    channelMap = new HashMap<>();
    stack = new Stack();
    types = new ArrayList();
    functions = new ArrayList();
    ports = new ArrayList();
    categories = new HashMap<>();
    FunctionStructure4FenixManagerImpl.init();
    data = FunctionStructure4FenixManagerImpl.dataBuilder().build().get();
  }


  private HashMap<String, Channel> channelMap;

  public void transform(ASTComponentType node){
    createFunction(node);
    buildFunctionStructure();
  }

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
    Function function = FunctionStructure4FenixManagerImpl.functionBuilder().name(this.getQname());

    for (ASTConnector c : node.getConnectors()){
      mapConnectorToChannel(c);
    }

    if(node.getSymbol().isDecomposed()){
      for(ASTComponentInstance instance : node.getSubComponents()) {
        stack.push(instance.getName());
        function.addSubFunction(createFunction(node.getSpannedScope().resolveComponentType(
            instance.getSymbol().getType().getTypeInfo().getName()
        ).get().getAstNode()));
        stack.pop();
      }
    }

    List<ASTArcPort> inports = node.getPorts().stream().filter(x -> x.getSymbol().isIncoming()).collect(Collectors.toList());
    List<ASTArcPort> outports = node.getPorts().stream().filter(x -> x.getSymbol().isOutgoing()).collect(Collectors.toList());

    for (ASTArcPort inport : inports) {
      function.addIn(createPort(inport));
    }
    for(ASTArcPort outport : outports){
      function.addOut(createPort(outport));
    }
    functions.add(function);
    if(getQname().isEmpty()){
      stack.pop();
      buildFunctionStructure();
    }
    return function;
  }

  public void buildFunctionStructure() {
    buildTypes();
    buildCategories();
    buildChannels();
    buildPorts();
    buildFunctions();
    System.out.println("test");
  }

  public void buildChannels(){
    for(Channel c : new HashSet<>(channelMap.values())){
      c.build();
    }
  }

  public void buildTypes(){
    for(Type t : types){
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

  public void buildCategories(){
    for(Category c : categories.values()){
      c.build();
    }
  }

  public Category createCategory(ASTArcPort node){
    Category category;
    if(categories.containsKey(node.getSymbol().getTypeInfo().getFullName())){
      category = categories.get(node.getSymbol().getTypeInfo().getFullName());
    }else {
      category = FunctionStructure4FenixManagerImpl.categoryBuilder()
          .type(createType(node.getSymbol().getTypeInfo())).kind(data);
      categories.put(node.getSymbol().getTypeInfo().getFullName(), category);
    }
    return category;
  }

  public Type createType(TypeSymbol node){
    Type type = FunctionStructure4FenixManagerImpl.typeBuilder().mctype(node.getFullName());
    types.add(type);
    return type;
  }

  public Port createPort(ASTArcPort node){
    PortBuilder port = FunctionStructure4FenixManagerImpl.portBuilder().name(node.getName());
    if(channelMap.containsKey(getQname()+"."+node.getName())){
      port.channel(channelMap.get(getQname()+"."+node.getName()));
    }else{
      Log.error("Port " + this.getQname()+"."+port.getName()+ " does not span a channel");
    }
    ports.add(port);
    return port;
  }

  public void mapConnectorToChannel(ASTConnector connector){
    ASTPortAccess src = connector.getSource();
    for (ASTPortAccess tgt : connector.getTargetList()) {
      if (src.isPresentComponent() && tgt.isPresentComponent()) {
        createChannel(src, tgt);
      } else if (!src.isPresentComponent() && tgt.isPresentComponent()) {
        createChannel(tgt, src);
      } else if (src.isPresentComponent() && !tgt.isPresentComponent()) {
        createChannel(src, tgt);
      }
    }
  }
  public void createChannel(ASTPortAccess key, ASTPortAccess value) {
    if (channelMap.containsKey(this.getQname() + "." + value.getQName())) {
      if (channelMap.containsKey(this.getQname() + "." + key.getQName())) {
        Channel del = channelMap.get(this.getQname() + "." + value.getQName());
        for (Map.Entry<String, Channel> entry : channelMap.entrySet()) {
          if (entry.getValue().equals(del)) {
            channelMap.put(entry.getKey(), channelMap.get(this.getQname() + "." + key.getQName()));
          }
        }
        FunctionStructure4FenixManagerImpl.deleteChannel(del.getGemId());
      } else {
        channelMap.put(this.getQname() + "." + key.getQName(), channelMap.get(this.getQname() + "." + value.getQName()));
      }
    } else if (channelMap.containsKey(this.getQname() + "." + key.getQName())){
      channelMap.put(this.getQname() + "." + value.getQName(), channelMap.get(this.getQname() + "." + key.getQName()));
    }else{
      Channel c = FunctionStructure4FenixManagerImpl.channelBuilder().category(createCategory(key.getPortSymbol().getAstNode()));
      channelMap.put(this.getQname()+"."+key.getQName(), c);
      channelMap.put(this.getQname()+"."+value.getQName(), c);
    }
  }

}
