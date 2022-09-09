package hardy;

// GEN-P

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

// Sorgt dafür, dass der PersonTypeAdapter genutzt wird, wenn Gson eine Person (de)serialisiert.
public class PersonTypeAdapterFactory implements TypeAdapterFactory {
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if(!Person.class.isAssignableFrom(type.getRawType())) {
      return null;
    }

    return (TypeAdapter<T>) new PersonTypeAdapter((TypeAdapter<Person>)gson.getDelegateAdapter(this, type));
  }
}
