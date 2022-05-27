/* (c) https://github.com/MontiCore/monticore */
package hardy.service;

import hardy.FlensburgManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class DBAccessRestService {

  @GetMapping("/dbaccess/{type}")
  public ResponseEntity<String> accessTo(@PathVariable String type) {
    String msg = null;
    switch (type) {
      case "Car":
        msg = FlensburgManager.getCarList().stream().map(p -> "" + p.getGemId())
            .collect(Collectors.joining(", "));
        break;
      case "Person":
        msg = FlensburgManager.getPersonList().stream().map(p -> p.getNickname())
            .collect(Collectors.joining(", "));
        break;
      case "Human":
        msg = FlensburgManager.getHumanList().stream().map(h -> "" + h.getGemId())
            .collect(Collectors.joining(", "));
        break;
    }

    if (msg == null || msg.equals("")) {
      msg = String.format("Keine Entitäten vom Typ \"%s\" im System enthalten!", type);
    }

    return ResponseEntity.ok(msg);
  }

}
