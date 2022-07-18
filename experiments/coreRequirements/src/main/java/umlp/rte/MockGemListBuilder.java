package umlp.rte;

// GEN-T
// GEN-P: Demos mit FlensburgMock

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

// Siehe GemListBuilder
public class MockGemListBuilder<E extends GemIdentifiable> extends MockGemList<E>{
  protected boolean buildFinished = false;
  protected Map<Integer, E> idxToBuilder = new HashMap<>();

  public MockGemListBuilder(long gemID, Function<Long, LoadResult<E>> loadSingleObjectFunction, List<Long> initialIds) {
    super(gemID, loadSingleObjectFunction, initialIds);
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
  public Optional<GemList<E>> build() {
    buildFinished = true;
    idxToBuilder.clear();
    return Optional.of(this);
  }
}
