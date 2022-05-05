// (c) https://github.com/MontiCore/monticore
package umlptest.listTestUtil;

import umlp.util.GemIdentifiable;
import umlp.util.LazyList;
import umlp.util.LoadResult;
import umlp.util.LoadingState;

import java.util.AbstractList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * for testing purposes we use a List to be
 * object source instead of a manager
 */
public class ListBasedLazyListServer<E extends GemIdentifiable> extends AbstractList<E> {

  /**
   * keeps the data, acting as a server
   */
  protected List<Long> ids = new LinkedList<>();

  protected Map<Long, E> objects = new HashMap<>();

  protected Set<Long> idsNotAutorized = new HashSet<>();

  /**
   * stored to report updates
   */
  protected List<LazyList<E>> lazyLists = new LinkedList<>();

  public ListBasedLazyListServer(List<E> list) {
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

  public void setIdsNotAutorized(Set<Long> ids) {
    this.idsNotAutorized = ids;
  }

  public LazyList<E> createLazyList() {
    LazyList<E> lazyList = new LazyList<>(
        this::size,
        this::getIDs,
        this::getObjects,
        this::setIdxs,
        this::addIDs,
        this::removeIdxs
    );
    getLazyLists().add(lazyList);
    return lazyList;
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
    reportChange();
    return oldElem;
  }

  @Override
  public void add(int idx, E elem) {
    getIds().add(elem.getGemId());
    getObjects().put(elem.getGemId(), elem);
    reportChange();
  }

  @Override
  public E remove(int idx) {
    E elem = get(idx);
    getIds().remove(idx);
    reportChange();
    return elem;
  }

  protected void reportChange() {
    for (LazyList<E> lazyList : getLazyLists()) {
      lazyList.resetStructureCache();
    }
  }

  protected List<Long> getIDs(int startIdx, int endIdx) {
    return getIds().subList(startIdx, endIdx);
  }

  protected List<LoadResult<E>> getObjects(List<Long> ids) {
    List<LoadResult<E>> result = new LinkedList<>();
    for (long id : ids) {
      if (getIdsNotAutorized().contains(id)) {
        result.add(new LoadResult<E>(LoadingState.NOT_AUTHORIZED));
      }
      else if (getObjects().containsKey(id)) {
        result.add(new LoadResult<>(getObjects().get(id)));
      }
      else {
        result.add(new LoadResult<E>(LoadingState.FAILED_TO_LOAD));
      }
    }
    return result;
  }

  protected void setIdxs(Map<Integer, Long> idxs) {
    for (Map.Entry<Integer, Long> entry : idxs.entrySet()) {
      getIds().set(entry.getKey(), entry.getValue());
    }
    reportChange();
  }

  protected void addIDs(int idx, List<Long> ids) {
    getIds().addAll(idx, ids);
    reportChange();
  }

  protected void removeIdxs(Set<Integer> idxs) {
    List<Long> oldIds = getIds();
    ids = new LinkedList<>();
    for (int i = 0; i < oldIds.size(); i++) {
      if (!idxs.contains(i)) {
        ids.add(oldIds.get(i));
      }
    }
    reportChange();
  }

  protected List<Long> getIds() {
    return ids;
  }

  protected Map<Long, E> getObjects() {
    return objects;
  }

  protected Set<Long> getIdsNotAutorized() {
    return idsNotAutorized;
  }

  protected List<LazyList<E>> getLazyLists() {
    return lazyLists;
  }
}
