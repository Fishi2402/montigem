/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.montigem.gradle.specific.merge.MergeTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static de.montigem.gradle.BackendPlugin.MERGE_UMLP_TASK;

/**
 * Merge UMLP task is shared by MontiGem backend and frontend
 */
public abstract class SharedPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getTasks().register(MERGE_UMLP_TASK, MergeTask.class);
  }

}
