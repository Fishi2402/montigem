package de.se_rwth.artifacts.static_extractors.commons;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DirectoryWalker gets all files (including nested directories) from a directory
 */
public class ExtractorDirectoryWalker extends DirectoryWalker {

  @Override
  protected boolean handleDirectory(File directory, int depth, Collection results)
      throws IOException {
    results.add(directory);
    return super.handleDirectory(directory, depth, results);
  }

  @Override
  protected void handleFile(final File file, final int depth, final Collection results) throws IOException {
    results.add(file);
  }

  /**
   * Getting all Files under a root. Making them root aware to be able to store information of
   * the corresponding root.
   * @param root under which all artifacts should be searched
   * @param external is the root an external one
   * @return
   */
  public Set<RootAwareFile> getFiles(File root, boolean external) {
    Set<File> files = new HashSet<>();

    try {
      walk(root, files);
    }
    catch (IOException e) {
      Log.warn("Problem finding files!", e);
    }

    // Extra information about the root necessary for ContainerFileExtractor
    return files.stream().map(f -> new RootAwareFile(f, root, external)).collect(Collectors.toSet());
  }

}
