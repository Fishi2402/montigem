// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.*;

/*
 * A lazy list
 * Elements are loaded as they are required.
 * Useful if e.g. the elements are loaded over a network.
 */
public class GemList<E extends GemIdentifiable> extends AbstractList<E>
    implements GemIdentifiable {

  protected final static String UNEXPECTED_LIST_LENGTH_ERROR =
      "0xAC724 recieved list of unexpected length";

  protected final static String ELEMENT_NOT_AVAILABLE_ERROR =
      "0xAC726 reqested element has not been loaded";

  protected final static int UNKNOWN_SIZE = -32;

  protected final static int INITIAL_LOAD_SIZE = 10;

  protected long gemID = -32l;

  protected int size = UNKNOWN_SIZE;

  protected Map<Integer, Long> idx2IDMap = new HashMap<>();

  protected Map<Long, LoadResult<E>> id2ObjectMap = new HashMap<>();

  protected Supplier<Integer> loadSizeFunction;

  protected BiFunction<Integer, Integer, List<Long>> loadIDsFunction;

  protected Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction;

  protected Consumer<Map<Integer, Long>> setIdxsFunction;

  protected BiConsumer<Integer, List<Long>> addIDsFunction;

  protected Consumer<Set<Integer>> removeIdxsFunction;

  public GemList(
      Supplier<Integer> loadSizeFunction,
      BiFunction<Integer, Integer, List<Long>> loadIDsFunction,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction,
      Consumer<Map<Integer, Long>> setIdxsFunction,
      BiConsumer<Integer, List<Long>> addIDsFunction,
      Consumer<Set<Integer>> removeIdxsFunction
  ) {
    this.loadSizeFunction = loadSizeFunction;
    this.loadIDsFunction = loadIDsFunction;
    this.loadObjectsFunction = loadObjectsFunction;
    this.setIdxsFunction = setIdxsFunction;
    this.addIDsFunction = addIDsFunction;
    this.removeIdxsFunction = removeIdxsFunction;
    //initial load
    //alternative: accept size + first few objects in constructor

    // TODO: Not possible in current initialization order, gemId is needed to exec loadSizeFunction
    // new PersonBuilder()
    //  -> initPerson()
    //    -> createOwns()
    //      -> new GemList()
    //      -> (size() needs Person.gemId)
    //    =>
    //  => Person.gemId = gemId
    //int loadSize = Math.min(size(), INITIAL_LOAD_SIZE);
    //loadObjects(0, loadSize);
  }

  @Override
  public int size() {
    if (size == UNKNOWN_SIZE) {
      size = loadSizeFunction.get();
    }
    return size;
  }

  @Override
  public E get(int idx) {
    assertInclusiveIdxInBounds(idx);
    if (!isPresent(idx)) {
      Log.error(ELEMENT_NOT_AVAILABLE_ERROR);
    }
    return id2ObjectMap.get(getID(idx)).get();
  }

  @Override
  public E set(int idx, E elem) {
    assertInclusiveIdxInBounds(idx);
    final E oldElem = get(idx);
    setIDs(Collections.singletonMap(idx, getID(elem)));
    return oldElem;
  }

  @Override
  public void add(int idx, E elem) {
    assertExclusiveIdxInBounds(idx);
    addIDs(idx, Collections.singletonList(getID(elem)));
  }

  @Override
  public E remove(int idx) {
    E elem = get(idx);
    removeIdxs(Collections.singleton(idx));
    return elem;
  }

  /**
   * returns if the element is available
   * e.g. one is authorized to access it
   * this is NOT a range check
   */
  public boolean isPresent(int idx) {
    assertInclusiveIdxInBounds(idx);
    if (!isLoaded(idx)) {
      loadObject(idx);
    }
    return isLoaded(idx)
        && id2ObjectMap.get(getID(idx)).isPresent();
  }

  public void setIDs(Map<Integer, Long> changes) {
    setIdxsFunction.accept(changes);
  }

  public void addIDs(int idx, List<Long> additions) {
    addIDsFunction.accept(idx, additions);
  }

  public void removeIdxs(Set<Integer> idxs) {
    removeIdxsFunction.accept(idxs);
  }

  protected boolean isLoaded(int idx) {
    return isLoaded(getID(idx));
  }

  protected boolean isLoaded(long id) {
    return id2ObjectMap.containsKey(id);
  }

  protected boolean isLoadedID(int idx) {
    return idx2IDMap.containsKey(idx);
  }

  protected void loadObject(int idx) {
    loadObjects(idx, idx + 1);
  }

  protected void loadObjects(int startIdx, int endIdx) {
    List<Long> ids = getIDs(startIdx, endIdx);
    List<LoadResult<E>> objects = loadObjectsFunction.apply(ids);
    if (ids.size() != objects.size()) {
      Log.error(UNEXPECTED_LIST_LENGTH_ERROR);
    }

    Iterator<Long> idIt = ids.iterator();
    Iterator<LoadResult<E>> objIt = objects.iterator();
    while (idIt.hasNext() && objIt.hasNext()) {
      id2ObjectMap.put(idIt.next(), objIt.next());
    }
  }

  protected void loadID(int idx) {
    loadIDs(idx, idx + 1);
  }

  protected void loadIDs(int startIdx, int endIdx) {
    assertExclusiveIdxInBounds(startIdx);
    assertExclusiveIdxInBounds(endIdx);
    List<Long> ids = loadIDsFunction.apply(startIdx, endIdx);
    if (ids.size() != endIdx - startIdx) {
      Log.error(UNEXPECTED_LIST_LENGTH_ERROR);
    }
    for (int idx = 0; idx < endIdx - startIdx; idx++) {
      idx2IDMap.put(idx + startIdx, ids.get(idx));
    }
  }

  protected void assertInclusiveIdxInBounds(int idx) {
    if (idx < 0 || idx >= size()) {
      throw new IndexOutOfBoundsException();
    }
  }

  protected void assertExclusiveIdxInBounds(int idx) {
    if (idx < 0 || idx > size()) {
      throw new IndexOutOfBoundsException();
    }
  }

  protected Long getID(int idx) {
    return getIDs(idx, idx + 1).get(0);
  }

  protected List<Long> getIDs(int startIdx, int endIdx) {
    assertExclusiveIdxInBounds(startIdx);
    assertExclusiveIdxInBounds(endIdx);
    boolean allLoaded = true;
    for (int idx = startIdx; idx < endIdx; idx++) {
      if (!isLoadedID(idx)) {
        allLoaded = false;
      }
    }
    if (!allLoaded) {
      loadIDs(startIdx, endIdx);
    }
    List<Long> ids = new LinkedList<>();
    for (int idx = startIdx; idx < endIdx; idx++) {
      ids.add(idx2IDMap.get(idx));
    }
    return ids;
  }

  /**
   * to be called (by the manager) if the list has been changed
   * on the side of the server
   */
  public void resetStructureCache() {
    size = UNKNOWN_SIZE;
    idx2IDMap = new HashMap<>();
  }

  public void resetCache() {
    resetStructureCache();
    id2ObjectMap = new HashMap<>();
  }

  /**
   * returns the gemID of a given element
   */
  protected Long getID(E elem) {
    return elem.getGemId();
  }

  // TODO AHe: Why do we need this?
  @Override
  public long getGemId() {
    return gemID;
  }
}
