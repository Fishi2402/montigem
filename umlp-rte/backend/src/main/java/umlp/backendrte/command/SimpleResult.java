/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import umlp.backendrte.common.LoadingState;

public class SimpleResult<T> extends CommandResult<T> {
  protected T obj;
  protected LoadingState loadingState;

  public SimpleResult(long id, T obj, String typeParameter) {
    super(id, typeParameter);
    this.obj = obj;
    this.loadingState = LoadingState.LOADED;
  }

  public SimpleResult(long id, LoadingState loadingState, String typeParameter){
    super(id, typeParameter);
    this.loadingState = loadingState;
  }

  public T get(){
    return obj;
  }

  public boolean isSuccessful(){
    return LoadingState.LOADED.equals(loadingState);
  }

  public LoadingState getLoadingState(){
    return loadingState;
  }

  @Override
  public boolean isSimpleResult() {
    return true;
  }
}
