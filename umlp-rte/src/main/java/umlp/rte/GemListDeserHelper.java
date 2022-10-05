package umlp.rte;

// RTE-P

import java.util.List;

/**
 * Unvollständiger dummy einer GemList, welche alle IDs zur initialisierung einer kompletten GemList enthält.
 * Wird im letzten Schritt der Deserialisierung ersetzt, siehe hardy.PersonTypeAdapter#read(com.google.gson.stream.JsonReader),
 * hardy.PersonTOP#INTERNAL_finishDeser().
 */
// TODO: no longer needed!
public class GemListDeserHelper<E extends GemIdentifiable> extends GemList<E>{
  private List<Long> initialIds;

  public GemListDeserHelper(long gemId, int size, List<Long> ids) {
    super(gemId, ids, null, null, null, null);
    this.initialIds = ids;
  }

  public List<Long> getInitialIds(){
    return initialIds;
  }
}
