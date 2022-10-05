package umlp.rte;

// GEN-T
// GEN-P: Demos mit FlensburgMock


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

// Eine Implementierung der GemList, welche alle IDs lokal hält.
// Wird in FlensburgMock genutzt, da es keine weitere Instanz(wie eine Datenbank) gibt, welche
// die ids verwaltet.
public class MockGemList<E extends GemIdentifiable> extends GemList<E> {
  protected Function<Long, LoadResult<E>> loadSingleObjectFunction;

  public MockGemList(long gemID, List<Long> ids,
      Function<Long, LoadResult<E>> loadSingleObjectFunction
  ) {
    super(gemID, ids, null);
    this.loadSingleObjectFunction = loadSingleObjectFunction;
    loadObjectsFunction = this::doLoadObjects;
  }

  public MockGemList(
      long gemID,
      List<Long> ids,
      Function<Long, LoadResult<E>> loadSingleObjectFunction,
      BiConsumer<Integer, Long> setIdxFunction,
      BiConsumer<Integer, List<Long>> addIDsFunction,
      Consumer<Set<Integer>> removeIdxsFunction
      ) {
    super(gemID, ids, null, setIdxFunction, addIDsFunction, removeIdxsFunction);
    this.loadSingleObjectFunction = loadSingleObjectFunction;

    loadObjectsFunction = this::doLoadObjects;
  }

  protected List<LoadResult<E>> doLoadObjects(List<Long> idList) {
    List<LoadResult<E>> res = new ArrayList<>();

    for (Long id : idList) {
      res.add(loadSingleObjectFunction.apply(id)); 
    }

    return res;
  }
}
