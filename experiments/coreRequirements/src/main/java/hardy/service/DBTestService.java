package hardy.service;

import hardy.FlensburgManagerImpl;
import hardy.Human;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// This is a simple test for DB Access
// Feel Free to remove it, once other Commands are also accessing the DB
@RestController
public class DBTestService {
  @GetMapping("/dbtest/createHuman/")
  public ResponseEntity<String> createHuman() {
    FlensburgManagerImpl.init();

    Optional<Human> optHuman = FlensburgManagerImpl.humanBuilder()
        .age(0)
        .build();

    if(optHuman.isPresent()) {
      return ResponseEntity.ok("id:" + optHuman.get().getGemId());
    } else {
      return ResponseEntity.status(400).body("Error Building Human");
    }
  }

  @GetMapping("/dbtest/increaseAge/{id}")
  public ResponseEntity<String> increaseAge(@PathVariable long id) {
    FlensburgManagerImpl.init();

    System.err.println("Got a Request with ID " + id);
    // FlensburgManagerImpl.init();
    Human h = FlensburgManagerImpl.getHumanSure(id);
    h.setAge(h.getAge() + 1);
    return ResponseEntity.ok("success");
  }

  @GetMapping("/dbtest/getAge/{id}")
  public ResponseEntity<String> getAge(@PathVariable long id) {
    FlensburgManagerImpl.init();

    return ResponseEntity.ok("age:" + FlensburgManagerImpl.getHumanSure(id).getAge());
  }
}
