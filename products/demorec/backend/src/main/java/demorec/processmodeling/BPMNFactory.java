package demorec.processmodeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.IllegalArgumentException;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;

import de.monticore.cd.facade.MCQualifiedNameFacade;

public class BPMNFactory {
  public enum SupportedGatewayType {
    PARALLEL,
    EXCLUSIVE
  }
  public enum SupportedGatewayDirection {
    SPLIT,
    MERGE
  }
  public enum SupportedEventType {
    START,
    END
  }

  private ASTWFProcessBuilder processBuilder;
  private HashMap<String, ASTFlowElement> elements;
  private HashMap<String, ASTFlowBlock> flowBlocks;

  public BPMNFactory(String processName) {
    WorkflowMill.init();
    processBuilder = WorkflowMill.wFProcessBuilder();
    processBuilder.setName(processName);
    elements = new HashMap<>();
    flowBlocks = new HashMap<>();
  }

  // TODO is this still necessary?
  public BPMNFactory addLane(String name) {
    ASTWFLaneBuilder laneBuilder = WorkflowMill.wFLaneBuilder();
    laneBuilder.setName(name);

    ASTWFLane newLane = laneBuilder.build();
    processBuilder.addFlowElement(newLane);

    return this;
  }

  public BPMNFactory addTask(String name, String type, String parentName) {
    ASTWFTaskBuilder taskBuilder = WorkflowMill.wFTaskBuilder();
    taskBuilder.setName(name);
    taskBuilder.setType(4); // TODO this is shit
    taskBuilder.setModifier(WorkflowMill.modifierBuilder().setStereotypeAbsent().build());

    ASTWFTask newTask = taskBuilder.build();
    processBuilder.addFlowElement(newTask);
    elements.put(name, newTask);

    return this;
  }

  public BPMNFactory addProcessSequenceFlow(ArrayList<String> flowElements) {
    if (!flowElements.isEmpty()) {
      // Does every element in the sequence flow exist?
      for (String element : flowElements) {
        if (!elements.containsKey(element) && !flowBlocks.containsKey(element)) {
          throw new IllegalArgumentException(String.format("Cannot add the flow element with name '%s', because it has not been added yet!", element));
        }
      }
      processBuilder.addFlowElement(buildASTSequenceFlowFromElementNames(flowElements));
    }

    return this;
  }

  private ASTWFEvent buildInlineEvent(SupportedEventType type) {
    ASTWFEventBuilder neBuilder = WorkflowMill.wFEventBuilder();
    //neBuilder.setParentRef(processBuilder.getName());

    if (type == SupportedEventType.START) {
      neBuilder.setStart(true);
    }
    else if (type == SupportedEventType.END) {
      neBuilder.setEnd(true);
    }

    ASTWFEvent newEvent = neBuilder.build();
    newEvent.setName(type.name());
    processBuilder.addFlowElement(newEvent);

    return newEvent;
  }

  private ASTSequenceFlow buildASTSequenceFlowFromElementNames(ArrayList<String> flowElements) {
    ASTSequenceFlowBuilder mainSfBuilder = WorkflowMill.sequenceFlowBuilder();

    // Add start event
    ASTWFEvent startEvent = buildInlineEvent(SupportedEventType.START);
    mainSfBuilder.addPath(buildASTFlowTargetFromASTInlineEvent(startEvent));

    // Add the actual sequence flow
    for (String elementName : flowElements) {
      if (elements.containsKey(elementName)) {
        mainSfBuilder.addPath(buildASTFlowTargetFromElementName(elementName));
      }
      else if (flowBlocks.containsKey(elementName)) {
        ASTFlowBlock block = flowBlocks.get(elementName);
        mainSfBuilder.addPath(buildASTFlowTargetFromASTFlowBlock(block));
      }
      else {
        throw new IllegalArgumentException(String.format("Cannot add the element with name '%s' to a sequence flow, because it has not been added yet!", elementName));
      }
    }

    // Add end event
    ASTWFEvent endEvent = buildInlineEvent(SupportedEventType.END);
    mainSfBuilder.addPath(buildASTFlowTargetFromASTInlineEvent(endEvent));

    return mainSfBuilder.build();
  }

  private ASTFlowTarget buildASTFlowTargetFromASTFlowBlock(ASTFlowBlock flowBlock) {
    ASTFlowTargetBuilder ftBuilder = WorkflowMill.flowTargetBuilder();
    ftBuilder.setBlock(flowBlock);

    return ftBuilder.build();
  }

  private ASTFlowTarget buildASTFlowTargetFromASTInlineEvent(ASTWFEvent event) {
    ASTFlowTargetBuilder ftBuilder = WorkflowMill.flowTargetBuilder();
    //ftBuilder.setNodeRef(MCQualifiedNameFacade.createQualifiedName(event.getName()));

    return ftBuilder.build();
  }

  private ASTFlowTarget buildASTFlowTargetFromElementName(String flowElement) {
    ASTFlowTargetBuilder ftBuilder = WorkflowMill.flowTargetBuilder();
    //ftBuilder.setNodeRef(MCQualifiedNameFacade.createQualifiedName(flowElement));

    return ftBuilder.build();
  }

  public BPMNFactory addOneSidedGateway(String gatewayIdentifier, SupportedGatewayType type, SupportedGatewayDirection direction) {
    if (elements.containsKey(gatewayIdentifier)) {
      throw new IllegalArgumentException(String.format("There already exists an element with the identifier '%s', please choose another name", gatewayIdentifier));
    }

    // Build the gateway's type
    ASTGatewayTypeBuilder gwTypeBuilder = WorkflowMill.gatewayTypeBuilder();
    if (type == SupportedGatewayType.PARALLEL) {
      gwTypeBuilder.setParallel(true);
    }
    else if (type == SupportedGatewayType.EXCLUSIVE) {
      gwTypeBuilder.setExclusive(true);
    }

    ASTWFGateway gw = null;
    gw = buildOneSidedGateway(gatewayIdentifier, gwTypeBuilder.build(), direction);

    elements.put(gatewayIdentifier, gw);

    return this;
  }

  public BPMNFactory addFlowBlock(String flowBlockIdentifier, ArrayList<ArrayList<String>> flowElements) {
    if (!flowElements.isEmpty()) { // An empty flow block in the Workflow DSL cannot be compiled
      for (ArrayList<String> subFlow : flowElements) {
        for (String element : subFlow) {
          if (!elements.containsKey(element) && !flowBlocks.containsKey(element)) {
            throw new IllegalArgumentException(String.format("Cannot add the flow block with name '%s', because the element '%s' from it's flow elements has not been added yet!", flowBlockIdentifier, element));
          }
        }
      }
      ASTFlowBlock flowBlock = buildGatewayFlowBlock(flowElements);
      flowBlocks.put(flowBlockIdentifier, flowBlock);
    }
    return this;
  }

  private ASTFlowBlock buildGatewayFlowBlock(ArrayList<ArrayList<String>> flowElements) {
    ASTFlowBlockBuilder fBlockBuilder = WorkflowMill.flowBlockBuilder();
    for (ArrayList<String> flow : flowElements) {
      ASTSequenceFlowBuilder fBranchBuilder = WorkflowMill.sequenceFlowBuilder();
      for (String flowElementName : flow) {
        // We do not check here, if the flow element exists in order to ease the serialization
        if (elements.containsKey(flowElementName)) {
          fBranchBuilder.addPath(buildASTFlowTargetFromElementName(flowElementName));
        }
        else if (flowBlocks.containsKey(flowElementName)) {
          fBranchBuilder.addPath(buildASTFlowTargetFromASTFlowBlock(flowBlocks.get(flowElementName)));
        }
        else {
          throw new IllegalArgumentException(String.format("Cannot add the element with name '%s' to a gateway's flow block, because it has not been added yet!", flowElementName));
        }
      }
      fBlockBuilder.addBranch(fBranchBuilder.build());
    }

    return fBlockBuilder.build();
  }

  private ASTWFGateway buildOneSidedGateway(String id, ASTGatewayType type,  SupportedGatewayDirection direction) {
    ASTWFGatewayBuilder gwBuilder = WorkflowMill.wFGatewayBuilder();
    if(direction == SupportedGatewayDirection.SPLIT) {
      gwBuilder.setDirection(0); // TODO sehr ugly
    } else {
      gwBuilder.setDirection(1);
    }
    gwBuilder.setType(type);
    gwBuilder.setName(id);
    gwBuilder.setModifier(WorkflowMill.modifierBuilder().setStereotypeAbsent().build());

    return gwBuilder.build();
  }

  public ASTWorkflowCompilationUnit getCompilationUnit() {
    ASTWorkflowCompilationUnitBuilder cuBuilder = WorkflowMill.workflowCompilationUnitBuilder();

    cuBuilder.setWFProcess(processBuilder.build());

    return cuBuilder.build();
  }

  public String buildString() {
    return WorkflowMill.prettyPrint(getCompilationUnit(), true);
  }

}
