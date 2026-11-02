package coveragedashboard.util;

import jsweet.lang.Erased;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

public class ResourcePathHelper {

  public interface PathFunction<R>{
    @Erased
    R apply(Path path) throws IOException;
  }

  private ResourcePathHelper(){
  }


  @Erased
  public static <R> Optional<R> withPath(String rawPath, PathFunction<R> action) throws IOException{
    if(rawPath == null || rawPath.isBlank()){
      return Optional.empty();
    }

    URI uri = parseUriOrNull(rawPath);
    String scheme = (uri != null) ? uri.getScheme() : null;

    if("jar".equalsIgnoreCase(scheme)){
      return executeInJar(uri, action);
    }

    Path path = (uri != null && "file".equalsIgnoreCase(scheme))
        ? Path.of(uri).normalize()
        : Path.of(rawPath).normalize();
    return Optional.ofNullable(action.apply(path));
  }

  @Erased
  private static URI parseUriOrNull(String rawPath){
    try{
      String normalized = rawPath.replace('\\', '/');
      if(normalized.matches("^[a-zA-Z]:/.*")){
        return Path.of(normalized).toUri();
      }
      return URI.create(normalized);
    } catch (Exception e) {
      return null;
    }
  }

  @Erased
  private static <R> Optional<R> executeInJar(URI uri, PathFunction<R> action) throws IOException {
    try{
      Path path = Path.of(uri).normalize();
      return Optional.ofNullable(action.apply(path));
    } catch (FileSystemNotFoundException e) {
      // 1. Try opening the URI directly on the FS
      // Create and keep the filesystem only for the executed action alive
      try(FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())){
        Path path = Path.of(uri).normalize();
        return Optional.ofNullable(action.apply(path));
      }catch (IOException ioException){
        // 2. Fallback, Host path in JAR URI does not exist (e.g. Docker or moved TMP DIR)
        String str = uri.toString();
        int bang = str.indexOf("!");
        if(bang != -1){
          String innerPath = str.substring(bang + 2);
          java.net.URL url = ResourcePathHelper.class.getClassLoader().getResource(innerPath);
          if(url != null){
            URI classpathURI = URI.create(url.toString().replace(" ", "%20"));
            try(FileSystem fs = FileSystems.newFileSystem(classpathURI, Collections.emptyMap())){
              return Optional.ofNullable(action.apply(Path.of(classpathURI)));
            }
          }
          throw ioException;
        }
        return Optional.empty();
      }
    }
  }

}
