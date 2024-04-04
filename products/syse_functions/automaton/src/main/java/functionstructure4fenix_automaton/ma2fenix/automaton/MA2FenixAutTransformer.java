package functionstructure4fenix_automaton.ma2fenix.automaton;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import functionstructure4fenix_automaton.Automaton;
import functionstructure4fenix_automaton.FunctionAut;
import functionstructure4fenix.Function;
import functionstructure4fenix.ma2fenix.MA2FenixTransformer;
import functionstructure4fenix_automaton.FunctionAutBuilder;
import functionstructure4fenix_automaton.FunctionStructure4Fenix_AutomatonManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MA2FenixAutTransformer extends MA2FenixTransformer {
  private List<Automaton> automata;
  protected List<FunctionAut> functions;
  public MA2FenixAutTransformer(){
    super();
    automata = new ArrayList();
    functions = new ArrayList();
  }

  @Override
  public Function createFunction(ASTComponentType node){
    if(this.getQname().isEmpty()) {
      stack.push(node.getName().toLowerCase());
    }
    FunctionAutBuilder function = FunctionStructure4Fenix_AutomatonManager.functionAutBuilder().name(stack.peek());

    for (ASTConnector c : node.getConnectors()){
      mapConnectorToChannel(c);
    }

    if(node.getSymbol().isDecomposed()){
      for(ASTComponentInstance instance : node.getSubComponents()) {
        stack.push(instance.getName());
        function.subFunctionAdd(createFunction(node.getSpannedScope().resolveComponentType(
            instance.getSymbol().getType().getTypeInfo().getName()
        ).get().getAstNode()));
        stack.pop();
      }
    }

    List<ASTArcPort> inports = node.getPorts().stream().filter(x -> x.getSymbol().isIncoming()).collect(Collectors.toList());
    List<ASTArcPort> outports = node.getPorts().stream().filter(x -> x.getSymbol().isOutgoing()).collect(Collectors.toList());

    for (ASTArcPort inport : inports) {
      function.inAdd(createPort(inport));
    }
    for(ASTArcPort outport : outports){
      function.outAdd(createPort(outport));
    }

    if(node.getSymbol().isAtomic()){
      Optional<ASTArcStatechart> sc = node.getBody().streamArcElementsOfType(ASTArcStatechart.class).findFirst();
      if(sc.isPresent()) {
        function.automaton(createAutomaton(sc.get()));
      }
    }

    functions.add(function);
    if(getQname().isEmpty()){
      stack.pop();
      buildFunctionStructure();
    }

    return function;
  }

  @Override
  public void buildFunctionStructure() {
    buildTypes();
    buildCategories();
    buildChannels();
    buildPorts();
    buildAutomata();
    buildFunctions();
  }

  @Override
  public void buildFunctions(){
    for(FunctionAut f : functions){
      f.build();
    }
  }

  public void buildAutomata(){
    for(Automaton a : automata){
      a.build();
    }
  }

  public Automaton createAutomaton(ASTArcStatechart node){
    Automaton aut = FunctionStructure4Fenix_AutomatonManager.automatonBuilder()
        .states(node.getStates().size());
    automata.add(aut);
    return aut;
  }

}
