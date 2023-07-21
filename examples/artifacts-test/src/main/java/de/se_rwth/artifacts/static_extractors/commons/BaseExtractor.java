/* (c) https://github.com/MontiCore/monticore */
package de.se_rwth.artifacts.static_extractors.commons;


import umlp.backendrte.common.GemIdentifiable;

import java.io.File;
import java.util.*;

/**
 *
 * The base extractor that should be extended by all new extractors.
 *
 * It gives a basic structure of how to implement new extractors.
 *
 * Also, some basic functionalities are handled like artifact collection and execution such that
 * it does not need to take care that extractors are compounded.
 *
 */

public abstract class BaseExtractor {


  /**
   * Specifies the exact behaviour of the extractor meaning how to transform the artifacts to
   * their runtime information.
   * Usually parsing etc will take place here.
   * Consider using two steps for easy extraction, such that first the objects are build in a
   * first pass and afterwards they are connected using key determining functions.
   * @param filetype2Files all found artifacts split by their artifact type
   * @return the runtime objects which should be the results of the extraction
   */
  public abstract Set<GemIdentifiable> doExtract(Map<String, Collection<RootAwareFile>> filetype2Files);

  public Set<GemIdentifiable> extractGivenRoot(List<File> rootFiles) {
    return doExtract(getFileTypeToFilesMapOfProjectInternalRoots(rootFiles));
  }


  public Map<String, Collection<RootAwareFile>> getFileTypeToFilesMapOfProjectInternalRoots(List<File> rootFiles) {
    Map<String, Collection<RootAwareFile>> fileTypeToFiles = new HashMap<>();

    ExtractorDirectoryWalker extractorDirectoryWalker = new ExtractorDirectoryWalker();

    for (File root : rootFiles) {
      Set<RootAwareFile> files = extractorDirectoryWalker.getFiles(root, false);
      fillFileTypesToFilesMap(fileTypeToFiles, files);
    }
    return fileTypeToFiles;
  }

  private void fillFileTypesToFilesMap(Map<String, Collection<RootAwareFile>> fileTypeToFiles, Set<RootAwareFile> files) {
    for (RootAwareFile file : files) {
      String extension = ExtractorUtils.splitSimpleNameAndExtension(file.getName())[ExtractorUtils.NAME_EXTENSION_POS];
      if(fileTypeToFiles.containsKey(extension)) {
        fileTypeToFiles.get(extension).add(file);
      } else {
        fileTypeToFiles.put(extension, new HashSet<RootAwareFile>(){{add(file);}});
      }
    }
  }
}
