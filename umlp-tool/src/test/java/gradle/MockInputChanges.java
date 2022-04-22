package gradle;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.InputChanges;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MockInputChanges implements InputChanges {
  protected Set<FileChange> modified = new HashSet<>();

  public InputChanges added(File f){
    modified.add(new MockFileChange(f, ChangeType.ADDED));
    return this;
  }
  public InputChanges modified(File f){
    modified.add(new MockFileChange(f, ChangeType.MODIFIED));
    return this;
  }
  public InputChanges removed(File f){
    modified.add(new MockFileChange(f, ChangeType.REMOVED));
    return this;
  }



  @Override
  public boolean isIncremental() {
    return true;
  }

  public Iterable<FileChange> getFileChanges(Set<File> fileCollection) {
    return modified.stream().filter(ch -> fileCollection.contains(ch.getFile())).collect(Collectors.toSet());
  }

  @Override
  public Iterable<FileChange> getFileChanges(FileCollection fileCollection) {
    return getFileChanges(fileCollection.getFiles());
  }

  @Override
  public Iterable<FileChange> getFileChanges(Provider<? extends FileSystemLocation> provider) {
    Set<File> inputFiles = new HashSet<>();

    if(provider.isPresent()){
      File f = provider.get().getAsFile();
      if(f.isFile()){
        inputFiles.add(f);
      }
      if(f.isDirectory()){
        File[] files = f.listFiles();
        if(files != null){
          inputFiles.addAll(Arrays.asList(files));
        }
      }
    }

    return getFileChanges(inputFiles);
  }
}
