package demorec.service;

import de.se_rwth.commons.logging.Log;
import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import demorec.DemoRecServerApplication;
import demorec.Photo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@RestController
public class BlobEndpoint {

  @PostMapping(
      value="/blob")
  public ResponseEntity<String> storeBlob(@RequestPart("file") MultipartFile file,
                                          @RequestHeader("name") String name) {
    Log.info("Received Image", this.getClass().getName());
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please provide a file to upload");
    }

    Optional<Photo> photoOpt = Optional.empty();

    try {
      // Get the bytes from the MultipartFile
      byte[] fileBytes = file.getBytes();

      // Specify the file path where you want to save the uploaded file
      File imageOnServerFileSystem;
      String fileName;

      // Avoid double file names
      do {
        fileName = calculateSystemInternalFileName();
        imageOnServerFileSystem = Paths.get( DemoRecServerApplication.blobStorageLocation + "/" + fileName).toFile();
      } while(imageOnServerFileSystem.exists());

      synchronized (BlobEndpoint.class) {
        photoOpt = DemoRecManager.photoBuilder()
            .name(name)
            .filename(fileName)
            .size((long) fileBytes.length).build();
      }

      if(photoOpt.isEmpty()) {
        return ResponseEntity.status(500).body("Internal Server Error: Upload failed " +name);
      }

      Log.info(imageOnServerFileSystem.getAbsolutePath(), this.getClass().getName());
      imageOnServerFileSystem.getParentFile().mkdirs();
      imageOnServerFileSystem.createNewFile();
      imageOnServerFileSystem.setWritable(true);

      // Write the bytes to the file
      try (FileOutputStream fos = new FileOutputStream(imageOnServerFileSystem)) {
        fos.write(fileBytes);
        fos.flush();
        fos.close();
        Log.info("Created "+fileName +" on Server.", this.getClass().getSimpleName());
      }
      return ResponseEntity.ok("{\"gemId\": " + photoOpt.get().getGemId() + "}");
    } catch (IOException | NoSuchAlgorithmException e) {
      // Handle exceptions (e.g., IOException) and return an error response
      Log.warn("Failed to upload the file because of exception: ", e);
      return ResponseEntity.status(500).body("Internal Server Error: Failed to upload " +name);
    }
  }

  /**
   * Calculate random hash for Resource Download Link
   * @return Random Hash used for the download link.
   * @throws NoSuchAlgorithmException
   */
  private String calculateSystemInternalFileName() throws NoSuchAlgorithmException {
    SecureRandom random = new SecureRandom();
    byte[] randBytes = new byte[64];
    random.nextBytes(randBytes);
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(randBytes);
    byte[] hash = md.digest(randBytes);
    return Base64.getUrlEncoder().encodeToString(hash);
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
