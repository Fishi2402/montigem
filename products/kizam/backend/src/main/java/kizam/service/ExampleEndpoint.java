package kizam.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleEndpoint {

  @GetMapping(value="/helloWorld")
  public ResponseEntity<String> helloWorld() {
      return ResponseEntity.status(400).body("Hello World!");
  }
}
