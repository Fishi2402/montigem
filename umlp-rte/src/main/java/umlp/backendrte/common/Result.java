/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

public class Result <T> {
  private final T value;
  protected boolean successful;
  protected Result(T value, boolean successful) {
    this.value = value;
    this.successful = successful;
  }

  public static <T> Result<T> ok(T value){
    return new Result(value, true);
  }

  public static <T> Result error(){
    return new Result(null, false);
  }

  public T getValue() {
    return value;
  }

  public boolean wasSuccessful(){
    return successful;
  }

  public boolean wasUnsuccessful(){
    return !successful;
  }
}
