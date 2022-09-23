package umlp.rte;

// RTE-P

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


// GemList, welche bei der Erstellung von Objekten benutzt werden muss, da sie in diesem Zustand
// noch nicht mit anderen Teilnehmen synchronisiert werden kann.
public class GemListBuilder<E extends GemIdentifiable> extends GemList<E> {
  protected transient boolean buildFinished = false;
  protected List<Long> ids = new ArrayList<>();
  // Da einem in einem Builder andere Builder hinzugefügt werden können und der FlensburgManager diese
  // nicht kennt, also auch nicht per id laden kann, muss hier eine lokale Verwaltung stattfinden.
  // Nach build() wird diese geleert und nicht weiter verwendet.
  protected Map<Integer, E> idxToBuilder = new HashMap<>();

  public GemListBuilder(long gemID, Supplier<Integer> loadSizeFunction, BiFunction<Integer, Integer, List<Long>> loadIDsFunction, Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction) {
    super(gemID, loadSizeFunction, loadIDsFunction, loadObjectsFunction);
  }

  public Optional<GemList<E>> build(){
    if(buildFinished){
      return Optional.of(this);
    }

    idStorage.clear();
    for (int i = 0; i < ids.size(); i++) {
      Long id = ids.get(i);
      idStorage.set(Collections.singletonMap(i, id));
    }
    size = ids.size();

    buildFinished = true;

    // Von hier an werden alle Element vom FlensburgManager geladen
    idxToBuilder.clear();

    return Optional.of(this);
  }

  @Override
  public void ensureSizeLoaded() {
    if(buildFinished) {
      super.ensureSizeLoaded();
    } else {
      if(size == UNKNOWN_SIZE){
        size = 0;
      }
    }
  }

  @Override
  public int size() {
    if(buildFinished){
      return super.size();
    } else {
      return ids.size();
    }
  }

  @Override
  protected void ensureIdLoaded(int idx) {
    if(buildFinished){
      super.ensureIdLoaded(idx);
    } else {
      assertExclusiveIdxInBounds(idx);
    }
  }

  @Override
  public E get(int idx) {
    if(buildFinished) {
      return super.get(idx);
    } else {
      // List might contain builders, which a normal GemList will not
      // Thus, a local data storage is needed
      if(idxToBuilder.containsKey(idx)){
        return idxToBuilder.get(idx);
      } else {
        return super.get(idx);
      }
    }
  }

  @Override
  public void add(int idx, E elem) {
    if(!buildFinished) {
      idxToBuilder.put(idx, elem);
    }
    super.add(idx, elem);
  }

  @Override
  public void setIDs(Map<Integer, Long> changes) {
    if(buildFinished) {
      super.setIDs(changes);
    } else {
      changes.forEach((idx, id) -> ids.set(idx, id));
    }
  }

  @Override
  public void _internal_add(int idx, List<Long> additions) {
    if(buildFinished) {
      super._internal_add(idx, additions);
    } else {
      ids.addAll(idx, additions);
    }
  }

  @Override
  public void _internal_remove(Set<Integer> idxs) {
    if(buildFinished) {
      super._internal_remove(idxs);
    } else {
      idxs
          .stream()
          .sorted(Comparator.reverseOrder())
          .forEach(idx -> ids.remove((int) idx));
    }
  }

  @Override
  protected boolean isLoadedID(int idx) {
    if(buildFinished) {
      return super.isLoadedID(idx);
    } else {
      return idx < ids.size();
    }
  }

  @Override
  protected void loadIDs(int startIdx, int endIdx) {
    if(buildFinished) {
      super.loadIDs(startIdx, endIdx);
    } else {
      throw new IllegalStateException("Can not load IDs in GemListBuilder!");
    }
  }

  @Override
  public List<Long> getIDs(int startIdx, int endIdx) {
    if(buildFinished) {
      return super.getIDs(startIdx, endIdx);
    } else {
      return ids.subList(startIdx, endIdx);
    }
  }

  @Override
  protected void loadID(int idx) {
    if(buildFinished) {
      super.loadID(idx);
    } else {
      throw new IllegalStateException("Can not load id in builder!");
    }
  }
}
