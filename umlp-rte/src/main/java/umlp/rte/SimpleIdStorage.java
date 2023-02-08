/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

// Nur während der Entwicklung zu nutzten

import de.se_rwth.commons.logging.Log;
import umlp.util.LogUtil;
import umlp.util.jsweetUtils.JavaUtilUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

// Sehr simple aber langsame implementierung des IdStorage.
// Verwaltet alle IDs in einer Map und kopiert diese immer wenn IDs verschoben werden müssen.
public class SimpleIdStorage implements IdStorage{
  protected Map<Integer, Long> idxToId = new HashMap<>();

  @Override
  public Optional<Long> getId(int idx) {
    if(idxToId.containsKey(idx)){
      return Optional.of(idxToId.get(idx));
    }else {
      return Optional.empty();
    }
  }

  @Override
  public void addAll(int i, List<Long> ids) {
    Log.trace("Adding ids " + ids + " at index " + i, "IdStorage " + LogUtil.getLoggerName());
    Log.trace("Initial state: " + idxToId, "IdStorage " + LogUtil.getLoggerName());
    // Shift up all existing indicies by copying
    // This is insanely simple and slow, see ChunkedIdStorage for more efficient implementation
    Map<Integer, Long> res = new HashMap<>();
    int s = ids.size();

    for (Integer key : idxToId.keySet()) {
      Long value = idxToId.get(key);
      if(key >= i){
        res.put(key + s, value);
      }else{
        res.put(key, value);
      }
    }

    for (Long id : ids) {
      res.put(i++, id);
    }

    idxToId.clear();
    idxToId = res;
    Log.trace("End state: " + idxToId, "IdStorage " + LogUtil.getLoggerName());
  }

  @Override
  public void set(Map<Integer, Long> changes) {
    Log.trace("Changes " + changes ,"IdStorage " + LogUtil.getLoggerName());
    Log.trace("Initial state: " + idxToId, "IdStorage " + LogUtil.getLoggerName());
    idxToId.putAll(changes);
    Log.trace("Finished state: " + idxToId, "IdStorage " + LogUtil.getLoggerName());
  }

  @Override
  public void remove(Collection<Integer> indicies) {
    Log.trace("Removing indicies " + indicies, "IdStorage " + LogUtil.getLoggerName());
    Log.trace("Initial state: " + idxToId, "IdStorage " + LogUtil.getLoggerName());
    TreeMap<Integer, Integer> offsets = new TreeMap<>();

    int i = 0;
    List<Integer> sorted = new ArrayList<>(indicies);
    sorted.sort(null);
    for (Integer idx : sorted) {
      i++;
      offsets.put(idx, i);
    }

    Map<Integer, Long> res = new HashMap<>();
    for (Integer key : idxToId.keySet()) {
      Long value = idxToId.get(key);
      if(indicies.contains(key)){
        continue;
      }

      Integer floorKey = offsets.floorKey(key);
      if(floorKey != null) {
        Integer floorValue = offsets.get(floorKey);
        res.put(key - floorValue, value);
      }else{
        res.put(key, value);
      }
    }

    idxToId.clear();
    idxToId = res;
    Log.trace("Finished state: " + idxToId, "IdStorage " + LogUtil.getLoggerName());
  }

  @Override
  public boolean containsIdx(int idx) {
    return idxToId.containsKey(idx);
  }

  @Override
  public void clear() {
    Log.trace("Clearing, was " + idxToId, "IdStorage " + LogUtil.getLoggerName());
    idxToId.clear();
  }

  @Override
  public int size() {
    Log.trace("Returning size " + idxToId.size(), "IdStorage " + LogUtil.getLoggerName());
    return idxToId.size();
  }

  @Override
  public boolean removeId(long gemId) {
    Log.trace("Trying to remove gemId " + gemId, "IdStorage " + LogUtil.getLoggerName());
    int key = -1;
    for (Integer i : idxToId.keySet()) {
      Long value = idxToId.get(i);
      if(JavaUtilUtils.equals(value, gemId)){
        key = i;
        break;
      }
    }

    if(key >= 0){
      Log.trace("Removing gemId " + gemId + " at index " + key, "IdStorage " + LogUtil.getLoggerName());
      // Entfernt und verschiebt alle höheren IDs nach vorne
      remove(Collections.singleton(key));
      return true;
    }else{
      Log.trace("Cant not find index of gemId " + gemId, "IdStorage " + LogUtil.getLoggerName());
      return false;
    }
  }
}
