/* (c) https://github.com/MontiCore/monticore */

// TODO AGe
/*
export class SerializableDeSer implements DeSer<any> {

  public serialize(obj: Serializable): String {
    let baos: ByteArrayOutputStream = new ByteArrayOutputStream();
    let oos: ObjectOutputStream = new ObjectOutputStream(baos)
    oos.writeObject(obj);
    oos.flush();
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }

  public deserialize(str: String): Serializable {
    let bais: ByteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(str));
    let ois: ObjectInputStream = new ObjectInputStream(bais)
    return <Serializable>ois.readObject();
  }
}
/**/