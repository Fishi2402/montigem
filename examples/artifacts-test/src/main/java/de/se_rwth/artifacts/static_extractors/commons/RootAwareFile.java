package de.se_rwth.artifacts.static_extractors.commons;


import java.io.File;

/**
 * File which also stored the info of which file is its root
 */
public class RootAwareFile extends File {

  private File root;
  private boolean isRoot = false;
  private boolean isExternal;

  public RootAwareFile(File file, File root, boolean external) {
    super(file.getAbsolutePath());
    this.root = root;
    isRoot = (file.equals(root));
    this.isExternal = external;
  }

  public File getRoot() {
    return root;
  }

  public boolean isRoot() {
    return isRoot;
  }

  public boolean isExternal() {return isExternal;}

}