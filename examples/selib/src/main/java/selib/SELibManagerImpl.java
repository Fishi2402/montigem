/* (c) https://github.com/MontiCore/monticore */
package selib;

import selib.gemlists.BookDescriptionGemList;
import selib.gemlists.BookGemList;
import selib.gemlists.PersonGemList;
import umlp.backendrte.common.GemList;
import umlp.backendrte.common.GemListBuilder;

import java.util.ArrayList;
import java.util.List;

public class SELibManagerImpl extends SELibManagerImplTOP {
  @Override
  protected GemList<Person> _doGetPersonList() {
    List<Long> ids = new ArrayList<>(this.knownPersons.keySet());
    GemListBuilder<Person> list = new PersonGemList(ids);
    return list.build().get();
  }
  @Override
  protected GemList<Book> _doGetBookList() {
    List<Long> ids = new ArrayList<>(this.knownBooks.keySet());
    GemListBuilder<Book> list = new BookGemList(ids);
    return list.build().get();
  }
  @Override
  protected GemList<BookDescription> _doGetBookDescriptionList() {
    List<Long> ids = new ArrayList<>(this.knownBookDescriptions.keySet());
    GemListBuilder<BookDescription> list = new BookDescriptionGemList(ids);
    return list.build().get();
  }
}
