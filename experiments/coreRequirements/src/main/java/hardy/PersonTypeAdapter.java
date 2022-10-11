package hardy;

// GEN-P

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Führt nach der Deserialisierung die Methode INTERNAL_finishDeser aus, welche die enthaltenen GemLists zu Ende
 * initialisiert.
 */
public class PersonTypeAdapter extends TypeAdapter<Person> {
  protected final TypeAdapter<Person> delegate;

  public PersonTypeAdapter(TypeAdapter<Person> delegate) {
    this.delegate = delegate;
  }

  @Override
  public void write(JsonWriter out, Person value) throws IOException {
    delegate.write(out, value);
  }

  @Override
  public Person read(JsonReader in) throws IOException {
    Person res = delegate.read(in);
    res.INTERNAL_finishDeser();
    return res;
  }
}
