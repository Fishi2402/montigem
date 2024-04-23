package demorec.service;

import de.se_rwth.commons.logging.Log;
import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import demorec.DemoRecServerApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Optional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class BlobEndpoint {

  @PostMapping(
      value="/blob")
  public ResponseEntity<String> storeBlob(@RequestPart("file") MultipartFile file,
                                          @RequestHeader("PATH") String path) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please provide a file to upload");
    }
    Optional<DemoRecComponent> component = resolveDemoRecComponent(path);

    if(component.isEmpty()) {
      return ResponseEntity.badRequest().body("Please save the component before uploading a file.");
    }

    try {
      // Get the bytes from the MultipartFile
      byte[] fileBytes = file.getBytes();

      // Specify the file path where you want to save the uploaded file
      String givenPath;
      if(path.startsWith("/")) {
        givenPath = path.replaceFirst("/", "");
      } else {
        givenPath = path;
      }
      if(!givenPath.endsWith("/")) {
        givenPath= givenPath+"/";
      }
      String photoLocation = givenPath+file.getOriginalFilename();
      photoLocation = photoLocation.replaceAll("[^\\w.-]", "_");
      String pathOnFS = DemoRecServerApplication.blobStorageLocation + photoLocation;

      File image = Paths.get(pathOnFS).toFile();

      if(image.exists() && !image.isDirectory()) {
        Log.info("Name Clash "+photoLocation, this.getClass().getName());
        if(checkIfFileAlreadyExists(file, image.getPath())) {
          Log.info("File '"+photoLocation+"' was uploaded beforehand.", this.getClass().getName());
          // TODO -> do not store the file on the system again, but add it to the component if it was not added before
          if(!component.get().getPhotoLocationsList().contains(photoLocation)) {
            Log.info("Adding '"+photoLocation+"' to "+component.get().getGemId(), this.getClass().getName());
            component.get().addPhotoLocations(component.get().sizePhotoLocations(), photoLocation);
          }
          return ResponseEntity.status(400).body("File "+path+" was already uploaded.");
        } else { // Avoid name clashes by postfixing current time to the fileName
          String namePostFix = "_"+ java.time.LocalDateTime.now();
          image = Paths.get(pathOnFS+namePostFix).toFile();
          photoLocation += namePostFix;
        }
      }

      Log.info(image.getAbsolutePath(), this.getClass().getName());
      image.getParentFile().mkdirs();
      image.createNewFile();
      image.setWritable(true);

      // Write the bytes to the file
      try (FileOutputStream fos = new FileOutputStream(image)) {
        fos.write(fileBytes);
        component.get().addPhotoLocations(component.get().sizePhotoLocations(), photoLocation);
        Log.info(String.join(", ",component.get().getPhotoLocationsList()), this.getClass().getSimpleName());
        Log.info(photoLocation, this.getClass().getSimpleName());
      }
      return ResponseEntity.ok(null);
    } catch (IOException e) {
      // Handle exceptions (e.g., IOException) and return an error response
      Log.warn("Failed to upload the file because of exception: ", e);
      return ResponseEntity.status(500).body("Internal Server Error: Failed to upload " +path);
    }
  }

  private Optional<DemoRecComponent> resolveDemoRecComponent(String originalFileName) {
    Log.info(originalFileName, this.getClass().getSimpleName());
    String[] splitArray = originalFileName.split("_");
    String gemIdStringRepr = splitArray[splitArray.length-1];
    Log.info(gemIdStringRepr, this.getClass().getSimpleName());
    try{
      return DemoRecManager.demoRecComponentOpt(Long.parseLong(gemIdStringRepr));
    } catch(NumberFormatException e) {
      return Optional.empty();
    }
  }

  @GetMapping(value="/images/**")
  public ResponseEntity<byte[]> getImage(HttpServletRequest request) throws IOException {
    String imagePath = new AntPathMatcher().extractPathWithinPattern(
        request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),request.getRequestURI());

    Log.info(
        "Requested Image from Backend:"+ imagePath, this.getClass().getName()
    );

    String[] splitResult = imagePath.split("images/");

    imagePath = String.join("",Arrays.copyOfRange(splitResult, 1, splitResult.length));


    Log.info(
        "Requested Image from Backend:"+ imagePath, this.getClass().getName()
    );

    // Load the image from the classpath resource
   /* Resource resource = new ClassPathResource("static/" + imageName);
    Path imagePath = resource.getFile().toPath();
    System.out.println(imagePath.toAbsolutePath());*/

    String filePath = DemoRecServerApplication.blobStorageLocation+"/"+imagePath ;
    Log.info(filePath, this.getClass().getName());
    File test = Paths.get(filePath).toFile();
    Log.info(test.getAbsolutePath(), this.getClass().getName());

    if(!test.exists()) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Read the image bytes
    byte[] imageBytes = Files.readAllBytes(Paths.get(filePath));

    // Set the content type and content disposition headers
    // TODO what about other Media Types?
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG); // adjust the media type based on your image type
    headers.setContentDispositionFormData("attachment", imagePath);

    // Return the image bytes and headers as a ResponseEntity
    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

  }


  public static boolean checkIfFileAlreadyExists(MultipartFile multipartFile, String filePath) throws IOException {
    // Read content of MultipartFile
    byte[] multipartBytes = multipartFile.getBytes();

    // Read content of file
    Path path = Paths.get(filePath);
    byte[] fileBytes = Files.readAllBytes(path);

    // Compare byte arrays
    return MessageDigest.isEqual(multipartBytes, fileBytes);
  }
}
