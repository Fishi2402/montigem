package demorec.service;

import de.se_rwth.commons.logging.Log;
import demorec.DemoRecServerApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    try {
      // Get the bytes from the MultipartFile
      byte[] fileBytes = file.getBytes();

      // Specify the file path where you want to save the uploaded file
      String givenPath;
      String filePath;
      if(path.startsWith("/")) {
        givenPath = path.replaceFirst("/", "");
      } else {
        givenPath = path;
      }
      filePath = DemoRecServerApplication.blobStorageLocation + givenPath;
      if(!filePath.endsWith("/")) {
        filePath = filePath + "/";
      }

      filePath = filePath + file.getOriginalFilename();
      File test = Paths.get(filePath).toFile();
      Log.info(test.getAbsolutePath(), this.getClass().getName());
      test.getParentFile().mkdirs();
      test.createNewFile();
      test.setWritable(true);

      // Write the bytes to the file
      try (FileOutputStream fos = new FileOutputStream(test)) {
        fos.write(fileBytes);
      }
      return ResponseEntity.ok("File uploaded successfully");
    } catch (IOException e) {
      // Handle exceptions (e.g., IOException) and return an error response
      Log.warn("Failed to upload the file because of exception: ", e);
      return ResponseEntity.status(500).body("Failed to upload the file");
    }
  }

  @GetMapping(value="/images/{imageName}")
  public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {

    // Load the image from the classpath resource
   /* Resource resource = new ClassPathResource("static/" + imageName);
    Path imagePath = resource.getFile().toPath();
    System.out.println(imagePath.toAbsolutePath());*/

    String filePath = DemoRecServerApplication.blobStorageLocation+"/"+imageName ;
    File test = Paths.get(filePath).toFile();
    Log.info(test.getAbsolutePath(), this.getClass().getName());

    // Read the image bytes
    byte[] imageBytes = Files.readAllBytes(Paths.get(filePath));

    // Set the content type and content disposition headers
    // TODO what about other Media Types?
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG); // adjust the media type based on your image type
    headers.setContentDispositionFormData("attachment", imageName);

    // Return the image bytes and headers as a ResponseEntity
    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

  }
}
