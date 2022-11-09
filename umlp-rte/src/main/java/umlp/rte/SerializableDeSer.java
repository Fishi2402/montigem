package umlp.rte;

import umlp.command.DeSer;

import java.io.*;
import java.util.Base64;
import java.util.Optional;

public class SerializableDeSer implements DeSer<Serializable> {

  @Override
  public String serialize(Serializable obj) {
    try(
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)
    ) {
      oos.writeObject(obj);
      oos.flush();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      // TODO: error handling
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Serializable> deserialize(String str) {
    try(
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(str));
        ObjectInputStream ois = new ObjectInputStream(bais)
    ){
      return Optional.ofNullable((Serializable) ois.readObject());
    } catch (IOException | ClassNotFoundException e) {
      // TODO: error logging
      return Optional.empty();
    }
  }
}
