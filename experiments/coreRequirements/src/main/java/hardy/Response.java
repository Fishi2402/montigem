/* (c) https://github.com/MontiCore/monticore */

package hardy;

/**
 * Created by Alexander on 25.06.2015.
 */
public class Response<T> {
  private T object;

  public enum Type {
    SUCCESS,
    FAILURE;
  }

  private Type type;

  public Response(Type t) {
    this.type = t;
  }

  public Type getType() {
    return type;
  }

  public void setObject(T o) {
    this.object = o;
  }

  public T getObject() {
    return this.object;
  }
  
  public String toString(){
	  return type.toString() + ": " + object;
  }
}
