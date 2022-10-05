// (c) https://github.com/MontiCore/monticore
package umlp.rte.lazyloadTestUtil;

import umlp.rte.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * for testing purposes we use a List to be
 * object source instead of a manager
 */
public class ListBasedServer<E extends GemIdentifiable> extends AbstractList<E> {

  /**
   * keeps the data, acting as a server
   */
  protected List<Long> ids = new LinkedList<>();

  protected Map<Long, E> objects = new HashMap<>();

  protected Set<Long> idsNotAuthorized = new HashSet<>();

  /**
   * stored to report updates
   */
  protected List<GemList<E>> gemLists = new LinkedList<>();

  protected long nextGemListID = 2001;

  public ListBasedServer(List<E> list) {
    for (E obj : list) {
      this.getIds().add(obj.getGemId());
      this.getObjects().put(obj.getGemId(), obj);
    }
  }

  public void addKnownObjects(Collection<E> objects) {
    for (E obj : objects) {
      getObjects().put(obj.getGemId(), obj);
    }
  }

  public List<E> getList() {
    return getIds().stream()
        .map(getObjects()::get)
        .collect(Collectors.toList());
  }

  public void setIdsNotAuthorized(Set<Long> ids) {
    this.idsNotAuthorized = ids;
  }

  public GemList<E> createLazyList() {
    GemList<E> gemList = new GemList<>(
        getNextGemListID(),
        ids,
        this::getObjects,
        this::setIdxs,
        this::addIDs,
        this::removeIdxs
    );
    getLazyLists().add(gemList);
    return gemList;
  }

  public Gem<E> createGem(Long id) {
    return new Gem(id, () -> this.getObject(id));
  }

  @Override
  public int size() {
    return getIds().size();
  }

  @Override
  public E get(int idx) {
    return getObjects().get(getIds().get(idx));
  }

  @Override
  public E set(int idx, E elem) {
    E oldElem = get(idx);
    getIds().set(idx, elem.getGemId());
    getObjects().put(elem.getGemId(), elem);
    for(GemList<E> gemList : gemLists) {
      gemList._internal_set(idx, elem.getGemId());
    }
    return oldElem;
  }

  @Override
  public void add(int idx, E elem) {
    getIds().add(elem.getGemId());
    getObjects().put(elem.getGemId(), elem);
    for(GemList<E> gemList : gemLists) {
      gemList._internal_add(idx, Collections.singletonList(elem.getGemId()));
    }
  }

  @Override
  public E remove(int idx) {
    E elem = get(idx);
    getIds().remove(idx);
    for(GemList<E> gemList : gemLists) {
      gemList._internal_remove(Collections.singleton(idx));
    }
    return elem;
  }

  protected List<Long> getIDs(int startIdx, int endIdx) {
    return getIds().subList(startIdx, endIdx);
  }

  protected List<LoadResult<E>> getObjects(List<Long> ids) {
    return ids.stream()
        .map(this::getObject)
        .collect(Collectors.toList());
  }

  protected LoadResult<E> getObject(Long id) {
    if (getIdsNotAuthorized().contains(id)) {
      return new LoadResult<E>(LoadingState.NOT_AUTHORIZED);
    }
    else if (getObjects().containsKey(id)) {
      return new LoadResult<>(getObjects().get(id));
    }
    else {
      return new LoadResult<E>(LoadingState.FAILED_TO_LOAD);
    }
  }

  protected void setIdxs(int idx, long gemID) {
    getIds().set(idx, gemID);
    for(GemList<E> gemList : gemLists) {
      gemList._internal_set(idx, gemID);
    }
  }

  protected void addIDs(int idx, List<Long> ids) {
    getIds().addAll(idx, ids);
    for(GemList<E> gemList : gemLists) {
      gemList._internal_add(idx, ids);
    }
  }

  protected void removeIdxs(Set<Integer> idxs) {
    idxs
        .stream()
        .sorted(Comparator.reverseOrder())
        .forEach(idx -> ids.remove((int)idx));
    for(GemList<E> gemList : gemLists) {
      gemList._internal_remove(idxs);
    }
  }

  protected List<Long> getIds() {
    return ids;
  }

  protected Map<Long, E> getObjects() {
    return objects;
  }

  protected Set<Long> getIdsNotAuthorized() {
    return idsNotAuthorized;
  }

  protected List<GemList<E>> getLazyLists() {
    return gemLists;
  }

  protected long getNextGemListID() {
    return nextGemListID++;
  }
}
