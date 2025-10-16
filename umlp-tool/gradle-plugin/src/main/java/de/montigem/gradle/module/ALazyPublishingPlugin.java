package de.montigem.gradle.module;

import org.gradle.api.Project;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

public abstract class ALazyPublishingPlugin {

  final SoftwareComponentFactory softwareComponentFactory;
  final TaskDependencyFactory taskDependencyFactory;
  public ALazyPublishingPlugin(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    this.softwareComponentFactory = softwareComponentFactory;
    this.taskDependencyFactory = taskDependencyFactory;
  }

  /**
   * Create a lazy {@link org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact} from a given
   * {@link Jar} task.
   * Provides compatibility between gradle 7 and 8
   * --
   * This is important for Gradle's dependency substition to work,
   *  otherwise hand-written reloactions have to be written.
   */
  protected PublishArtifact createPublishedArtifact(TaskProvider<Jar> jarTaskProvider, Project project) {
    try {
      // Attempt to use the Gradle 7.4.2 constructor via reflection
      return LazyPublishArtifact.class
          .getConstructor(org.gradle.api.provider.Provider.class, String.class, FileResolver.class)
          .newInstance(jarTaskProvider, null, ((ProjectInternal) project).getFileResolver());
    } catch (ReflectiveOperationException e7) {
      // If the 7.4.2 constructor is not found, try the Gradle 8 constructor
      try {
        // Gradle 8 - the TaskDependencyFactory parameter was added
        return LazyPublishArtifact.class.getConstructor(org.gradle.api.provider.Provider.class, String.class, FileResolver.class, TaskDependencyFactory.class)
            .newInstance(jarTaskProvider, null, ((ProjectInternal) project).getFileResolver(), taskDependencyFactory);
      } catch (ReflectiveOperationException e8) {
        throw new IllegalStateException("Incompatible LazyPublishArtifact constructor in gradle " + project.getGradle().getGradleVersion(), e8);
      }
    }
  }
}
