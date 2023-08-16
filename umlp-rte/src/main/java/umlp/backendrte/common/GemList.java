// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;
import jsweet.lang.Replace;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

// TODO: Brauchen wir hier Observer, oder werden die Observer eine ebene höher genutzt?
// Sonst kann es zu Dopplung kommen, z.B. gibt es bei Person#addOwns die Methode PersonObserver#notifyAddOwns,
// welche Überschnitt mit GemListObserver#notifyAddIDs hätte.

// TODO JSweet: Sollte vielleicht besser den Arrayprototypen überschreiben
// Array.prototype.remove = function (o) {
//    // code to remove "o"
//    return this;
//}

/*
 * A lazy list
 * Elements are loaded as they are required.
 * Useful if e.g. the elements are loaded over a network.
 */
public abstract class GemList<E extends GemIdentifiable>
    extends AbstractList<E>
    implements GemBuildable {

  // Lokaler speicher für die gemIds der enthaltenen Objekte. Enthält alle IDs der Assoziation.
  protected List<Long> ids;

  // TODO discuss: CBu -> wird für eine simple Spring-based DB Kommunikation benötigt
  public GemList(
      List<Long> ids
  ) {
    this.ids = new ArrayList<>(ids);
  }

  public GemList() {
    this(Collections.emptyList());
  }

  @Override
  public Optional<? extends GemList<E>> build() {
    return Optional.of((GemList<E>) this);
  }

  @Override
  public int size() {
    return ids.size();
  }

  @Override
  public E get(int idx) {
    if (isNotIdxInInclusiveBounds(idx)) {
      logIndexError("0xAC729", "get", idx);
    }
    Long id = getID(idx);
    LoadResult<E> lr = loadSingleObject(id);
    if (lr.isPresent()) {
      return lr.get();
    }
    else {
      // TODO: rework error handling?
      Log.error("Can not load Object with id " + id);
      return null;
    }
  }

  /**
   * identical to {@link java.util.List#contains(Object)} but taking a gemID
   *
   * @param gemID the id of the object that may be contained in this list
   * @return whether the object corresponding to gemID is contained in this list
   */
  public boolean contains(Long gemID) {
    return ids.contains(gemID);
  }

  /**
   * provides the objects referenced by the gemIDs if possible
   *
   * @param gemIDs reference the objects to get
   * @return the results of trying to load the objects
   */
  protected abstract List<LoadResult<E>> loadObjects(List<Long> gemIDs);

  @Override
  public E set(int idx, E elem) {
    if (isNotIdxInInclusiveBounds(idx)) {
      logIndexError("0xAC725", "set", idx);
    }
    final E oldElem = get(idx);
    _internal_setAndSync(idx, elem.getGemId());
    return oldElem;
  }

  /**
   * to be overridden by a subclass to add synchronization
   */
  protected void _internal_setAndSync(int idx, long gemID) {
    _internal_set(idx, gemID);
  }

  public void _internal_set(int idx, long gemID) {
    if (isNotIdxInInclusiveBounds(idx)) {
      logIndexError("0xAC726", "_internal_set", idx);
    }
    ids.set(idx, gemID);
  }

  @Override
  public void add(int idx, E elem) {
    _internal_addAndSync(idx, Collections.singletonList(elem.getGemId()));
  }

  /**
   * to be overridden by a subclass to add synchronization
   */
  protected void _internal_addAndSync(int idx, List<Long> gemIDs) {
    _internal_add(idx, gemIDs);
  }

  public void _internal_add(int idx, List<Long> gemIDs) {
    if (isNotIdxInExclusiveBounds(idx)) {
      logIndexError("0xAC727", "_internal_add", idx);
    }
    ids.addAll(idx, gemIDs);
  }

  @Override
  public E remove(int idx) {
    // TODO: removeIdxsFunction needed?
    E elem = get(idx);
    _internal_removeAndSync(Collections.singleton(idx));
    return elem;
  }

  /**
   * to be overridden by a subclass to add synchronization
   */
  protected void _internal_removeAndSync(Set<Integer> idxs) {
    _internal_remove(idxs);
  }

  public void _internal_remove(Set<Integer> idxs) {
    List<Integer> toSort = new ArrayList<>();
    for (Integer idx : idxs) {
      toSort.add(idx);
    }
    Collections.reverse(toSort);
    for (Integer idx : toSort) {
      ids.remove((int) idx);
    }
  }

  /**
   * returns if the element is available
   * e.g. one is authorized to access it
   * this is NOT a range check
   */
  // TODO: needed?
  public boolean isPresent(int idx) {
    if (isNotIdxInInclusiveBounds(idx)) {
      logIndexError("oxAC728", "isPresent", idx);
    }
    return loadSingleObject(getID(idx)).isPresent();
  }

  public void setIDs(Map<Integer, Long> changes) {
    for (Map.Entry<Integer, Long> e : changes.entrySet()) {
      ids.set(e.getKey(), e.getValue());
    }
  }

  protected LoadResult<E> loadSingleObject(long id) {
    List<LoadResult<E>> res = loadObjects(Collections.singletonList(id));
    if (res.isEmpty()) {
      return new LoadResult<>(LoadingState.FAILED_TO_LOAD);
    }

    return res.get(0);
  }

  protected boolean isNotIdxInInclusiveBounds(int idx) {
    return idx < 0 || idx >= size();
  }

  protected boolean isNotIdxInExclusiveBounds(int idx) {
    return idx < 0 || idx > size();
  }

  protected void logIndexError(String errorCode, String methodName, int idx) {
    Log.error(
        errorCode +
            " GemList::" + methodName + " index out of bounds" +
            ", size: " + size() +
            ", idx: " + idx
    );
    throw new IndexOutOfBoundsException();
  }

  protected void assertExclusiveIdxInBounds(int idx) {
    if (idx < 0 || idx > size()) {
      throw new IndexOutOfBoundsException();
    }
  }

  public Long getID(int idx) {
    return getIDs(idx, idx + 1).get(0);
  }

  // Wird intern genutzt, um ids nachzuladen.
  // Muss public sein, damit im server die entsprechenden ids an den client zu schicken(auf Anfrage)
  public List<Long> getIDs(int startIdx, int endIdx) {
    return ids.subList(startIdx, endIdx);
  }

  public List<Long> getIDs() {
    return ids;
  }

  /**
   * returns the gemID of a given element
   */
  protected Long getID(E elem) {
    return elem.getGemId();
  }

  /**
   * Removes the given gemId from the idStorage.
   * Can only be called if the idStorage contains the gemId, otherwise the size will become invalid.
   * The gemId is not required to be loaded in the idStorage, thus the result of idStorage.removeId must not be returned
   */
  public void removeIdSure(long gemId) {
    ids.removeIf(l -> Objects.equals(l, gemId));
  }

  public int indexOfId(long gemId) {
    return ids.indexOf(gemId);
  }
}
