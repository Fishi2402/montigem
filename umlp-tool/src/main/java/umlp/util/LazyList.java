// (c) https://github.com/MontiCore/monticore
package umlp.util;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

/*
 * A lazy list
 * Elements are loaded as they are required.
 * Useful if e.g. the elements are loaded over a network.
 */
public class LazyList<E> extends AbstractList<E> {

  protected Map<Integer, E> loadedObjects = new HashMap<>();

  protected Integer sizeOfList;

  @Override
  public int size() {
    return sizeOfList;
  }

  @Override
  public E get(int idx) {
    if(!isLoaded(idx)) {
      loadObjects(idx, idx);
    }
    return loadedObjects.get(idx);
  }

  protected boolean isLoaded(int idx) {
    return loadedObjects.containsKey(idx);
  }

  protected void loadObjects(int startIdx, int endIdx) {
    //todo implement when interface to load models is available
  }

  protected void loadObjects() {
    loadObjects(0, size() - 1);
  }

}
