package demorec.processmodeling;

import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import demorec.JoinConnection;
import demorec.JoinConnectionKind;
import demorec.JoinConnectionType;
import demorec.Process;
import demorec.ProcessModel;
import demorec.ProcessModelBuilder;
import demorec.ProcessStep;
import demorec.State;
import demorec.Task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class InitialProcessModelCreator {

  public ProcessModel createInitialProcessModel(Process process) {
    /*
     Does the process already have a process model?
     -> If yes, warn the user
    */
    if (process.getProcessModelGem().isPresent()) {
      System.err.println("WARNING: A new process model is being generated for the process with " +
          "GemID " + process.getGemId() + " although the process already had a process model");
    }

    /*
      Build a process model for the process
      -> Approach: The order of the process steps, as provided by the index,
                   yields the dependency chain
    */
    ProcessModelBuilder pmBuilder = DemoRecManager.processModelBuilder()
      .underlyingProcess(process)
      .name(getProcessName(process))
      .description(getProcessDescription(process));

    // 1) Sort the process steps ascendingly by their index
    ArrayList<ProcessStep> processStepsSortedByIndex = new ArrayList<>(process.getConsistsOfList().stream().filter(Objects::nonNull).collect(Collectors.toList()));
    processStepsSortedByIndex.sort(Comparator.comparingInt(ProcessStep::getIndex));

    // 2) Establishing the process model task by task
    Task previousTask = null;

    // Define a default join connection which gets used if an underlying process step has no join connection
    JoinConnectionKind jck = DemoRecManager.joinConnectionKindBuilder().joinConnectionType(JoinConnectionType.UNDEFINED).build().get();
    JoinConnection defaultConnection = DemoRecManager.joinConnectionBuilder().joinConnectionKind(jck).condition(State.HEALTHY).build().get();

    for (ProcessStep processStep : processStepsSortedByIndex) {
      Task newTask = DemoRecManager.taskBuilder()
          .name(getProcessStepName(processStep))
          .description(getProcessStepDescription(processStep))
          .underlyingProcessStep(processStep)
          .timeConsumption(processStep.getTimeConsumption())
          .uses(processStep.getUses() != null ? processStep.getUses() : null)
          .deconstructs(processStep.getDeconstructs() != null ? processStep.getDeconstructs() : defaultConnection)
          .build().get();
      if (previousTask != null) {
        newTask.addDependsOn(previousTask);
      }
      pmBuilder.addSubTasks(newTask);
      previousTask = newTask;
    }

    return pmBuilder.build().get();
  }

  private String getProcessName(Process process) {
    return "Deconstruction Process from " + process.getStartedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY));
  }

  private String getProcessDescription(Process process) {
    String startdate = process.getStartedAt() != null ?
        process.getStartedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)).toString()
        : "an unknown date";

    if (process.getDeconstructionOf() != null) {
      StringBuilder descriptionBasedOnBatteryPack = new StringBuilder();
      descriptionBasedOnBatteryPack.append("Process from disassembling the battery pack '" + process.getDeconstructionOf().getName() + "',");
      descriptionBasedOnBatteryPack.append(" performed on " + startdate);

      return descriptionBasedOnBatteryPack.toString();
    }
    else {
      StringBuilder descriptionBasedOnDate = new StringBuilder();
      descriptionBasedOnDate.append("Disassembly process performed on " + startdate);

      return descriptionBasedOnDate.toString();
    }
  }

  private String getProcessStepName(ProcessStep processStep) {
    if (processStep.getIndex() != null && processStep.getType() != null) {
      return "Task " + processStep.getIndex() + " " + processStep.getType().name();
    }
    return "Unknown Task " + System.currentTimeMillis();
  }

  private String getProcessStepDescription(ProcessStep processStep) {
    if (processStep.getDescription().isPresent()) {
      return processStep.getDescription().get();
    }
    else if (processStep.getDeconstructs() != null){
      StringBuilder descriptionBasedOnJoinConnection = new StringBuilder();

      if (processStep.getDeconstructs().getJoinConnectionKind() != null) {
        if (processStep.getDeconstructs().getJoinConnectionKind().getJoinConnectionType() != null) {
          descriptionBasedOnJoinConnection.append("Disassembly of a connection of type ");
          descriptionBasedOnJoinConnection.append(processStep.getDeconstructs().getJoinConnectionKind().getJoinConnectionType().toString());
        }
        else {
          descriptionBasedOnJoinConnection.append("Disassembly of a connection of type of Join Connection");
        }

        if (processStep.getDeconstructs().getJoinConnectionKind().getConnectsList() != null) {
          descriptionBasedOnJoinConnection.append(" between the following components: ");
          descriptionBasedOnJoinConnection.append(processStep.getDeconstructs().getJoinConnectionKind().getConnectsList().stream()
              .map(DemoRecComponent::getName)
              .collect(Collectors.joining(", ")));
          descriptionBasedOnJoinConnection.append(".\n");
        }
        else {
          descriptionBasedOnJoinConnection.append(" between unknown components.");
        }
      }

      if (processStep.getUses() != null) {
        descriptionBasedOnJoinConnection.append("The following end effector was used: ");
        descriptionBasedOnJoinConnection.append(processStep.getUses().getName() + ".");
      }
      else {
        descriptionBasedOnJoinConnection.append("An unknown end effector was used");
      }

      if (processStep.getTimeConsumption() != null) {
        descriptionBasedOnJoinConnection.append("The whole procedure took " + convertSecondsToMinutesAndRemainingSeconds(processStep.getTimeConsumption()) + ".");
      }
      else {
        descriptionBasedOnJoinConnection.append("The whole procedure took an unknown amount of time.");
      }

      return descriptionBasedOnJoinConnection.toString();
    }
    else {
      StringBuilder descriptionBasedOnEndEffector = new StringBuilder();

      if (processStep.getUses() != null) {
        descriptionBasedOnEndEffector.append("Disassembly task performed with the following end effector: ");
        descriptionBasedOnEndEffector.append(processStep.getUses().getName() + ".");
      }
      else {
        descriptionBasedOnEndEffector.append("Disassembly task performed with an unknown end effector");
      }

      if (processStep.getTimeConsumption() != null) {
        descriptionBasedOnEndEffector.append("The whole procedure took " + convertSecondsToMinutesAndRemainingSeconds(processStep.getTimeConsumption()) + ".");
      }
      else {
        descriptionBasedOnEndEffector.append("The whole procedure took an unknown amount of time.");
      }

      return descriptionBasedOnEndEffector.toString();
    }
  }

  public static String convertSecondsToMinutesAndRemainingSeconds(double seconds) {
    int minutes = (int)(seconds / 60);
    int remainingSeconds = (int)(seconds % 60);
    return minutes + " minutes and " + remainingSeconds + " seconds";
  }
}
