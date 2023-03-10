/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte;

// RTE-T

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import umlp.backendrte.common.ChunkedIdStorage;
import umlp.backendrte.common.IdStorage;
import umlp.backendrte.common.SimpleIdStorage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class IdStorageTest {

  private static Stream<IdStorage> generator(){
    return Stream.of(new SimpleIdStorage(), new ChunkedIdStorage());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testAddIntoEmpty(IdStorage storage){
    storage.addAll(0, Arrays.asList(10L, 11L, 12L, 13L));

    assertTrue(storage.getId(0).isPresent());
    assertTrue(storage.getId(1).isPresent());
    assertTrue(storage.getId(2).isPresent());
    assertTrue(storage.getId(3).isPresent());

    assertEquals(10L, storage.getId(0).get());
    assertEquals(11L, storage.getId(1).get());
    assertEquals(12L, storage.getId(2).get());
    assertEquals(13L, storage.getId(3).get());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testAppendWithoutGap(IdStorage storage){
    storage.addAll(0, Arrays.asList(10L, 11L));
    storage.addAll(2, Arrays.asList(12L, 13L));

    assertTrue(storage.getId(0).isPresent());
    assertTrue(storage.getId(1).isPresent());
    assertTrue(storage.getId(2).isPresent());
    assertTrue(storage.getId(3).isPresent());

    assertEquals(10L, storage.getId(0).get());
    assertEquals(11L, storage.getId(1).get());
    assertEquals(12L, storage.getId(2).get());
    assertEquals(13L, storage.getId(3).get());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testAppendWithGap(IdStorage storage){
    storage.addAll(0, Arrays.asList(10L, 11L));
    storage.addAll(3, Arrays.asList(13L, 14L));

    assertTrue(storage.getId(0).isPresent());
    assertTrue(storage.getId(1).isPresent());
    assertFalse(storage.getId(2).isPresent());
    assertTrue(storage.getId(3).isPresent());
    assertTrue(storage.getId(4).isPresent());

    assertEquals(10L, storage.getId(0).get());
    assertEquals(11L, storage.getId(1).get());
    assertEquals(13L, storage.getId(3).get());
    assertEquals(14L, storage.getId(4).get());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testInsertIntoChunk(IdStorage storage){
    storage.addAll(0, Arrays.asList(10L, 11L));
    storage.addAll(1, Arrays.asList(13L, 14L));

    assertTrue(storage.getId(0).isPresent());
    assertTrue(storage.getId(1).isPresent());
    assertTrue(storage.getId(2).isPresent());
    assertTrue(storage.getId(3).isPresent());

    assertEquals(10L, storage.getId(0).get());
    assertEquals(13L, storage.getId(1).get());
    assertEquals(14L, storage.getId(2).get());
    assertEquals(11L, storage.getId(3).get());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testSetInChunk(IdStorage storage){
    storage.addAll(0, Arrays.asList(10L, 11L));
    storage.set(Collections.singletonMap(0, 13L));

    assertEquals(13L, storage.getId(0).get());
    assertEquals(11L, storage.getId(1).get());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testSetOutOfChunk(IdStorage storage){
    storage.set(Collections.singletonMap(0, 10L));
    storage.set(Collections.singletonMap(3, 13L));

    assertEquals(10L, storage.getId(0).get());
    assertEquals(13L, storage.getId(3).get());

    assertFalse(storage.getId(1).isPresent());
    assertFalse(storage.getId(2).isPresent());
    assertFalse(storage.getId(4).isPresent());
    assertFalse(storage.getId(5).isPresent());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testRemoveInChunk(IdStorage storage){
    storage.addAll(0, Arrays.asList(10L, 11L, 12L, 13L));

    storage.remove(Arrays.asList(1));

    assertEquals(10L, storage.getId(0).get());
    assertEquals(12L, storage.getId(1).get());
    assertEquals(13L, storage.getId(2).get());
  }

  @ParameterizedTest
  @MethodSource("generator")
  public void testRemoveNonExisting(IdStorage storage){
    storage.remove((Arrays.asList(0, 1, 2)));
  }

  // TODO: Test and fix ChunkedIdStorage
  @Test
  public void testRemoveByExistingId(){
    IdStorage storage = new SimpleIdStorage();
    storage.addAll(0, List.of(1001L, 1002L, 1003L));

    storage.removeId(1003L);
    assertEquals(2, storage.size());
    assertEquals(1001L, storage.getId(0).get());
    assertEquals(1002L, storage.getId(1).get());

    storage.removeId(1001L);
    assertEquals(1, storage.size());
    assertEquals(1002L, storage.getId(0).get());
  }

}
