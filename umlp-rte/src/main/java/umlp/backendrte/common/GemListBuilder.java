/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

// RTE-P

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// GemList, welche bei der Erstellung von Objekten benutzt werden muss, da sie in diesem Zustand
// noch nicht mit anderen Teilnehmen synchronisiert werden kann.
public abstract class GemListBuilder<E extends GemIdentifiable & GemBuildable> extends GemList<E> {
  protected transient boolean buildFinished = false;

  // Da einem in einem Builder andere Builder hinzugefügt werden können und der FlensburgManager diese
  // nicht kennt, also auch nicht per id laden kann, muss hier eine lokale Verwaltung stattfinden.
  // Nach build() wird diese geleert und nicht weiter verwendet.
  protected List<IDOrBuilder<E>> builderElemStorage = new ArrayList<>();

  public GemListBuilder(List<Long> initialIds) {
    for(long gemID : initialIds) {
      this.builderElemStorage.add(new IDOrBuilder(gemID));
    }
  }

  @Override
  public Optional<GemList<E>> build(){
    if(buildFinished){
      return Optional.of(this);
    }

    ids.clear();
    for(IDOrBuilder<E> element : builderElemStorage) {
      ids.add(element.getIDAndMaybeBuild());
    }

    buildFinished = true;

    // Von hier an werden alle Element vom FlensburgManager geladen
    builderElemStorage.clear();

    return Optional.of(this);
  }

  @Override
  public boolean isBuildFinished() {
    return buildFinished;
  }

  @Override
  public int size() {
    if(buildFinished){
      return super.size();
    } else {
      return builderElemStorage.size();
    }
  }

  @Override
  public E get(int idx) {
    if(buildFinished) {
      return super.get(idx);
    } else {
      // List might contain builders, which a normal GemList will not
      // Thus, a local data storage is needed
      if(builderElemStorage.get(idx).isPresentBuilder()){
        return builderElemStorage.get(idx).getBuilder();
      } else {
        return super.get(idx);
      }
    }
  }

  @Override
  public void add(int idx, E elem) {
    if(buildFinished) {
      super.add(idx, elem);
    } else {
      builderElemStorage.add(idx, new IDOrBuilder<>(elem));
    }
  }

  @Override
  public void _internal_set(int idx, long gemID) {
    if(buildFinished) {
      super._internal_set(idx, gemID);
    } else {
      builderElemStorage.set(idx, new IDOrBuilder<>(gemID));
    }
  }

  @Override
  public void _internal_add(int idx, List<Long> additions) {
    if(buildFinished) {
      super._internal_add(idx, additions);
    } else {
      List<IDOrBuilder<E>> newElems = new ArrayList<>();
      for (Long e : additions) {
        IDOrBuilder<E> eidOrBuilder = new IDOrBuilder<>(e);
        newElems.add(eidOrBuilder);
      }
      builderElemStorage.addAll(idx, newElems);
    }
  }

  @Override
  public void _internal_remove(Set<Integer> idxs) {
    if(buildFinished) {
      super._internal_remove(idxs);
    } else {
      List<Integer> idxsReversed = new ArrayList<>();
      for (Integer integer : idxs) {
        idxsReversed.add(integer);
      }
      Collections.reverse(idxsReversed);
      for(int idx : idxsReversed) {
        builderElemStorage.remove(idx);
      }
    }
  }

  @Override
  public List<Long> getIDs(int startIdx, int endIdx) {
    if(buildFinished) {
      return super.getIDs(startIdx, endIdx);
    } else {
      List<Long> ids = new ArrayList<>();
      for(int idx = startIdx; idx < endIdx; idx++) {
        if(builderElemStorage.get(idx).isPresentID()) {
          ids.add(builderElemStorage.get(idx).getID());
        } else {
          //todo decide on error handling
          ids.add(-1l);
        }
      }
      return ids;
    }
  }

  @Override
  public List<Long> getIDs() {
    if(buildFinished){
      return super.getIDs();
    } else {
      return getIDs(0, builderElemStorage.size());
    }
  }
}
