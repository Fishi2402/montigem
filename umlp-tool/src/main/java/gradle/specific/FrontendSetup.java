/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.StreamSupport;

public abstract class FrontendSetup extends DefaultTask {

  public static final String FRONTEND_CONFIG = "frontend/config/";

  @OutputDirectory
  public abstract DirectoryProperty getOutput();

  @InputFiles
  @Optional
  @Incremental
  public abstract DirectoryProperty getInput();

  @TaskAction
  public void run(InputChanges changes) throws IOException, URISyntaxException {
    if(getInput().isPresent()) {
      // Delete files in output, if file in input was deleted
      StreamSupport.stream(changes.getFileChanges(getInput()).spliterator(), false)
          .filter(ch -> ch.getChangeType().equals(ChangeType.REMOVED))
          .forEach(ch -> {
            String filename = getInput().getAsFile().get().toPath().relativize(ch.getFile().toPath()).toString();
            FileUtils.deleteQuietly(getOutput().file(filename).get().getAsFile());
          });
    }

    // First Copy Files from Resources of UMLP-Tool
    for(String filename: getResourceListing(this.getClass(), FRONTEND_CONFIG)){
      InputStream file = this.getClass().getClassLoader().getResourceAsStream(FRONTEND_CONFIG + filename);
      FileUtils.copyInputStreamToFile(file, getOutput().file(filename).get().getAsFile());
    }

    if(getInput().isPresent()) {
      // Then Copy User-Defined Files
      for (File f : getInput().getAsFileTree()) {
        String filename = getInput().getAsFile().get().toPath().relativize(f.toPath()).toString();
        FileUtils.copyFile(f, getOutput().file(filename).get().getAsFile());
      }
    }
  }


  /**
   * List directory contents for a resource folder. Not recursive.
   * This is basically a brute-force implementation.
   * Works for regular files and also JARs.
   *
   * @author Greg Briggs
   * @param clazz Any java class that lives in the same place as the resources you want.
   * @param path Should end with "/", but not start with one.
   * @return Just the name of each member item, not the full paths.
   * @throws URISyntaxException
   * @throws IOException
   */
  String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
    URL dirURL = clazz.getClassLoader().getResource(path);
    if (dirURL != null && dirURL.getProtocol().equals("file")) {
      /* A file path: easy enough */
      return new File(dirURL.toURI()).list();
    }

    if (dirURL == null) {
      /*
       * In case of a jar file, we can't actually find a directory.
       * Have to assume the same jar as clazz.
       */
      String me = clazz.getName().replace(".", "/")+".class";
      dirURL = clazz.getClassLoader().getResource(me);
    }

    if (dirURL.getProtocol().equals("jar")) {
      /* A JAR path */
      String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
      JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
      Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
      Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
      for (Iterator<JarEntry> it = entries.asIterator(); it.hasNext(); ) {
        JarEntry entry = it.next();
        if(!entry.isDirectory()) {
          String name = entry.getName();
          if (name.startsWith(path)) { //filter according to the path
            result.add(name.substring(path.length()));
          }
        }
      }
      return result.toArray(new String[result.size()]);
    }

    throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
  }
}
