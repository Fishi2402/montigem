package gradle;

import org.gradle.api.file.FileType;
import org.gradle.internal.execution.history.changes.Change;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;

import java.io.File;

public class MockFileChange implements FileChange {
  protected File f;
  protected ChangeType changeType;

  public MockFileChange(File f, ChangeType changeType){
    this.f = f;
    this.changeType = changeType;
  }

  @Override
  public File getFile() {
    return f;
  }

  @Override
  public ChangeType getChangeType() {
    return changeType;
  }

  @Override
  public FileType getFileType() {
    if(f.isFile()){return FileType.FILE;}
    if(f.isDirectory()){return FileType.DIRECTORY;}
    return FileType.MISSING;
  }

  @Override
  public String getNormalizedPath() {
    return f.getPath();
  }
}
