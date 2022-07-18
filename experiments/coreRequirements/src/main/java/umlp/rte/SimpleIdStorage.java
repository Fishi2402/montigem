package umlp.rte;

// Nur während der Entwicklung zu nutzten

import de.se_rwth.commons.logging.Log;

import java.util.*;

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
    Log.trace("Adding ids " + ids + " at index " + i, "IdStorage");
    Log.trace("Initial state: " + idxToId, "IdStorage");
    // Shift up all existing indicies by copying
    // This is insanely simple and slow, see ChunkedIdStorage for more efficient implementation
    Map<Integer, Long> res = new HashMap<>();
    int s = ids.size();

    for (Map.Entry<Integer, Long> e : idxToId.entrySet()) {
      if(e.getKey() >= i){
        res.put(e.getKey() + s, e.getValue());
      }else{
        res.put(e.getKey(), e.getValue());
      }
    }

    for (Long id : ids) {
      res.put(i++, id);
    }

    idxToId.clear();
    idxToId = res;
    Log.trace("End state: " + idxToId, "IdStorage");
  }

  @Override
  public void set(Map<Integer, Long> changes) {
    Log.trace("Changes " + changes ,"IdStorage");
    Log.trace("Initial state: " + idxToId, "IdStorage");
    idxToId.putAll(changes);
    Log.trace("Finished state: " + idxToId, "IdStorage");
  }

  @Override
  public void remove(Collection<Integer> indicies) {
    TreeMap<Integer, Integer> offsets = new TreeMap<>();

    int i = 0;
    List<Integer> sorted = new ArrayList<>(indicies);
    sorted.sort(null);
    for (Integer idx : sorted) {
      i++;
      offsets.put(idx, i);
    }

    Map<Integer, Long> res = new HashMap<>();
    for (Map.Entry<Integer, Long> e : idxToId.entrySet()) {
      if(indicies.contains(e.getKey())){
        continue;
      }

      Map.Entry<Integer, Integer> floorEntry = offsets.floorEntry(e.getKey());
      if(floorEntry != null) {
        res.put(e.getKey() - floorEntry.getValue(), e.getValue());
      }else{
        res.put(e.getKey(), e.getValue());
      }
    }

    idxToId.clear();
    idxToId = res;
  }

  @Override
  public boolean containsIdx(int idx) {
    return idxToId.containsKey(idx);
  }

  @Override
  public void clear() {
    idxToId.clear();
  }

  @Override
  public int size() {
    return idxToId.size();
  }
}
