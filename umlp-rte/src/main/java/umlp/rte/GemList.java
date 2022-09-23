// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO: Brauchen wir hier Observer, oder werden die Observer eine ebene höher genutzt?
// Sonst kann es zu Dopplung kommen, z.B. gibt es bei Person#addOwns die Methode PersonObserver#notifyAddOwns,
// welche Überschnitt mit GemListObserver#notifyAddIDs hätte.

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

  protected final static String FAILED_TO_LOAD_IDS_ERROR =
      "0xAC727 failed to load ids";

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

  protected BiConsumer<Integer, Long> setIdxFunction;

  protected BiConsumer<Integer, List<Long>> addIDsFunction;

  protected Consumer<Set<Integer>> removeIdxsFunction;

  public GemList(
      long gemID,
      Supplier<Integer> loadSizeFunction,
      BiFunction<Integer, Integer, List<Long>> loadIDsFunction,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction
  ) {
    this(
      gemID,
      loadSizeFunction,
      loadIDsFunction,
      loadObjectsFunction,
        null,
        null,
        null
    );
    this.setIdxFunction = this::_internal_set;
    this.addIDsFunction = this::_internal_add;
    this.removeIdxsFunction = this::_internal_remove;
  }

  public GemList(
      long gemId,
      Supplier<Integer> loadSizeFunction,
      BiFunction<Integer, Integer, List<Long>> loadIDsFunction,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction,
      BiConsumer<Integer, Long> setIdxFunction,
      BiConsumer<Integer, List<Long>> addIDsFunction,
      Consumer<Set<Integer>> removeIdxsFunction
  ){
    this(
        gemId,
        Collections.emptyList(),
        loadSizeFunction,
        loadIDsFunction,
        loadObjectsFunction,
        setIdxFunction,
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
      BiConsumer<Integer, Long> setIdxFunction,
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
    this.setIdxFunction = setIdxFunction;
    this.addIDsFunction = addIDsFunction;
    this.removeIdxsFunction = removeIdxsFunction;
  }

  public boolean sizeLoaded() {
    return size != UNKNOWN_SIZE;
  }

  public void ensureSizeLoaded(){
    if (!sizeLoaded()) {
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
    ensureSizeLoaded();
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
    ensureSizeLoaded();
    assertInclusiveIdxInBounds(idx);
    final E oldElem = get(idx);
    setIdxFunction.accept(idx, elem.getGemId());
    return oldElem;
  }

  public void _internal_set(int idx, long gemID) {
    ensureSizeLoaded();
    assertInclusiveIdxInBounds(idx);
    setIDs(Collections.singletonMap(idx, gemID));
  }

  @Override
  public void add(int idx, E elem) {
    addIDsFunction.accept(idx, Collections.singletonList(elem.getGemId()));
  }

  public void _internal_add(int idx, List<Long> gemIDs) {
    boolean sizeWasLoaded = sizeLoaded();
    ensureSizeLoaded();
    assertExclusiveIdxInBounds(idx);
    idStorage.addAll(idx, gemIDs);
    // TODO: AHe, FDr: Discuss and enable Tests lazyListTestAdd and lazyListTestRemove
    //if(sizeWasLoaded) {
      size += gemIDs.size();
    //}
  }

  @Override
  public E remove(int idx) {
    ensureSizeLoaded();
    E elem = get(idx);
    removeIdxsFunction.accept(Collections.singleton(idx));
    return elem;
  }

  public void _internal_remove(Set<Integer> idxs) {
    boolean sizeWasLoaded = sizeLoaded();
    ensureSizeLoaded();
    idStorage.remove(idxs);
    //if(sizeWasLoaded) {
      size -= idxs.size();
    //}
    assert size >= 0;
  }

  /**
   * returns if the element is available
   * e.g. one is authorized to access it
   * this is NOT a range check
   */
  // TODO: needed?
  public boolean isPresent(int idx) {
    ensureSizeLoaded();
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
    idStorage.set(changes);
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

  protected void loadID(int idx) {
    loadIDs(idx, idx + 1);
  }

  protected void loadIDs(int startIdx, int endIdx) {
    ensureSizeLoaded();
    assertExclusiveIdxInBounds(startIdx);
    assertExclusiveIdxInBounds(endIdx);
    List<Long> ids = loadIDsFunction.apply(startIdx, endIdx);
    if(ids == null){
      Log.error(FAILED_TO_LOAD_IDS_ERROR);
      return;
    }

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
    ensureSizeLoaded();
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

  /**
   * Removes the given gemId from the idStorage.
   * Can only be called if the idStorage contains the gemId, otherwise the size will become invalid.
   * The gemId is not required to be loaded in the idStorage, thus the result pf idStorage.removeId must not be returned
   */
  public void removeIdSure(long gemId) {
    size--;
    assert size >= 0;
    idStorage.removeId(gemId);
  }
}
