/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

// RTE-P

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Implementierung des IdStorage, welche ids in Chunks verwaltet und Verschiebungen von ids
// durch add und remove operationen lazy durchführt.
// Ein Chunk besteht aus einem start-Index und einer Liste an sequentiellen Ids.
// Der Chunk startend mit idx 4, welcher die ids von 10 bis 14 enthält kann dargestellt werden als
// 4 -> [10, 11, 12, 13, 14]
// Werden neue IDs in der Mitte einer GemList eingefügt, so muss der Index alle späteren IDs verschoben werden.
// z.B. Mit den Chunks 4 -> [10, 11], 7 -> [12, 13, 14] wird [15, 16] an Index 6 eingefügt, dann muss
// der Start vom ehemals zweiten Chunk von 7 auf 9 verschoben werden.
// Analog bei entfernung von indicies.
// Damit diese teuren operationen nicht unnötig ausgeführt werden, werden sie erst ausgeführt, wenn
// auf den entsprechenden Chunk zugegriffen wird.
// Alternativ könnte bei geringer Systemlast diese Operation auch nebenläufig erfolgen.
public class ChunkedIdStorage implements IdStorage {
  protected List<Chunk> chunks = new ArrayList<>();
  protected int correctUntil = 0;
  protected Map<Chunk, Integer> sizeChanges = new HashMap<>();
  private int lastHealedChunkIndex = 0;

  @Override
  public Optional<Long> getId(int idx){
    healUntil(idx);
    return getFloorChunk(idx).flatMap(c -> c.getId(idx));
  }

  @Override
  public void addAll(int i, List<Long> ids) {
    healUntil(i);
    int chunkIndex = getFloorChunkIdx(i);
    Chunk c = null;
    if(chunkIndex >= 0){
      c = chunks.get(chunkIndex);
    }

    if(c != null && (c.contains(i) || c.contains(i - 1))){
      c.ids.addAll(c.getPositionInChunk(i), ids);
      addSizeChange(c, ids.size());
    }else{
      c = new Chunk(i, ids);
      chunks.add(Math.max(chunkIndex + 1, 0), c);
      checkChunksOrdered();
      addSizeChange(c, ids.size());
    }

    lastHealedChunkIndex = 0; // TODO: optimize
  }

  // TODO: remove
  private void checkChunksOrdered() {
    int last = -1;
    for (Chunk chunk : chunks) {
      if(chunk.startIndex <= last){
        throw new IllegalStateException("Chunks must be ordered!");
      }
      last = chunk.startIndex;
    }
  }

  protected void healUntil(int idx) {
    if(idx <= correctUntil){
      return;
    }

    int start = lastHealedChunkIndex;
    if(start < 0){
      return;
    }

    int curOffset = 0;
    List<Chunk> subList = chunks.subList(start, chunks.size());
    List<Integer> toRemove = new ArrayList<>();
    for (int i = 0; i < subList.size(); i++) {
      Chunk chunk = subList.get(i);
      chunk.startIndex += curOffset;
      correctUntil = chunk.startIndex + chunk.ids.size() - 1;
      lastHealedChunkIndex = i;

      if (idx <= correctUntil) {
        break;
      }

      if (sizeChanges.containsKey(chunk)) {
        curOffset += sizeChanges.get(chunk);
        sizeChanges.remove(chunk);
      }

      if(chunk.ids.isEmpty()){
        toRemove.add(i);
      }
    }

    List<Integer> toSort = new ArrayList<>();
    for (Integer r : toRemove) {
      toSort.add(r);
    }
    Collections.reverse(toSort);
    for (Integer r : toSort) {
      chunks.remove((int) r);
    }
  }

  protected int getFloorChunkIdx(int idx) {
    if(chunks.isEmpty()){
      return -1;
    }

    int min = 0;
    int max = lastHealedChunkIndex;

    while(min >= 0 && max <= lastHealedChunkIndex && min <= max){
      int cur = (max + min) >>> 1;
      Chunk curChunk = chunks.get(cur);
      if(curChunk.contains(idx)){
        return cur;
      }

      if(curChunk.getPositionInChunk(idx) < 0){
        max = cur - 1;
      } else {
        min = cur + 1;
      }
    }

    return 0;
  }

  protected Optional<Chunk> getFloorChunk(int i) {
    int chunkIdx = getFloorChunkIdx(i);
    if(chunkIdx < 0){
      return Optional.empty();
    }else{
      return Optional.ofNullable(chunks.get(chunkIdx));
    }
  }

  @Override
  public void set(Map<Integer, Long> changes) {
    for (int idx : changes.keySet()) {
      int chunkIndex = getFloorChunkIdx(idx);
      Chunk c = chunkIndex >= 0 ? chunks.get(chunkIndex) : null;
      Long id = changes.get(idx);
      if(c != null && c.contains(idx)){
        c.ids.set(c.getPositionInChunk(idx), id);
      }else{
        Chunk tmp = new Chunk(idx, Collections.singletonList(id));
        chunks.add(Math.max(chunkIndex + 1, 0), tmp);
        checkChunksOrdered();
      }
    }
  }

  @Override
  public void remove(Collection<Integer> indicies) {
    final AtomicReference<Integer> lastChunk = new AtomicReference<>(-1);
    List<Integer> toSort = new ArrayList<>();
    for (Integer idx : indicies) {
      toSort.add(idx);
    }
    Collections.reverse(toSort);
    for (Integer idx : toSort) {
      if (lastChunk.get() < 0 || !chunks.get(lastChunk.get()).contains(idx)) {
        int fc = getFloorChunkIdx(idx);
        if (fc >= 0) {
          lastChunk.set(fc);
        }
        else {
          lastChunk.set(-1);
        }
      }

      int ci = lastChunk.get();
      if (ci >= 0) {
        Chunk chunk = chunks.get(ci);
        if (chunk.contains(idx)) {
          chunk.ids.remove(chunk.getPositionInChunk(idx));
          correctUntil = Math.min(correctUntil, idx);
          lastHealedChunkIndex = Math.min(lastHealedChunkIndex, ci);
          addSizeChange(chunk, -1);
        }
      }
    }
  }

  private void addSizeChange(Chunk chunk, int change) {
    if (sizeChanges.containsKey(chunk)){
      sizeChanges.put(chunk, sizeChanges.get(chunk)  + change);
    }else{
      sizeChanges.put(chunk, change);
    }
  }

  @Override
  public boolean containsIdx(int idx) {
    Optional<Chunk> fcOpt = getFloorChunk(idx);
    return fcOpt.isPresent() && fcOpt.get().contains(idx);
  }

  @Override
  public void clear() {
    chunks.clear();
    correctUntil = 0;
    lastHealedChunkIndex = 0;
  }

  @Override
  public int size(){
    int res = 0;
    for (Chunk chunk : chunks) {
      res += chunk.ids.size();
    }
    return res;
  }

  // TODO: test
  @Override
  public boolean removeId(long gemId) {
    int chunkId = -1;
    int idx = -1;
    for (int i = 0; i < chunks.size(); i++) {
      Chunk chunk = chunks.get(i);
      List<Long> ids = chunk.ids;
      for (int j = 0; j < ids.size(); j++) {
        Long id = ids.get(j);
        if (id == gemId) {
          chunkId = i;
          idx = j;
          break;
        }
      }
    }

    if(chunkId >= 0 && idx >= 0){
      Chunk toBeSplit = chunks.remove(chunkId);
      List<Chunk> res = new ArrayList<>();

      if(idx > 0){
        res.add(new Chunk(toBeSplit.startIndex, toBeSplit.ids.subList(0, idx)));
      }

      if(idx < toBeSplit.ids.size() - 1){
        res.add(new Chunk(toBeSplit.startIndex + idx + 1, toBeSplit.ids.subList(idx + 1, toBeSplit.ids.size())));
      }
      chunks.addAll(chunkId, res);
      return true;
    }

    return false;
  }

  public static class Chunk{
    int startIndex;
    List<Long> ids;

    public Chunk(int startIndex, List<Long> ids){
      this.startIndex = startIndex;
      if(ids == null || ids.isEmpty()){
        throw new IllegalStateException("No empty chunks allowed!");
      }
      this.ids = new ArrayList<>(ids);
    }

    public boolean contains(int idx){
      return idx >= 0 && idx >= startIndex && idx < startIndex + ids.size();
    }

    public Optional<Long> getId(int idx){
      if(contains(idx)){
        int pos = getPositionInChunk(idx);
        return Optional.of(ids.get(pos));
      }else{
        return Optional.empty();
      }
    }

    public int getPositionInChunk(int idx) {
      return idx - startIndex;
    }
  }
}
