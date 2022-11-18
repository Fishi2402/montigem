// (c) https://github.com/MontiCore/monticore
package umlp.rte.lazyloadTestUtil;

import umlp.rte.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * for testing purposes we use a List to be
 * object source instead of a manager
 */
public class ListBasedServer extends AbstractList<Elem> {

  /**
   * keeps the data, acting as a server
   */
  protected List<Long> ids = new LinkedList<>();

  protected Map<Long, Elem> objects = new HashMap<>();

  protected Set<Long> idsNotAuthorized = new HashSet<>();

  /**
   * stored to report updates
   */
  protected List<GemList<Elem>> gemLists = new LinkedList<>();

  public ListBasedServer(List<Elem> list) {
    for (Elem obj : list) {
      this.getIds().add(obj.getGemId());
      this.getObjects().put(obj.getGemId(), obj);
    }
  }

  public void addKnownObjects(Collection<Elem> objects) {
    for (Elem obj : objects) {
      getObjects().put(obj.getGemId(), obj);
    }
  }

  public List<Elem> getList() {
    return getIds().stream()
        .map(getObjects()::get)
        .collect(Collectors.toList());
  }

  public void setIdsNotAuthorized(Set<Long> ids) {
    this.idsNotAuthorized = ids;
  }

  public GemList<Elem> createLazyList() {
    GemList<Elem> gemList = new ElemGemList(ids, this);
    getLazyLists().add(gemList);
    return gemList;
  }

  public Gem<Elem> createGem(Long id) {
    return new Gem(id, () -> this.getObject(id));
  }

  @Override
  public int size() {
    return getIds().size();
  }

  @Override
  public Elem get(int idx) {
    return getObjects().get(getIds().get(idx));
  }

  @Override
  public Elem set(int idx, Elem elem) {
    Elem oldElem = get(idx);
    getIds().set(idx, elem.getGemId());
    getObjects().put(elem.getGemId(), elem);
    for (GemList<Elem> gemList : gemLists) {
      gemList._internal_set(idx, elem.getGemId());
    }
    return oldElem;
  }

  @Override
  public void add(int idx, Elem elem) {
    getIds().add(elem.getGemId());
    getObjects().put(elem.getGemId(), elem);
    for (GemList<Elem> gemList : gemLists) {
      gemList._internal_add(idx, Collections.singletonList(elem.getGemId()));
    }
  }

  @Override
  public Elem remove(int idx) {
    Elem elem = get(idx);
    getIds().remove(idx);
    for (GemList<Elem> gemList : gemLists) {
      gemList._internal_remove(Collections.singleton(idx));
    }
    return elem;
  }

  protected List<Long> getIDs(int startIdx, int endIdx) {
    return getIds().subList(startIdx, endIdx);
  }

  protected List<LoadResult<Elem>> getObjects(List<Long> ids) {
    return ids.stream()
        .map(this::getObject)
        .collect(Collectors.toList());
  }

  protected LoadResult<Elem> getObject(Long id) {
    if (getIdsNotAuthorized().contains(id)) {
      return new LoadResult<Elem>(LoadingState.NOT_AUTHORIZED);
    }
    else if (getObjects().containsKey(id)) {
      return new LoadResult<>(getObjects().get(id));
    }
    else {
      return new LoadResult<Elem>(LoadingState.FAILED_TO_LOAD);
    }
  }

  protected void setIdx(int idx, long gemID) {
    getIds().set(idx, gemID);
    for (GemList<Elem> gemList : gemLists) {
      gemList._internal_set(idx, gemID);
    }
  }

  protected void addIDs(int idx, List<Long> ids) {
    getIds().addAll(idx, ids);
    for (GemList<Elem> gemList : gemLists) {
      gemList._internal_add(idx, ids);
    }
  }

  protected void removeIdxs(Set<Integer> idxs) {
    idxs
        .stream()
        .sorted(Comparator.reverseOrder())
        .forEach(idx -> ids.remove((int) idx));
    for (GemList<Elem> gemList : gemLists) {
      gemList._internal_remove(idxs);
    }
  }

  protected List<Long> getIds() {
    return ids;
  }

  protected Map<Long, Elem> getObjects() {
    return objects;
  }

  protected Set<Long> getIdsNotAuthorized() {
    return idsNotAuthorized;
  }

  protected List<GemList<Elem>> getLazyLists() {
    return gemLists;
  }

}
