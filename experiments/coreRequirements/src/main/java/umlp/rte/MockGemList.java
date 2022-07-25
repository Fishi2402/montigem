package umlp.rte;

// GEN-T
// GEN-P: Demos mit FlensburgMock


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

// Eine Implementierung der GemList, welche alle IDs lokal hält.
// Wird in FlensburgMock genutzt, da es keine weitere Instanz(wie eine Datenbank) gibt, welche
// die ids verwaltet.
public class MockGemList<E extends GemIdentifiable> extends GemList<E> {
  protected Function<Long, LoadResult<E>> loadSingleObjectFunction;

  public MockGemList(long gemID, Function<Long, LoadResult<E>> loadSingleObjectFunction, List<Long> initialIds) {
    super(gemID, null, null, null);
    this.loadSingleObjectFunction = loadSingleObjectFunction;
    idStorage.addAll(0, initialIds);

    loadSizeFunction = idStorage::size;
    loadIDsFunction = this::doLoadIds;
    loadObjectsFunction = this::doLoadObjects;
  }

  protected List<Long> doLoadIds(int startIncl, int endExcl) {
    List<Long> res = new ArrayList<>();

    for(int i = startIncl; i < endExcl; i++){
      Optional<Long> idOpt = idStorage.getId(i);
      if(idOpt.isPresent()) {
        res.add(idOpt.get());
      }else{
        throw new IllegalStateException("Can not load unknown ids in MockGemList!");
      }
    }

    return res;
  }

  protected List<LoadResult<E>> doLoadObjects(List<Long> idList) {
    List<LoadResult<E>> res = new ArrayList<>();

    for (Long id : idList) {
      res.add(loadSingleObjectFunction.apply(id)); 
    }

    return res;
  }
}
