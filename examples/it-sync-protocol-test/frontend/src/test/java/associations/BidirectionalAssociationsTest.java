/* (c) https://github.com/MontiCore/monticore */
package associations;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BidirectionalAssociationsTest {

  // TODO: Impl setting the other side
  // TODO: Filter attributes for "umlp.builder.IsBuildable"

  @Test
  public void testOptToOptSync() {
    AssociationsMock.init();

    OptToOptLhs lhs = AssociationsManager.optToOptLhsBuilder().build().get();
    OptToOptRhs rhs = AssociationsManager.optToOptRhsBuilder().build().get();
    lhs.setOptToOptRhs(rhs);

    assertEquals(rhs, lhs.getOptToOptRhs());
    assertEquals(lhs, rhs.getOptToOptLhs());

    OptToOptLhs lhs1 = AssociationsManager.optToOptLhsBuilder().build().get();
    OptToOptRhs rhs1 = AssociationsManager.optToOptRhsBuilder().optToOptLhs(lhs1).build().get();

    assertEquals(rhs1, lhs1.getOptToOptRhs());
    assertEquals(lhs1, rhs1.getOptToOptLhs());

    // delete assoc
    lhs1.setOptToOptRhsAbsent();

    assertFalse(lhs1.getOptToOptRhsGem().isPresent());
    assertFalse(rhs1.getOptToOptLhsGem().isPresent());

  }


  @Test
  public void testOptToOneSync() {
    AssociationsMock.init();

    OptToOneRhs rhs = AssociationsManager.optToOneRhsBuilder().build().get();
    OptToOneLhs lhs = AssociationsManager.optToOneLhsBuilder().optToOneRhs(rhs).build().get();

    assertEquals(rhs, lhs.getOptToOneRhs());
    assertEquals(lhs, rhs.getOptToOneLhs());

    // change assoc
    OptToOneRhs rhs2 = AssociationsManager.optToOneRhsBuilder().build().get();
    lhs.setOptToOneRhs(rhs2);

    assertEquals(rhs2, lhs.getOptToOneRhs());
    assertEquals(lhs, rhs2.getOptToOneLhs());
    assertFalse(rhs.getOptToOneLhsGem().isPresent());

  }


  @Test
  public void testOptToManySync() {
    AssociationsMock.init();

    // 3xR and 1xL
    OptToManyLhs lhs = AssociationsManager.optToManyLhsBuilder().build().get();
    // R1 -> L, R2 -> L, R3 -> L
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhs).build().get();
    OptToManyRhs rhs2 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhs).build().get();
    OptToManyRhs rhs3 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhs).build().get();
    // L ->

    assertEquals(lhs, rhs1.getOptToManyLhs());
    assertEquals(lhs, rhs2.getOptToManyLhs());
    assertEquals(lhs, rhs3.getOptToManyLhs());

    assertTrue(lhs.getOptToManyRhsList().contains(rhs1));
    assertTrue(lhs.getOptToManyRhsList().contains(rhs2));
    assertTrue(lhs.getOptToManyRhsList().contains(rhs3));

    OptToManyRhs rhs21 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyRhs rhs22 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyRhs rhs23 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs2 = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Sets.newHashSet(rhs21, rhs22, rhs23)).build().get();

    assertTrue(lhs2.getOptToManyRhsList().contains(rhs21));
    assertTrue(lhs2.getOptToManyRhsList().contains(rhs22));
    assertTrue(lhs2.getOptToManyRhsList().contains(rhs23));

    assertEquals(lhs2, rhs21.getOptToManyLhs());
    assertEquals(lhs2, rhs22.getOptToManyLhs());
    assertEquals(lhs2, rhs23.getOptToManyLhs());

    // Add assoc after build
    OptToManyRhs rhs31 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyRhs rhs32 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyRhs rhs33 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs3 = AssociationsManager.optToManyLhsBuilder().build().get();

    lhs3.addOptToManyRhs(rhs31);
    lhs3.addOptToManyRhs(rhs32);
    lhs3.addOptToManyRhs(rhs33);

    assertTrue(lhs3.getOptToManyRhsList().contains(rhs31));
    assertTrue(lhs3.getOptToManyRhsList().contains(rhs32));
    assertTrue(lhs3.getOptToManyRhsList().contains(rhs33));

    assertEquals(lhs3, rhs31.getOptToManyLhs());
    assertEquals(lhs3, rhs32.getOptToManyLhs());
    assertEquals(lhs3, rhs33.getOptToManyLhs());

    // delete assoc
    lhs3.removeOptToManyRhs(rhs31);

    assertFalse(lhs3.getOptToManyRhsList().contains(rhs31));
    assertTrue(lhs3.getOptToManyRhsList().contains(rhs32));
    assertTrue(lhs3.getOptToManyRhsList().contains(rhs33));

    assertFalse(rhs31.getOptToManyLhsGem().isPresent());
    assertEquals(lhs3, rhs32.getOptToManyLhs());
    assertEquals(lhs3, rhs33.getOptToManyLhs());

  }

  @Test
  public void testOptToSomeSync() {
    AssociationsMock.init();

    // 1xL and  3xR
    OptToSomeRhs rhs1 = AssociationsManager.optToSomeRhsBuilder().build().get();
    OptToSomeRhs rhs2 = AssociationsManager.optToSomeRhsBuilder().build().get();
    OptToSomeRhs rhs3 = AssociationsManager.optToSomeRhsBuilder().build().get();
    OptToSomeLhs lhs = AssociationsManager.optToSomeLhsBuilder().optToSomeRhs(Sets.newHashSet(rhs3, rhs2, rhs1)).build().get();

    assertTrue(lhs.getOptToSomeRhsList().contains(rhs3));
    assertTrue(lhs.getOptToSomeRhsList().contains(rhs2));
    assertTrue(lhs.getOptToSomeRhsList().contains(rhs1));

    assertEquals(lhs, rhs1.getOptToSomeLhs());
    assertEquals(lhs, rhs2 .getOptToSomeLhs());
    assertEquals(lhs, rhs3.getOptToSomeLhs());

    // Add assoc after build
    OptToSomeRhs rhs4 = AssociationsManager.optToSomeRhsBuilder().build().get();
    lhs.addOptToSomeRhs(rhs4);

    assertEquals(lhs, rhs4.getOptToSomeLhs());
    assertTrue(lhs.getOptToSomeRhsList().contains(rhs4));

    // delete right
    lhs.removeOptToSomeRhs(rhs4);

    assertFalse(lhs.getOptToSomeRhsList().contains(rhs4));
    assertFalse(rhs4.getOptToSomeLhsGem().isPresent());

    // delete left
    rhs1.setOptToSomeLhsAbsent();

    assertFalse(rhs1.getOptToSomeLhsGem().isPresent());
    assertFalse(lhs.getOptToSomeRhsList().contains(rhs1));

  }


  @Test
  public void testOneToOptSync() {
    AssociationsMock.init();

    OneToOptLhs lhs = AssociationsManager.oneToOptLhsBuilder().build().get();
    OneToOptRhs rhs = AssociationsManager.oneToOptRhsBuilder().oneToOptLhs(lhs).build().get();

    assertEquals(rhs, lhs.getOneToOptRhs());
    assertEquals(lhs, rhs.getOneToOptLhs());

    // change assoc after build
    OneToOptLhs lhs2 = AssociationsManager.oneToOptLhsBuilder().build().get();
    rhs.setOneToOptLhs(lhs2);

    assertEquals(rhs, lhs2.getOneToOptRhs());
    assertEquals(lhs2, rhs.getOneToOptLhs());
    assertFalse(lhs.getOneToOptRhsGem().isPresent());

  }

  @Test
  public void testOneToManySync() {
    AssociationsMock.init();

    OneToManyLhs lhs = AssociationsManager.oneToManyLhsBuilder().build().get();

    OneToManyRhs rhs1 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhs).build().get();
    OneToManyRhs rhs2 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhs).build().get();
    OneToManyRhs rhs3 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhs).build().get();

    assertEquals(lhs, rhs1.getOneToManyLhs());
    assertEquals(lhs, rhs2.getOneToManyLhs());
    assertEquals(lhs, rhs3.getOneToManyLhs());

    assertTrue(lhs.getOneToManyRhsList().contains(rhs1));
    assertTrue(lhs.getOneToManyRhsList().contains(rhs2));
    assertTrue(lhs.getOneToManyRhsList().contains(rhs3));

    // change assoc after build
    OneToManyLhs lhs2 = AssociationsManager.oneToManyLhsBuilder().build().get();
    rhs1.setOneToManyLhs(lhs2);

    assertEquals(lhs2, rhs1.getOneToManyLhs());
    assertTrue(lhs2.getOneToManyRhsList().contains(rhs1));

  }

  @Test
  public void testManyToOptSync() {
    AssociationsMock.init();

    ManyToOptRhs rhs = AssociationsManager.manyToOptRhsBuilder().build().get();

    ManyToOptLhs lhs1 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhs).build().get();
    ManyToOptLhs lhs2 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhs).build().get();
    ManyToOptLhs lhs3 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhs).build().get();

    assertEquals(rhs, lhs1.getManyToOptRhs());
    assertEquals(rhs, lhs2.getManyToOptRhs());
    assertEquals(rhs, lhs3.getManyToOptRhs());

    assertEquals(lhs2, rhs.getManyToOptLhs(1));
    assertEquals(lhs3, rhs.getManyToOptLhs(2));
    assertEquals(lhs1, rhs.getManyToOptLhs(0));

    // add assoc after build
    ManyToOptRhs rhs2 = AssociationsManager.manyToOptRhsBuilder().build().get();

    ManyToOptLhs lhs21 = AssociationsManager.manyToOptLhsBuilder().build().get();
    ManyToOptLhs lhs22 = AssociationsManager.manyToOptLhsBuilder().build().get();
    ManyToOptLhs lhs23 = AssociationsManager.manyToOptLhsBuilder().build().get();

    rhs2.addManyToOptLhs(lhs21);
    rhs2.addManyToOptLhs(lhs22);
    rhs2.addManyToOptLhs(lhs23);

    assertEquals(rhs2, lhs21.getManyToOptRhs());
    assertEquals(rhs2, lhs22.getManyToOptRhs());
    assertEquals(rhs2, lhs23.getManyToOptRhs());

    assertEquals(lhs22, rhs2.getManyToOptLhs(1));
    assertEquals(lhs23, rhs2.getManyToOptLhs(2));
    assertEquals(lhs21, rhs2.getManyToOptLhs(0));

    // add assoc after build: use another add
    ManyToOptRhs rhs3 = AssociationsManager.manyToOptRhsBuilder().build().get();

    ManyToOptLhs lhs31 = AssociationsManager.manyToOptLhsBuilder().build().get();
    ManyToOptLhs lhs32 = AssociationsManager.manyToOptLhsBuilder().build().get();
    ManyToOptLhs lhs33 = AssociationsManager.manyToOptLhsBuilder().build().get();

    rhs3.addAllManyToOptLhss(Sets.newHashSet(lhs31, lhs32, lhs33));

    assertEquals(rhs3, lhs31.getManyToOptRhs());
    assertEquals(rhs3, lhs32.getManyToOptRhs());
    assertEquals(rhs3, lhs33.getManyToOptRhs());

    assertTrue(rhs3.containsManyToOptLhs(lhs31));
    assertTrue(rhs3.containsManyToOptLhs(lhs32));
    assertTrue(rhs3.containsManyToOptLhs(lhs33));

    // add assoc after build: other side
    ManyToOptRhs rhs4 = AssociationsManager.manyToOptRhsBuilder().build().get();
    ManyToOptLhs lhs41 = AssociationsManager.manyToOptLhsBuilder().build().get();

    lhs41.setManyToOptRhs(rhs4);

    assertEquals(rhs4, lhs41.getManyToOptRhs());
    assertTrue(rhs4.containsManyToOptLhs(lhs41));

    // delete right
    lhs41.setManyToOptRhsAbsent();
    assertFalse(lhs41.getManyToOptRhsGem().isPresent());
    assertFalse(rhs4.containsManyToOptLhs(lhs41));

    // delete left
    rhs3.removeManyToOptLhs(lhs31);
    assertFalse(rhs3.containsManyToOptLhs(lhs31));
    assertFalse(lhs31.getManyToOptRhsGem().isPresent());
  }

  @Test
  public void testManyToOneSync() {
    AssociationsMock.init();

    ManyToOneRhs rhs = AssociationsManager.manyToOneRhsBuilder().build().get();

    ManyToOneLhs lhs1 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhs).build().get();
    ManyToOneLhs lhs2 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhs).build().get();
    ManyToOneLhs lhs3 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhs).build().get();

    assertTrue(rhs.getManyToOneLhsList().contains(lhs1));
    assertTrue(rhs.getManyToOneLhsList().contains(lhs2));
    assertTrue(rhs.getManyToOneLhsList().contains(lhs3));

    assertEquals(rhs, lhs1.getManyToOneRhs());
    assertEquals(rhs, lhs2.getManyToOneRhs());
    assertEquals(rhs, lhs3.getManyToOneRhs());

    // change assoc after build
    ManyToOneRhs rhs2 = AssociationsManager.manyToOneRhsBuilder().build().get();
    lhs1.setManyToOneRhs(rhs2);

    assertTrue(rhs2.getManyToOneLhsList().contains(lhs1));
    assertEquals(rhs2, lhs1.getManyToOneRhs());

  }

  @Test
  public void testManyToManySync() {
    AssociationsMock.init();

    ManyToManyRhs rhs1 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyRhs rhs2 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyRhs rhs3 = AssociationsManager.manyToManyRhsBuilder().build().get();

    ManyToManyLhs lhs1 = AssociationsManager.manyToManyLhsBuilder().build().get();
    ManyToManyLhs lhs2 = AssociationsManager.manyToManyLhsBuilder().build().get();
    ManyToManyLhs lhs3 = AssociationsManager.manyToManyLhsBuilder().build().get();

    lhs1.addManyToManyRhs(rhs1);
    lhs1.addManyToManyRhs(rhs2);
    lhs1.addManyToManyRhs(rhs3);

    lhs2.addManyToManyRhs(rhs1);
    lhs2.addManyToManyRhs(rhs2);
    lhs2.addManyToManyRhs(rhs3);

    lhs3.addManyToManyRhs(rhs1);
    lhs3.addManyToManyRhs(rhs2);
    lhs3.addManyToManyRhs(rhs3);

    assertTrue(rhs1.getManyToManyLhsList().contains(lhs1));
    assertTrue(rhs2.getManyToManyLhsList().contains(lhs1));
    assertTrue(rhs3.getManyToManyLhsList().contains(lhs1));

    assertTrue(rhs1.getManyToManyLhsList().contains(lhs2));
    assertTrue(rhs2.getManyToManyLhsList().contains(lhs2));
    assertTrue(rhs3.getManyToManyLhsList().contains(lhs2));

    assertTrue(rhs1.getManyToManyLhsList().contains(lhs3));
    assertTrue(rhs2.getManyToManyLhsList().contains(lhs3));
    assertTrue(rhs3.getManyToManyLhsList().contains(lhs3));

    assertTrue(lhs1.getManyToManyRhsList().contains(rhs1));
    assertTrue(lhs1.getManyToManyRhsList().contains(rhs2));
    assertTrue(lhs1.getManyToManyRhsList().contains(rhs3));

    assertTrue(lhs2.getManyToManyRhsList().contains(rhs1));
    assertTrue(lhs2.getManyToManyRhsList().contains(rhs2));
    assertTrue(lhs2.getManyToManyRhsList().contains(rhs3));

    assertTrue(lhs3.getManyToManyRhsList().contains(rhs1));
    assertTrue(lhs3.getManyToManyRhsList().contains(rhs2));
    assertTrue(lhs3.getManyToManyRhsList().contains(rhs3));

    //other side:
    ManyToManyRhs rhs11 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyRhs rhs22 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyRhs rhs33 = AssociationsManager.manyToManyRhsBuilder().build().get();

    ManyToManyLhs lhs11 = AssociationsManager.manyToManyLhsBuilder().build().get();
    ManyToManyLhs lhs22 = AssociationsManager.manyToManyLhsBuilder().build().get();
    ManyToManyLhs lhs33 = AssociationsManager.manyToManyLhsBuilder().build().get();

    rhs11.addManyToManyLhs(lhs11);
    rhs11.addManyToManyLhs(lhs22);
    rhs11.addManyToManyLhs(lhs33);

    rhs22.addManyToManyLhs(lhs11);
    rhs22.addManyToManyLhs(lhs22);
    rhs22.addManyToManyLhs(lhs33);

    rhs33.addManyToManyLhs(lhs11);
    rhs33.addManyToManyLhs(lhs22);
    rhs33.addManyToManyLhs(lhs33);

    assertTrue(rhs11.getManyToManyLhsList().contains(lhs11));
    assertTrue(rhs22.getManyToManyLhsList().contains(lhs11));
    assertTrue(rhs33.getManyToManyLhsList().contains(lhs11));

    assertTrue(rhs11.getManyToManyLhsList().contains(lhs22));
    assertTrue(rhs22.getManyToManyLhsList().contains(lhs22));
    assertTrue(rhs33.getManyToManyLhsList().contains(lhs22));

    assertTrue(rhs11.getManyToManyLhsList().contains(lhs33));
    assertTrue(rhs22.getManyToManyLhsList().contains(lhs33));
    assertTrue(rhs33.getManyToManyLhsList().contains(lhs33));

    assertTrue(lhs11.getManyToManyRhsList().contains(rhs11));
    assertTrue(lhs11.getManyToManyRhsList().contains(rhs22));
    assertTrue(lhs11.getManyToManyRhsList().contains(rhs33));

    assertTrue(lhs22.getManyToManyRhsList().contains(rhs11));
    assertTrue(lhs22.getManyToManyRhsList().contains(rhs22));
    assertTrue(lhs22.getManyToManyRhsList().contains(rhs33));

    assertTrue(lhs33.getManyToManyRhsList().contains(rhs11));
    assertTrue(lhs33.getManyToManyRhsList().contains(rhs22));
    assertTrue(lhs33.getManyToManyRhsList().contains(rhs33));

    // delete right
    lhs11.removeManyToManyRhs(rhs11);
    assertFalse(rhs11.getManyToManyLhsList().contains(lhs11));
    assertFalse(lhs11.getManyToManyRhsList().contains(rhs11));

    // delete left
    rhs22.removeManyToManyLhs(lhs22);
    assertFalse(rhs22.getManyToManyLhsList().contains(lhs22));
    assertFalse(lhs22.getManyToManyRhsList().contains(rhs22));

  }

  @Test
  public void testManyToSomeSync(){
    AssociationsMock.init();

    ManyToSomeRhs rhs1 = AssociationsManager.manyToSomeRhsBuilder().build().get();
    ManyToSomeRhs rhs2 = AssociationsManager.manyToSomeRhsBuilder().build().get();
    ManyToSomeRhs rhs3 = AssociationsManager.manyToSomeRhsBuilder().build().get();

    ManyToSomeLhs lhs1 = AssociationsManager.manyToSomeLhsBuilder()
        .manyToSomeRhsAdd(rhs1)
        .manyToSomeRhsAdd(rhs2)
        .manyToSomeRhsAdd(rhs3).build().get();
    ManyToSomeLhs lhs2 = AssociationsManager.manyToSomeLhsBuilder()
        .manyToSomeRhs(Sets.newHashSet(rhs1, rhs2, rhs3)).build().get();
    ManyToSomeLhs lhs3 = AssociationsManager.manyToSomeLhsBuilder()
        .manyToSomeRhs(Sets.newHashSet(rhs1, rhs2, rhs3)).build().get();

    assertTrue(lhs1.getManyToSomeRhsList().contains(rhs1));
    assertTrue(lhs1.getManyToSomeRhsList().contains(rhs2));
    assertTrue(lhs1.getManyToSomeRhsList().contains(rhs3));

    assertTrue(lhs2.getManyToSomeRhsList().contains(rhs1));
    assertTrue(lhs2.getManyToSomeRhsList().contains(rhs2));
    assertTrue(lhs2.getManyToSomeRhsList().contains(rhs3));

    assertTrue(lhs3.getManyToSomeRhsList().contains(rhs1));
    assertTrue(lhs3.getManyToSomeRhsList().contains(rhs2));
    assertTrue(lhs3.getManyToSomeRhsList().contains(rhs3));

    // delete right
    lhs1.removeManyToSomeRhs(rhs1);
    assertFalse(lhs1.containsManyToSomeRhs(rhs1));
    assertFalse(rhs1.containsManyToSomeLhs(lhs1));

    // delete left
    rhs2.removeManyToSomeLhs(lhs2);
    assertFalse(lhs2.containsManyToSomeRhs(rhs2));
    assertFalse(rhs2.containsManyToSomeLhs(lhs2));
  }

  @Test
  public void testSomeToOptSync() {
    AssociationsMock.init();

    // 3xL and  1xR
    SomeToOptLhs lhs1 = AssociationsManager.someToOptLhsBuilder().build().get();
    SomeToOptLhs lhs2 = AssociationsManager.someToOptLhsBuilder().build().get();
    SomeToOptLhs lhs3 = AssociationsManager.someToOptLhsBuilder().build().get();

    // R <- L1, L2, L3
    SomeToOptRhs rhs = AssociationsManager.someToOptRhsBuilder().someToOptLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();

    assertTrue(rhs.getSomeToOptLhsList().contains(lhs1));
    assertTrue(rhs.getSomeToOptLhsList().contains(lhs2));
    assertTrue(rhs.getSomeToOptLhsList().contains(lhs3));

    assertEquals(rhs, lhs1.getSomeToOptRhs());
    assertEquals(rhs, lhs2.getSomeToOptRhs());
    assertEquals(rhs, lhs3.getSomeToOptRhs());

    // add assoc after build
    SomeToOptLhs lhs4 = AssociationsManager.someToOptLhsBuilder().build().get();
    rhs.addSomeToOptLhs(lhs4);

    assertTrue(rhs.getSomeToOptLhsList().contains(lhs4));
    assertEquals(rhs, lhs4.getSomeToOptRhs());

    // delete right
    lhs4.setSomeToOptRhsAbsent();
    assertFalse(rhs.containsSomeToOptLhs(lhs4));
    assertFalse(lhs4.getSomeToOptRhsGem().isPresent());
  }

  @Test
  public void testSomeToManySync() {
    AssociationsMock.init();

    SomeToManyLhs lhs1 = AssociationsManager.someToManyLhsBuilder().build().get();
    SomeToManyLhs lhs2 = AssociationsManager.someToManyLhsBuilder().build().get();
    SomeToManyLhs lhs3 = AssociationsManager.someToManyLhsBuilder().build().get();

    SomeToManyRhs rhs1 = AssociationsManager.someToManyRhsBuilder()
        .someToManyLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();
    SomeToManyRhs rhs2 = AssociationsManager.someToManyRhsBuilder()
        .someToManyLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();
    SomeToManyRhs rhs3 = AssociationsManager.someToManyRhsBuilder()
        .someToManyLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();

    lhs1.addSomeToManyRhs(rhs1);
    lhs1.addSomeToManyRhs(rhs2);
    lhs1.addSomeToManyRhs(rhs3);

    lhs2.addSomeToManyRhs(rhs1);
    lhs2.addSomeToManyRhs(rhs2);
    lhs2.addSomeToManyRhs(rhs3);

    lhs3.addSomeToManyRhs(rhs1);
    lhs3.addSomeToManyRhs(rhs2);
    lhs3.addSomeToManyRhs(rhs3);

    assertTrue(lhs1.getSomeToManyRhsList().contains(rhs1));
    assertTrue(lhs1.getSomeToManyRhsList().contains(rhs2));
    assertTrue(lhs1.getSomeToManyRhsList().contains(rhs3));
    assertTrue(lhs2.getSomeToManyRhsList().contains(rhs1));
    assertTrue(lhs2.getSomeToManyRhsList().contains(rhs2));
    assertTrue(lhs2.getSomeToManyRhsList().contains(rhs3));
    assertTrue(lhs3.getSomeToManyRhsList().contains(rhs1));
    assertTrue(lhs3.getSomeToManyRhsList().contains(rhs2));
    assertTrue(lhs3.getSomeToManyRhsList().contains(rhs3));

    // delete right
    lhs1.removeSomeToManyRhs(rhs1);
    assertFalse(lhs1.containsSomeToManyRhs(rhs1));
    assertFalse(rhs1.containsSomeToManyLhs(lhs1));

    // delete left
    SomeToManyLhs checkLhs = rhs2.getSomeToManyLhs(0);
    rhs2.removeSomeToManyLhs(0);
    assertFalse(rhs2.containsSomeToManyLhs(checkLhs));
    assertFalse(checkLhs.containsSomeToManyRhs(rhs2));
  }
}
