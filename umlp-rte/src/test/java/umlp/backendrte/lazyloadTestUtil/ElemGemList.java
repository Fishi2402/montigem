// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.lazyloadTestUtil;

import umlp.backendrte.common.GemList;
import umlp.backendrte.common.LoadResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ElemGemList extends GemList<Elem> {

  protected ListBasedServer server;

  public ElemGemList(java.util.List<Long> ids, ListBasedServer server) {
    super(ids);
    this.server = server;
  }

  @Override
  public Optional<ElemGemList> build() {
    return Optional.of(this);
  }

  protected java.util.List<LoadResult<Elem>> loadObjects(java.util.List<Long> gemIDs) {
    return server.getObjects(gemIDs);
  }

  @Override
  protected void _internal_addAndSync(int idx, List<Long> gemIDs) {
    this.server.addIDs(idx, gemIDs);
  }

  @Override
  protected void _internal_setAndSync(int idx, long gemID) {
    this.server.setIdx(idx, gemID);
  }

  @Override
  protected void _internal_removeAndSync(Set<Integer> idxs) {
    this.server.removeIdxs(idxs);
  }

  @Override
  public boolean TESTING_deepEquals(List<Elem> o) {
    return this == o;
  }
}
