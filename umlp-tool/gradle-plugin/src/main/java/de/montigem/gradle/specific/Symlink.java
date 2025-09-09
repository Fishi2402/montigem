package de.montigem.gradle.specific;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;

public abstract class Symlink {

  @OutputFile
  public abstract RegularFileProperty getLinkFile();

  @InputFile
  public abstract DirectoryProperty getTargetFile();

}
