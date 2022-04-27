// (c) https://github.com/MontiCore/monticore
package umlp.util;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * A lazy list
 * Elements are loaded as they are required.
 * Useful if e.g. the elements are loaded over a network.
 */
public class LazyList<E> extends AbstractList<E> {

  List<Long> listOfIDs = new LinkedList<>();

  protected Map<Long, E> loadedObjects = new HashMap<>();

  @Override
  public int size() {
    return getListOfIDs().size();
  }

  @Override
  public E get(int idx) {
    assertIdxInBounds(idx);
    if(!isLoaded(idx)) {
      loadObjects(idx, idx);
    }
    return loadedObjects.get(getListOfIDs().get(idx));
  }

  @Override
  public E set(int idx, E elem) {
    assertIdxInBounds(idx);
    E oldElem = get(idx);
    loadedObjects.put(getUMLPId(elem), elem);
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

  protected boolean isLoaded(int idx){
    return isLoaded(getListOfIDs().get(idx));
  }

  protected boolean isLoaded(Long id) {
    return loadedObjects.containsKey(id);
  }

  protected void loadObjects(int start, int endIdx) {
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

  protected List<Long> getListOfIDs() {
    return listOfIDs;
  }

  protected Long getUMLPId(E elem) {
    //todo get new id? or has the element an ID? -> would require interface
    //todo implement when interface to load models is available
    return -32l;
  }
}
