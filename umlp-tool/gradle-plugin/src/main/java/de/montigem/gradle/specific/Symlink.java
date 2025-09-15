package de.montigem.gradle.specific;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;

public abstract class Symlink {

  @Internal
  public abstract RegularFileProperty getLink();

  @InputDirectory
  public abstract DirectoryProperty getTarget();

}
