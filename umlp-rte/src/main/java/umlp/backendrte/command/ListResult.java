package umlp.backendrte.command;

import java.util.List;

public class ListResult<U, T extends List<U>> extends CommandResult<U> {
  protected T list;
  protected boolean successful;

  private ListResult(long commandId, T list, String innerTypeParameter, boolean successful) {
    super(commandId, innerTypeParameter);
    this.list = list;
    this.successful = successful;
  }
  
  public static <U, V extends List<U>> ListResult<U, V> of(long commandId, V value, String innerTypeParameter){
    return new ListResult<>(commandId, value, innerTypeParameter, true);
  }

  public static <U, V extends List<U>> ListResult<U, V> error(long commandId, String innerTypeParameter){
    return new ListResult<U, V>(commandId, null, innerTypeParameter, false);
  }

  public T get(){
    return list;
  }

  public boolean isSuccessful() {
    return successful;
  }

    @Override
  public boolean isListResult() {
    return true;
  }
}
