package de.se_rwth.artifacts.static_extractors.commons;

import baseartifactstest.RootArtifact;

import java.nio.file.Path;
import java.text.SimpleDateFormat;

public class ExtractorUtils {


  public static final int SIMPLE_NAME_POS = 0;

  public static final int NAME_EXTENSION_POS = 1;
  public static final SimpleDateFormat ARTIFACTS_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  public static final String DIRECTORY_FILE_TYPE = "";

  /**
   * Make all paths unique in their appearance so only "/" is used never "\"
   * @param path with potential "\"
   * @return path without "\"
   */
  public static String transformWindowsPathToLinuxStyle(String path){
    return path.replace("\\","/");
  }

  /**
   * Make all paths unique in their appearance so only "/" is used never "\"
   * And also remove part of the root
   * @param path with potential "\" + root path
   * @param r the root
   * @return path without "\" and root path yielding a relative path inside of the root
   */
  public static String removeRootPathFromAbsolutePath(String path, RootArtifact r){
    return transformWindowsPathToLinuxStyle(path).replace(r.getFullName(), "");
  }


  public static String[] splitSimpleNameAndExtension(String fileName) {
    String[] nameAndExtension = new String[2];

    int pointFoundAt = fileName.lastIndexOf(".");
    nameAndExtension[SIMPLE_NAME_POS] = pointFoundAt >= 0 ? fileName.substring(0, pointFoundAt) : fileName;
    nameAndExtension[NAME_EXTENSION_POS] = pointFoundAt >= 0 && pointFoundAt < fileName.length() ?
        fileName.substring(pointFoundAt) :
        "";
    return nameAndExtension;
  }
}
