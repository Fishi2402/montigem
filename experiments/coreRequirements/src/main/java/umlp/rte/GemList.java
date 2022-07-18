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

  // TODO: replace with efficient impl, maybe ChunkedIdStorage
  // Lokaler speicher für die gemIds der enthaltenen Objekte. Speichert nur aktuell benötigte ids.
  protected IdStorage idStorage = new SimpleIdStorage();

  protected Supplier<Integer> loadSizeFunction;

  protected BiFunction<Integer, Integer, List<Long>> loadIDsFunction;

  protected Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction;

  protected Consumer<Map<Integer, Long>> setIdxsFunction;

  protected BiConsumer<Integer, List<Long>> addIDsFunction;

  protected Consumer<Set<Integer>> removeIdxsFunction;

  public GemList(
      long gemId,
      Supplier<Integer> loadSizeFunction,
      BiFunction<Integer, Integer, List<Long>> loadIDsFunction,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction,
      Consumer<Map<Integer, Long>> setIdxsFunction,
      BiConsumer<Integer, List<Long>> addIDsFunction,
      Consumer<Set<Integer>> removeIdxsFunction
  ){
    this(
        gemId,
        Collections.emptyList(),
        loadSizeFunction,
        loadIDsFunction,
        loadObjectsFunction,
        setIdxsFunction,
        addIDsFunction,
        removeIdxsFunction
    );
  }

  public GemList(
      long gemId,
      List<Long> initialIds,
      Supplier<Integer> loadSizeFunction,
      BiFunction<Integer, Integer, List<Long>> loadIDsFunction,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction,
      Consumer<Map<Integer, Long>> setIdxsFunction,
      BiConsumer<Integer, List<Long>> addIDsFunction,
      Consumer<Set<Integer>> removeIdxsFunction
  ) {
    this.gemID = gemId;
    if(!initialIds.isEmpty()) {
      idStorage.addAll(0, initialIds);
    }

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

  public void ensureSizeLoaded(){
    if (size == UNKNOWN_SIZE) {
      size = loadSizeFunction.get();
    }
  }

  @Override
  public int size() {
    ensureSizeLoaded();
    return size;
  }

  @Override
  public E get(int idx) {
    assertInclusiveIdxInBounds(idx);
    Long id = getID(idx);
    LoadResult<E> lr = loadSingleObject(id);
    if(lr.isPresent()){
      return lr.get();
    }else{
      // TODO: rework error handling?
      Log.error("Can not load Object with id " + id);
      return null;
    }
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
  // TODO: needed?
  public boolean isPresent(int idx) {
    assertInclusiveIdxInBounds(idx);
    ensureIdLoaded(idx);
    return loadSingleObject(getID(idx)).isPresent();
  }

  protected void ensureIdLoaded(int idx) {
    if(!idStorage.containsIdx(idx)){
      loadID(idx);
    }
  }

  public void setIDs(Map<Integer, Long> changes) {
    setIdxsFunction.accept(changes);
    idStorage.set(changes);
  }

  public void addIDs(int idx, List<Long> additions) {
    ensureSizeLoaded();
    idStorage.addAll(idx, additions);
    addIDsFunction.accept(idx, additions);
    size += additions.size();
  }

  public void removeIdxs(Set<Integer> idxs) {
    ensureSizeLoaded();
    idStorage.remove(idxs);
    removeIdxsFunction.accept(idxs);
    size -= idxs.size();
  }

  protected boolean isLoaded(int idx) {
    return isLoaded(getID(idx));
  }

  // TODO: why is this needed?
  protected boolean isLoaded(long id) {
    return loadSingleObject(id).isPresent();
  }

  protected LoadResult<E> loadSingleObject(long id) {
    List<LoadResult<E>> res = loadObjectsFunction.apply(Collections.singletonList(id));
    if(res.isEmpty()){
      return new LoadResult<>(LoadingState.FAILED_TO_LOAD);
    }

    return res.get(0);
  }

  protected boolean isLoadedID(int idx) {
    return idStorage.containsIdx(idx);
  }

  protected void loadObject(int idx) {

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
    Map<Integer, Long> idx2IDMap = new HashMap<>();
    for (int idx = 0; idx < endIdx - startIdx; idx++) {
      idx2IDMap.put(idx + startIdx, ids.get(idx));
    }
    idStorage.set(idx2IDMap);
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

  // Wird intern genutzt, um ids nachzuladen.
  // Muss public sein, damit im server die entsprechenden ids an den client zu schicken(auf Anfrage)
  public List<Long> getIDs(int startIdx, int endIdx) {
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
      // TODO: bulk operation
      Optional<Long> idOpt = idStorage.getId(idx);
      if(idOpt.isPresent()) {
        ids.add(idOpt.get());
      }else{
        // TODO: Better error handling
        throw new IllegalStateException("Can not find id for index " + idx);
      }
    }
    return ids;
  }

  /**
   * to be called (by the manager) if the list has been changed
   * on the side of the server
   */
  public void resetStructureCache() {
    size = UNKNOWN_SIZE;
    idStorage.clear();
  }

  public void resetCache() {
    resetStructureCache();
  }

  /**
   * returns the gemID of a given element
   */
  protected Long getID(E elem) {
    return elem.getGemId();
  }

  @Override
  public long getGemId() {
    return gemID;
  }

  public int sizeLocallyKnownIds(){
    return idStorage.size();
  }

  // Pseudo-build that does nothing
  public Optional<GemList<E>> build(){
    return Optional.of(this);
  }
}
