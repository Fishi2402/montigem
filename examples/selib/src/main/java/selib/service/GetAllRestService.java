/* (c) https://github.com/MontiCore/monticore */
package selib.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import selib.BookDeSer;
import selib.BookDescriptionDeSer;
import selib.PersonDeSer;
import selib.SELibManager;

import java.util.stream.Collectors;

@RestController
public class GetAllRestService {
  @GetMapping(value = "/getall/{type}")
  @CrossOrigin
  public ResponseEntity<String> getAll(@PathVariable String type) {
    System.out.println(type);
    String msg = "";
    switch (type) {
      case "Person":
        msg = SELibManager.getPersonList().stream().map(obj -> new PersonDeSer().serialize(obj))
            .collect(Collectors.joining(", "));
        break;
      case "Book":
        msg = SELibManager.getBookList().stream().map(obj -> new BookDeSer().serialize(obj))
            .collect(Collectors.joining(", "));
        break;
      case "BookDescription":
        msg = SELibManager.getBookDescriptionList().stream().map(obj -> new BookDescriptionDeSer().serialize(obj))
            .collect(Collectors.joining(", "));
        break;
    }

    if (msg == null || msg.equals("")) {
      msg = String.format("");
    }

    System.out.println(msg);

    return ResponseEntity.ok(String.format("[%s]", msg));
  }
}
