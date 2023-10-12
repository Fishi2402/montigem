package umlp;

import de.monticore.io.paths.GlobExpressionEvaluator;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MCPathUtil {

  public static List<URL> getAllModels(MCPath path, String pathRegex){
    List<URL> res = new ArrayList<>();

    for (Path entry : path.getEntries()) {
      if(entry.toString().endsWith(".jar") || entry.toString().endsWith(".zip")){
        GlobExpressionEvaluator inJar = new GlobExpressionEvaluator(pathRegex, MCPath.getJarFS(entry.toFile()), true);
        res.addAll(toUrls(inJar.evaluate(new File("/"))));
      }else{
        File file = entry.toFile();
        if(file.exists()){
          GlobExpressionEvaluator outOfJar = new GlobExpressionEvaluator(pathRegex, FileSystems.getDefault(), false);
          res.addAll(toUrls(outOfJar.evaluate(file)));
        }
      }
    }

    return res;
  }

  protected static List<URL> toUrls(Collection<URI> uris){
    return uris.stream().map(uri -> {
      try {
        return uri.toURL();
      } catch (MalformedURLException e) {
        Log.error("Can not turn uri into url " + uri);
        return null;
      }
    }).collect(Collectors.toList());
  }

}
