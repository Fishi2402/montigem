package selib;

import selib.gemlists.BookDescriptionGemList;
import umlp.backendrte.common.GemList;

import java.util.stream.Collectors;

public class Person extends PersonTOP {

  @Override
  public GemList<BookDescription> getRentedBookDescriptionsDerivedGemList() {
    return new BookDescriptionGemList(
        this.getRentedBooks().stream()
            .map(BookTOP::getDescription)
            .map(BookDescription::getGemId)
            .collect(Collectors.toList())
    );
  }
}
