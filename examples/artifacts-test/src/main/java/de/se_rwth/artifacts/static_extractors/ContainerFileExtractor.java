/* (c) https://github.com/MontiCore/monticore */
package de.se_rwth.artifacts.static_extractors;

import baseartifactstest.*;
import de.se_rwth.artifacts.static_extractors.commons.BaseExtractor;
import de.se_rwth.artifacts.static_extractors.commons.ExtractorUtils;
import de.se_rwth.artifacts.static_extractors.commons.RootAwareFile;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;
import umlp.backendrte.common.GemIdentifiable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static de.se_rwth.artifacts.static_extractors.commons.ExtractorUtils.*;

public class ContainerFileExtractor extends BaseExtractor {

  private Map<Directory, List<String>> directoryToSubArtifactsMap = new HashMap<>();

  private List<RootArtifact> roots = new ArrayList<>();

  private Map<String, Artifact> absolutePathToArtifact = new HashMap<>();

  private RootArtifact defaultRoot;

  BaseArtifactsTestManager manager;

  public ContainerFileExtractor(BaseArtifactsTestManager manager) {
    this.manager = manager;
  }

  /**
   * This method does the extraction for roots, directories and fileartifacts i.e., creating their object representations and
   * furthermore connecting them with each other.
   * @param filetype2Files
   * @return
   */
  @Override
  public Set<GemIdentifiable> doExtract(Map<String, Collection<RootAwareFile>> filetype2Files) {
    java.util.Set<GemIdentifiable> runtimes = new HashSet<>();

    roots = createRoots(filetype2Files);
    runtimes.addAll(roots);

    List<Artifact> artifacts = createBaseArtifactsWithoutConnectedDirectories(filetype2Files);

    fillAbsolutePathToArtifactMap(absolutePathToArtifact, artifacts);

    runtimes.addAll(getAllFullyBuiltFileArtifacts(artifacts));
    runtimes.addAll(connectEveryDirectoryToItsSubArtifacts(filetype2Files));

    return runtimes;
  }

  private void fillAbsolutePathToArtifactMap(Map<String, Artifact> stringArtifactMap, List<Artifact> artifacts) {
    artifacts.forEach(a -> stringArtifactMap.put(a.getRoot().getFullName()+"/"+a.getFullName(), a));
  }

  private List<FileArtifact> getAllFullyBuiltFileArtifacts(List<Artifact> artifacts) {
    return artifacts.stream().filter(a -> a instanceof FileArtifact).map(fa -> (FileArtifact)fa).collect(Collectors.toList());
  }

  private List<Directory> connectEveryDirectoryToItsSubArtifacts(Map<String, Collection<RootAwareFile>> filetype2Files) {
    List<Directory> connectedDirectories = new ArrayList<>();

    if(filetype2Files.get(DIRECTORY_FILE_TYPE) != null) {
      return connectedDirectories;
    }
    Collection<RootAwareFile> directorySystemRepresentations = filetype2Files.get(DIRECTORY_FILE_TYPE);

    HashMap<Directory, List<File>> directoriesToSubArtifactsInSystemRepresentation =
        getDirectoryObjectToSubSystemFiles(directorySystemRepresentations);

    directoriesToSubArtifactsInSystemRepresentation.entrySet().forEach(
        e -> connectedDirectories.add(connectDirectoryToSubArtifactObjects(e)));

    return connectedDirectories;
  }

  private HashMap<Directory, List<File>> getDirectoryObjectToSubSystemFiles(Collection<RootAwareFile> directoriesSystemRepresentation) {
    HashMap<Directory, List<File>> directoriesToSubArtifactsInSystemRepresentation = new HashMap();

    directoriesSystemRepresentation.forEach(dir -> directoriesToSubArtifactsInSystemRepresentation.put(
        getDirectoryOfFile(dir), Arrays.stream(dir.listFiles()).collect(Collectors.toList())));
    return directoriesToSubArtifactsInSystemRepresentation;
  }

  private Directory getDirectoryOfFile(File f) {
    return (Directory) absolutePathToArtifact.get(ExtractorUtils.transformWindowsPathToLinuxStyle(f.getAbsolutePath()));
  }
  private Directory connectDirectoryToSubArtifactObjects(Map.Entry<Directory, List<File>> directoryToSubSystemArtifacts) {
    Directory directory = directoryToSubSystemArtifacts.getKey();

    directoryToSubSystemArtifacts.getValue().forEach(f ->
        directory.addContains(absolutePathToArtifact.get(ExtractorUtils.transformWindowsPathToLinuxStyle(f.getAbsolutePath()))));
    return directory;
  }

  /**
   * Method creates all directories and fileartifact objects and links from directories to files are remembered in subartifacts
   * @param filetype2Files
   * @return
   */
  private List<Artifact> createBaseArtifactsWithoutConnectedDirectories(Map<String, Collection<RootAwareFile>> filetype2Files) {
    ArrayList<Artifact> artifacts = new ArrayList<>();

    for(String fileType : filetype2Files.keySet()){
      Collection<RootAwareFile> files = filetype2Files.get(fileType);
      if (fileType.equals(DIRECTORY_FILE_TYPE)) {
        artifacts.addAll(createDirectories(files));
      }
      else {
        artifacts.addAll(createFileArtifacts(files));
      }
    }
    return artifacts;
  }

  private List<FileArtifact> createFileArtifacts(Collection<RootAwareFile> files) {
    ArrayList<FileArtifact> fileArtifacts = new ArrayList<>();

    for(RootAwareFile file : files) {
      fileArtifacts.add(createFullyConnnectedFileArtifact(file));
    }
    return fileArtifacts;
  }

  private FileArtifact createFullyConnnectedFileArtifact(RootAwareFile file) {
    FileArtifact fileArtifactObject = createFileArtifactObject(file,
        ExtractorUtils.removeRootPathFromAbsolutePath(file.getAbsolutePath(), getRootOfPath(file.getAbsolutePath())));
    linkArtifactWithItsRoot(file, fileArtifactObject);
    return fileArtifactObject;
  }

  private List<RootArtifact> createRoots(Map<String, Collection<RootAwareFile>> filetype2Files) {
    ArrayList<RootArtifact> roots = new ArrayList<>();

    // Every kind of File could be a Root
    for(String fileType : filetype2Files.keySet()){
      for(RootAwareFile f : filetype2Files.get(fileType)) {
        if (f.isRoot()) {
          roots.add(createRoot(f));
        }
      }
    }
    return roots;
  }

  private RootArtifact createRoot(RootAwareFile f) {
    RootArtifactBuilder rootBuilder = BaseArtifactsTestManager.rootArtifactBuilder();

    return rootBuilder.isExternal(f.isExternal())
        .fullName(ExtractorUtils.transformWindowsPathToLinuxStyle(f.getAbsolutePath()))
        .name(FilenameUtils.getBaseName(f.getAbsolutePath()))
        .build().orElse(null);
  }

  /**
   * Create directories and store possible contains relations for later to resolve them when all
   * Directories are present
   * @param dirs all Directory "files"
   */
  private List<Directory> createDirectories(Collection<RootAwareFile> dirs){
    List<Directory> directories = new ArrayList<>();

    for(RootAwareFile dir : dirs){
      directories.add(createDirectoryAndLinkWithRoot(dir));
    }
    return directories;
  }

  private Directory createDirectoryAndLinkWithRoot(RootAwareFile dir) {
    Directory directoryObject =
        createDirectoryObject(dir, ExtractorUtils.removeRootPathFromAbsolutePath(dir.getAbsolutePath(), getRootOfPath(dir.getAbsolutePath())));
    linkArtifactWithItsRoot(dir, directoryObject);
    return directoryObject;
  }

  private void linkArtifactWithItsRoot(RootAwareFile systemRepresentation, Artifact artifactRepresentation) {
    RootArtifact root = getRootOfPath(systemRepresentation.getAbsolutePath());

    artifactRepresentation.setRoot(root);

    if (systemRepresentation.isRoot()) { // if current dir is root we set the Directory Object as artifact representation
      root.addArtifactRepresentation(artifactRepresentation);
    } else { // set Directory object as descendent of root
      root.addDescendant(artifactRepresentation);
    }
  }

  private FileArtifact createFileArtifactObject(RootAwareFile file, String nameStartingFromRoot) {
    FileArtifactBuilder fileArtifactBuilder = BaseArtifactsTestManager.fileArtifactBuilder();

    String[] simpleNameAndExtension = ExtractorUtils.splitSimpleNameAndExtension(file.getName());

    return fileArtifactBuilder.fullName(nameStartingFromRoot)
        .nameExtension(simpleNameAndExtension[NAME_EXTENSION_POS])
        .simpleName(simpleNameAndExtension[SIMPLE_NAME_POS])
        .modified(ExtractorUtils.ARTIFACTS_DATE_FORMAT.format(file.lastModified()))
        .build().orElse(null);
  }

  private RootArtifact getRootOfPath(String absolutePathOfArtifact) {
    Optional<RootArtifact> rootOpt = roots.stream().filter(r ->
        r.getFullName().equals(ExtractorUtils.transformWindowsPathToLinuxStyle(absolutePathOfArtifact))).findFirst();
    if(rootOpt.isEmpty()) {
      Log.error("No root object with path "+ExtractorUtils.transformWindowsPathToLinuxStyle(absolutePathOfArtifact)
          + " was created.");
      return getDefaultRoot();
    } else {
      return rootOpt.get();
    }
  }

  private List<String> getFilesContainedByDirectoryAsStringPath(RootAwareFile dir) {
    return Arrays.stream(dir.listFiles())
        .map(f -> ExtractorUtils.transformWindowsPathToLinuxStyle(f.getAbsolutePath()))
        .collect(Collectors.toList());
  }

  private Directory createDirectoryObject(RootAwareFile dir, String nameStartingFromRoot) {
    DirectoryBuilder directoryBuilder = BaseArtifactsTestManager.directoryBuilder();

    return directoryBuilder.simpleName(dir.getName())
        .nameExtension("Folder")
        .modified(ExtractorUtils.ARTIFACTS_DATE_FORMAT.format(dir.lastModified()))
        .name(dir.getName())
        .fullName(nameStartingFromRoot)
        .build().orElse(null);
  }

  private RootArtifact getDefaultRoot() {
    if(defaultRoot == null) {
      RootArtifactBuilder rootBuilder = BaseArtifactsTestManager.rootArtifactBuilder();
      defaultRoot = rootBuilder.isExternal(false).fullName("DEFAULT_ERROR_ROOT").name("").build().orElse(null);
    }
    return this.defaultRoot;
  }
}
