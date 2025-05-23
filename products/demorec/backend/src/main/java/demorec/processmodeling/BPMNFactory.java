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

  private ASTProcessBuilder processBuilder;
  private HashMap<String, ASTFlowElement> elements;
  private HashMap<String, ASTFlowBlock> flowBlocks;

  public BPMNFactory(String processName) {
    WorkflowMill.init();
    processBuilder = WorkflowMill.processBuilder();
    processBuilder.setName(processName);
    elements = new HashMap<>();
    flowBlocks = new HashMap<>();
  }

  public BPMNFactory addLane(String name) {
    ASTLaneBuilder laneBuilder = WorkflowMill.laneBuilder();
    laneBuilder.setName(name);

    ASTLane newLane = laneBuilder.build();
    processBuilder.addLane(newLane);

    return this;
  }

  public BPMNFactory addTask(String name, String type, String parentName) {
    ASTTaskBuilder taskBuilder = WorkflowMill.taskBuilder();
    taskBuilder.setName(name);
    taskBuilder.setType(ASTTaskType.valueOf(type.trim().toUpperCase()));
    taskBuilder.setParentRef(parentName);
    taskBuilder.setModifier(WorkflowMill.modifierBuilder().setStereotypeAbsent().build());

    ASTTask newTask = taskBuilder.build();
    processBuilder.getLane(0).addFlowElement(newTask);
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
      processBuilder.getLane(0).addFlowElement(buildASTSequenceFlowFromElementNames(flowElements));
    }

    return this;
  }

  private ASTInlineEvent buildInlineEvent(SupportedEventType type) {
    ASTInlineEventBuilder neBuilder = WorkflowMill.inlineEventBuilder();
    neBuilder.setParentRef(processBuilder.getName());

    if (type == SupportedEventType.START) {
      neBuilder.setType(ASTEventType.START);
    }
    else if (type == SupportedEventType.END) {
      neBuilder.setType(ASTEventType.END);
    }

    ASTInlineEvent newEvent = neBuilder.build();
    newEvent.setName(type.name());
    processBuilder.getLane(0).addFlowElement(newEvent);

    return newEvent;
  }

  private ASTSequenceFlow buildASTSequenceFlowFromElementNames(ArrayList<String> flowElements) {
    ASTSequenceFlowBuilder mainSfBuilder = WorkflowMill.sequenceFlowBuilder();

    // Add start event
    ASTInlineEvent startEvent = buildInlineEvent(SupportedEventType.START);
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
    ASTInlineEvent endEvent = buildInlineEvent(SupportedEventType.END);
    mainSfBuilder.addPath(buildASTFlowTargetFromASTInlineEvent(endEvent));

    return mainSfBuilder.build();
  }

  private ASTFlowTarget buildASTFlowTargetFromASTFlowBlock(ASTFlowBlock flowBlock) {
    ASTFlowTargetBuilder ftBuilder = WorkflowMill.flowTargetBuilder();
    ftBuilder.setBlock(flowBlock);

    return ftBuilder.build();
  }

  private ASTFlowTarget buildASTFlowTargetFromASTInlineEvent(ASTInlineEvent event) {
    ASTFlowTargetBuilder ftBuilder = WorkflowMill.flowTargetBuilder();
    ftBuilder.setNodeRef(MCQualifiedNameFacade.createQualifiedName(event.getName()));

    return ftBuilder.build();
  }

  private ASTFlowTarget buildASTFlowTargetFromElementName(String flowElement) {
    ASTFlowTargetBuilder ftBuilder = WorkflowMill.flowTargetBuilder();
    ftBuilder.setNodeRef(MCQualifiedNameFacade.createQualifiedName(flowElement));

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

    ASTNamedGateway gw = null;
    if (direction == SupportedGatewayDirection.SPLIT) {
      gw = buildOneSidedGateway(gatewayIdentifier, gwTypeBuilder.build(), ASTGatewayDirection.SPLIT);
    }
    else if (direction == SupportedGatewayDirection.MERGE) {
      gw = buildOneSidedGateway(gatewayIdentifier, gwTypeBuilder.build(), ASTGatewayDirection.MERGE);
    }

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
      ASTFlowBranchBuilder fBranchBuilder = WorkflowMill.flowBranchBuilder();
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

  private ASTNamedGateway buildOneSidedGateway(String id, ASTGatewayType type, ASTGatewayDirection direction) {
    ASTNamedGatewayBuilder gwBuilder = WorkflowMill.namedGatewayBuilder();
    gwBuilder.setDirection(direction);
    gwBuilder.setType(type);
    gwBuilder.setName(id);
    gwBuilder.setModifier(WorkflowMill.modifierBuilder().setStereotypeAbsent().build());

    return gwBuilder.build();
  }

  public ASTWorkflowCompilationUnit getCompilationUnit() {
    ASTWorkflowCompilationUnitBuilder cuBuilder = WorkflowMill.workflowCompilationUnitBuilder();

    // No IO-Specification is needed here
    processBuilder.setIOSpecification(WorkflowMill.iOSpecificationBuilder().build());

    cuBuilder.setProcess(processBuilder.build());

    return cuBuilder.build();
  }

  public String buildString() {
    return WorkflowMill.prettyPrint(getCompilationUnit(), true);
  }

}
