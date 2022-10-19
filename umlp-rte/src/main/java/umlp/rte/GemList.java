// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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

  protected long gemID;

  // Lokaler speicher für die gemIds der enthaltenen Objekte. Enthält alle IDs der Assoziation.
  protected List<Long> ids;

  protected Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction;

  protected BiConsumer<Integer, Long> setIdxFunction;

  protected BiConsumer<Integer, List<Long>> addIDsFunction;

  protected Consumer<Set<Integer>> removeIdxsFunction;

  public GemList(
      long gemID,
      List<Long> ids,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction
  ) {
    this(gemID, ids, loadObjectsFunction, null, null, null);
    this.setIdxFunction = this::_internal_set;
    this.addIDsFunction = this::_internal_add;
    this.removeIdxsFunction = this::_internal_remove;
  }

  public GemList(
      long gemId,
      List<Long> ids,
      Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction,
      BiConsumer<Integer, Long> setIdxFunction,
      BiConsumer<Integer, List<Long>> addIDsFunction,
      Consumer<Set<Integer>> removeIdxsFunction
  ) {
    this.gemID = gemId;
    this.ids = new ArrayList<>(ids);
    this.loadObjectsFunction = loadObjectsFunction;
    this.setIdxFunction = setIdxFunction;
    this.addIDsFunction = addIDsFunction;
    this.removeIdxsFunction = removeIdxsFunction;
  }

  @Override
  public int size() {
    return ids.size();
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
    setIdxFunction.accept(idx, elem.getGemId());
    return oldElem;
  }

  public void _internal_set(int idx, long gemID) {
    assertInclusiveIdxInBounds(idx);
    setIDs(Collections.singletonMap(idx, gemID));
  }

  @Override
  public void add(int idx, E elem) {
    addIDsFunction.accept(idx, Collections.singletonList(elem.getGemId()));
  }

  public void _internal_add(int idx, List<Long> gemIDs) {
    assertExclusiveIdxInBounds(idx);
    ids.addAll(idx, gemIDs);
  }

  @Override
  public E remove(int idx) {
    // TODO: removeIdxsFunction needed?
    E elem = get(idx);
    removeIdxsFunction.accept(Collections.singleton(idx));
    return elem;
  }

  public void _internal_remove(Set<Integer> idxs) {
    idxs
        .stream()
        .sorted(Comparator.reverseOrder())
        .forEach(idx -> ids.remove((int)idx));
  }

  /**
   * returns if the element is available
   * e.g. one is authorized to access it
   * this is NOT a range check
   */
  // TODO: needed?
  public boolean isPresent(int idx) {
    assertInclusiveIdxInBounds(idx);
    return loadSingleObject(getID(idx)).isPresent();
  }

  public void setIDs(Map<Integer, Long> changes) {
    for (Map.Entry<Integer, Long> e : changes.entrySet()) {
      ids.set(e.getKey(), e.getValue());
    }
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
    return ids.subList(startIdx, endIdx);
  }

  public List<Long> getIDs(){
    return Collections.unmodifiableList(ids);
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

  // Pseudo-build that does nothing
  public Optional<GemList<E>> build(){
    return Optional.of(this);
  }

  /**
   * Removes the given gemId from the idStorage.
   * Can only be called if the idStorage contains the gemId, otherwise the size will become invalid.
   * The gemId is not required to be loaded in the idStorage, thus the result of idStorage.removeId must not be returned
   */
  public void removeIdSure(long gemId) {
    System.out.println(ids);
    ids.removeIf(l -> Objects.equals(l, gemId));
  }

  public void setGemId(long gemListId) {
    this.gemID = gemListId;
  }
}
