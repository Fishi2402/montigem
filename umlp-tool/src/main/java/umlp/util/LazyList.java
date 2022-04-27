// (c) https://github.com/MontiCore/monticore
package umlp.util;

import groovy.lang.MetaClassImpl;

import java.util.AbstractList;
import java.util.Collections;
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
    assertIdxInBounds(idx);
    if(!isLoaded(idx)) {
      loadObjects(idx, idx);
    }
    return loadedObjects.get(idx);
  }

  @Override
  public E set(int idx, E elem) {
    assertIdxInBounds(idx);
    E oldElem = get(idx);
    loadedObjects.put(idx, elem);
    //todo implement when interface to load models is available
    return oldElem;
  }

  @Override
  public void add(int idx, E elem) {
    if (idx < 0 || idx > size()) {
      throw new IndexOutOfBoundsException();
    }
    //todo implement when interface to load models is available
  }

  @Override
  public E remove(int idx) {
    E elem = get(idx);
    //todo implement when interface to load models is available
    return elem;
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

  protected void assertIdxInBounds(int idx) {
    if (idx < 0 || idx >= size()) {
      throw new IndexOutOfBoundsException();
    }
  }
}
