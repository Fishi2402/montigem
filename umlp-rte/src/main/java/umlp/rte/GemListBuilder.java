package umlp.rte;

// RTE-P

import java.util.*;
import java.util.function.Function;


// GemList, welche bei der Erstellung von Objekten benutzt werden muss, da sie in diesem Zustand
// noch nicht mit anderen Teilnehmen synchronisiert werden kann.
public class GemListBuilder<E extends GemIdentifiable> extends GemList<E> {
  protected transient boolean buildFinished = false;
  // TODO: needed?
  protected List<Long> builderIds = new ArrayList<>();
  // Da einem in einem Builder andere Builder hinzugefügt werden können und der FlensburgManager diese
  // nicht kennt, also auch nicht per id laden kann, muss hier eine lokale Verwaltung stattfinden.
  // Nach build() wird diese geleert und nicht weiter verwendet.
  protected Map<Integer, E> idxToBuilder = new HashMap<>();

  public GemListBuilder(Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction) {
    this(loadObjectsFunction, Collections.emptyList());
  }

  public GemListBuilder(Function<List<Long>, List<LoadResult<E>>> loadObjectsFunction, List<Long> initialIds) {
    super(initialIds, loadObjectsFunction);
  }

  public Optional<GemList<E>> build(){
    if(buildFinished){
      return Optional.of(this);
    }

    ids.clear();
    ids.addAll(builderIds);

    buildFinished = true;

    // Von hier an werden alle Element vom FlensburgManager geladen
    idxToBuilder.clear();

    return Optional.of(this);
  }

  @Override
  public int size() {
    if(buildFinished){
      return super.size();
    } else {
      return builderIds.size();
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
      changes.forEach((idx, id) -> builderIds.set(idx, id));
    }
  }

  @Override
  public void _internal_add(int idx, List<Long> additions) {
    if(buildFinished) {
      super._internal_add(idx, additions);
    } else {
      builderIds.addAll(idx, additions);
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
          .forEach(idx -> builderIds.remove((int) idx));
    }
  }

  @Override
  public List<Long> getIDs(int startIdx, int endIdx) {
    if(buildFinished) {
      return super.getIDs(startIdx, endIdx);
    } else {
      return builderIds.subList(startIdx, endIdx);
    }
  }

  @Override
  public List<Long> getIDs() {
    if(buildFinished){
      return super.getIDs();
    } else {
      return Collections.unmodifiableList(builderIds);
    }
  }
}
