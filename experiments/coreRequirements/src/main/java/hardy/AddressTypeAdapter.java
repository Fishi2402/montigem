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
public class AddressTypeAdapter extends TypeAdapter<Address> {
  protected final TypeAdapter<Address> delegate;

  public AddressTypeAdapter(TypeAdapter<Address> delegate) {
    this.delegate = delegate;
  }

  @Override
  public void write(JsonWriter out, Address value) throws IOException {
    delegate.write(out, value);
  }

  @Override
  public Address read(JsonReader in) throws IOException {
    Address res = delegate.read(in);
    res.INTERNAL_finishDeser();
    return res;
  }
}
