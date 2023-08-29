package associations;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
  }


  @Test
  public void testOptToOneSync() {
    AssociationsMock.init();

    OptToOneRhs rhs = AssociationsManager.optToOneRhsBuilder().build().get();
    OptToOneLhs lhs = AssociationsManager.optToOneLhsBuilder().optToOneRhs(rhs).build().get();

    assertEquals(rhs, lhs.getOptToOneRhs());
  }


  @Test
  public void testOptToManySync() {
    AssociationsMock.init();

    // 3xR and 1xL
    OptToManyLhsBuilder lhsBuilder = AssociationsManager.optToManyLhsBuilder();
    // R1 -> L, R2 -> L, R3 -> L
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
    OptToManyRhs rhs2 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
    OptToManyRhs rhs3 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
    // L ->
    OptToManyLhs lhs = lhsBuilder.build().get();

    assertEquals(lhs, rhs1.getOptToManyLhs());
    assertEquals(lhs, rhs2.getOptToManyLhs());
    assertEquals(lhs, rhs3.getOptToManyLhs());

    assertTrue(lhs.getOptToManyRhs().contains(rhs1));
    assertTrue(lhs.getOptToManyRhs().contains(rhs2));
    assertTrue(lhs.getOptToManyRhs().contains(rhs3));

    OptToManyRhsBuilder rhsBuilder1 = AssociationsManager.optToManyRhsBuilder();
    OptToManyRhsBuilder rhsBuilder2 = AssociationsManager.optToManyRhsBuilder();
    OptToManyRhsBuilder rhsBuilder3 = AssociationsManager.optToManyRhsBuilder();

    OptToManyLhs optToManyLhs = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertTrue(optToManyLhs.getOptToManyRhs().contains(rhs1));
    assertTrue(optToManyLhs.getOptToManyRhs().contains(rhs2));
    assertTrue(optToManyLhs.getOptToManyRhs().contains(rhs3));
    //Object was not loaded: EMPTY
    assertEquals(optToManyLhs, rhs1.getOptToManyLhs());
    assertEquals(optToManyLhs, rhs2.getOptToManyLhs());
    assertEquals(optToManyLhs, rhs3.getOptToManyLhs());

  }

  //TODO generation error. 'Some' is not generated correctly. Edited the class diagram.
//ready
  @Test
  public void testOptToSomeSync() {
    AssociationsMock.init();

    // 1xL and  3xR
    OptToSomeLhsBuilder lhsBuilder = AssociationsManager.optToSomeLhsBuilder();
    OptToSomeRhs rhs1 = AssociationsManager.optToSomeRhsBuilder().build().get();
    OptToSomeRhs rhs2 = AssociationsManager.optToSomeRhsBuilder().build().get();
    OptToSomeRhs rhs3 = AssociationsManager.optToSomeRhsBuilder().build().get();
    // L1 <- R3
    OptToSomeLhs lhs = lhsBuilder.optToSomeRhs(Sets.newHashSet(rhs3, rhs2, rhs1)).build().get();

    assertTrue(lhs.getOptToSomeRhs().contains(rhs3));
    assertTrue(lhs.getOptToSomeRhs().contains(rhs2));
    assertTrue(lhs.getOptToSomeRhs().contains(rhs1));

    //TODO does not work since other side is not automatically set.
    assertEquals(lhs, rhs1.getOptToSomeLhs());
    assertEquals(lhs, rhs2 .getOptToSomeLhs());
    assertEquals(lhs, rhs3.getOptToSomeLhs());


    //Other side:
    OptToSomeRhsBuilder rhsBuilder1 = AssociationsManager.optToSomeRhsBuilder();
    OptToSomeRhsBuilder rhsBuilder2 = AssociationsManager.optToSomeRhsBuilder();
    OptToSomeRhsBuilder rhsBuilder3 = AssociationsManager.optToSomeRhsBuilder();

    OptToSomeLhs optToSomeLhs = AssociationsManager.optToSomeLhsBuilder().optToSomeRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertTrue(optToSomeLhs.getOptToSomeRhs().contains(rhs1));
    assertTrue(optToSomeLhs.getOptToSomeRhs().contains(rhs2));
    assertTrue(optToSomeLhs.getOptToSomeRhs().contains(rhs3));
    //Object was not loaded: EMPTY
    assertEquals(optToSomeLhs, rhs1.getOptToSomeLhs());
    assertEquals(optToSomeLhs, rhs2.getOptToSomeLhs());
    assertEquals(optToSomeLhs, rhs3.getOptToSomeLhs());

  }


  @Test
  public void testOneToOptSync() {
    AssociationsMock.init();

    OneToOptLhsBuilder lhsBuilder = AssociationsManager.oneToOptLhsBuilder();
    OneToOptRhs rhs = AssociationsManager.oneToOptRhsBuilder().oneToOptLhs(lhsBuilder).build().get();
    OneToOptLhs lhs = lhsBuilder.oneToOptRhs(rhs).build().get();
    assertEquals(rhs, lhs.getOneToOptRhs());

  }

  @Disabled
  @Test
  public void testOneToManySync() {
    AssociationsMock.init();

    OneToManyLhsBuilder lhsBuilder = AssociationsManager.oneToManyLhsBuilder();

    OneToManyRhs rhs1 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();
    OneToManyRhs rhs2 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();
    OneToManyRhs rhs3 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();

    OneToManyLhs lhs = lhsBuilder.build().get();

    assertEquals(lhs, rhs1.getOneToManyLhs());
    assertEquals(lhs, rhs2.getOneToManyLhs());
    assertEquals(lhs, rhs3.getOneToManyLhs());

    assertTrue(lhs.getOneToManyRhs().contains(rhs1));
    assertTrue(lhs.getOneToManyRhs().contains(rhs2));
    assertTrue(lhs.getOneToManyRhs().contains(rhs3));

    //other side:
    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();
    //No value present
    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertTrue(oneToManyLhs.getOneToManyRhs().contains(rhs1));
    assertTrue(oneToManyLhs.getOneToManyRhs().contains(rhs2));
    assertTrue(oneToManyLhs.getOneToManyRhs().contains(rhs3));
    assertEquals(oneToManyLhs, rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs3.getOneToManyLhs());


  }

  @Test
  public void testManyToOptSync() {
    AssociationsMock.init();

    ManyToOptRhs rhsBuilder = AssociationsManager.manyToOptRhsBuilder().build().get();

    ManyToOptLhs lhs1 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhsBuilder).build().get();
    ManyToOptLhs lhs2 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhsBuilder).build().get();
    ManyToOptLhs lhs3 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhsBuilder).build().get();

    ManyToOptRhs rhs = rhsBuilder.build().get();

    assertEquals(rhs, lhs1.getManyToOptRhs());
    assertEquals(rhs, lhs2.getManyToOptRhs());
    assertEquals(rhs, lhs3.getManyToOptRhs());

    assertEquals(lhs2, rhs.getManyToOptLhs(1));
    assertEquals(lhs3, rhs.getManyToOptLhs(2));
    assertEquals(lhs1, rhs.getManyToOptLhs(0));

    ManyToOptRhsBuilder rhsBuilder1 = AssociationsManager.manyToOptRhsBuilder();
    ManyToOptRhsBuilder rhsBuilder2 = AssociationsManager.manyToOptRhsBuilder();
    ManyToOptRhsBuilder rhsBuilder3 = AssociationsManager.manyToOptRhsBuilder();
/*
    ManyToOptLhs manyToOptLhs = AssociationsManager.manyToOptRhsBuilder().manyToOptRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs2) );
   assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs3) );
  assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs1) );
    assertEquals(optToManyLhs , rhs1.getOptToManyLhs());
    assertEquals(optToManyLhs , rhs2.getOptToManyLhs());
    assertEquals(optToManyLhs , rhs3.getOptToManyLhs());
*/
  }

  @Disabled
  @Test
  public void testManyToOneSync() {
    AssociationsMock.init();

    ManyToOneRhsBuilder rhsBuilder = AssociationsManager.manyToOneRhsBuilder();

    ManyToOneLhs lhs1 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhsBuilder).build().get();
    ManyToOneLhs lhs2 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhsBuilder).build().get();
    ManyToOneLhs lhs3 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhsBuilder).build().get();

    ManyToOneRhs rhs = rhsBuilder.manyToOneLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();

    assertTrue(rhs.getManyToOneLhs().contains(lhs1));
    assertTrue(rhs.getManyToOneLhs().contains(lhs1));
    assertTrue(rhs.getManyToOneLhs().contains(lhs1));


    ManyToOneLhs lhsBuilder1 = AssociationsManager.manyToOneLhsBuilder().build().get();
    ManyToOneLhs lhsBuilder2 = AssociationsManager.manyToOneLhsBuilder().build().get();
    ManyToOneLhs lhsBuilder3 = AssociationsManager.manyToOneLhsBuilder().build().get();


    ManyToOneRhs manyToOneRhs = AssociationsManager.manyToOneRhsBuilder().manyToOneLhs(Sets.newHashSet(lhsBuilder1, lhsBuilder2, lhsBuilder3)).build().get();

    lhs1 = lhsBuilder1.build().get();
    lhs2 = lhsBuilder2.build().get();
    lhs3 = lhsBuilder3.build().get();

    assertTrue(manyToOneRhs.getManyToOneLhs().contains(lhs1));
    assertTrue(manyToOneRhs.getManyToOneLhs().contains(lhs2));
    assertTrue(manyToOneRhs.getManyToOneLhs().contains(lhs3));

    assertEquals(manyToOneRhs, lhs1.getManyToOneRhs());
    assertEquals(manyToOneRhs, lhs2.getManyToOneRhs());
    assertEquals(manyToOneRhs, lhs3.getManyToOneRhs());
  }

  @Disabled("Does not work since other side is not automatically set")
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
/*
    assertTrue(rhs1.getManyToManyLhs().contains(lhs1));
    assertEquals(lhs1, rhs2.getManyToManyLhs());
    assertEquals(lhs1, rhs3.getManyToManyLhs());
    assertEquals(lhs2, rhs1.getManyToManyLhs());
    assertEquals(lhs2, rhs2.getManyToManyLhs());
    assertEquals(lhs2, rhs3.getManyToManyLhs());
    assertEquals(lhs3, rhs1.getManyToManyLhs());
    assertEquals(lhs3, rhs2.getManyToManyLhs());
    assertEquals(lhs3, rhs3.getManyToManyLhs());
*/
    assertTrue(lhs1.getManyToManyRhs().contains(rhs1));
    assertTrue(lhs1.getManyToManyRhs().contains(rhs2));
    assertTrue(lhs1.getManyToManyRhs().contains(rhs3));

    assertTrue(lhs2.getManyToManyRhs().contains(rhs1));
    assertTrue(lhs2.getManyToManyRhs().contains(rhs2));
    assertTrue(lhs2.getManyToManyRhs().contains(rhs3));

    assertTrue(lhs3.getManyToManyRhs().contains(rhs1));
    assertTrue(lhs3.getManyToManyRhs().contains(rhs2));
    assertTrue(lhs3.getManyToManyRhs().contains(rhs3));


    //other side:

    ManyToManyRhs rhs11 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyRhs rhs22 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyRhs rhs33 = AssociationsManager.manyToManyRhsBuilder().build().get();

    ManyToManyLhs lhs11 = AssociationsManager.manyToManyLhsBuilder().build().get();
    ManyToManyLhs lhs22 = AssociationsManager.manyToManyLhsBuilder().build().get();
    ManyToManyLhs lhs33 = AssociationsManager.manyToManyLhsBuilder().build().get();

    rhs11.addManyToManyLhs(lhs11);
    rhs22.addManyToManyLhs(lhs22);
    rhs33.addManyToManyLhs(lhs22);

    rhs11.addManyToManyLhs(lhs11);
    rhs22.addManyToManyLhs(lhs22);
    rhs33.addManyToManyLhs(lhs33);

    rhs11.addManyToManyLhs(lhs11);
    rhs22.addManyToManyLhs(lhs22);
    rhs33.addManyToManyLhs(lhs33);


    assertEquals(rhs11, lhs11.getManyToManyRhs().get(0));
    assertEquals(rhs11, lhs22.getManyToManyRhs().get(0));
    assertEquals(rhs11, lhs33.getManyToManyRhs().get(0));
    assertEquals(rhs22, lhs11.getManyToManyRhs().get(1));
    assertEquals(rhs22, lhs22.getManyToManyRhs().get(1));
    assertEquals(rhs22, lhs33.getManyToManyRhs().get(1));
    assertEquals(rhs33, lhs11.getManyToManyRhs().get(2));
    assertEquals(rhs33, lhs22.getManyToManyRhs().get(2));
    assertEquals(rhs33, lhs33.getManyToManyRhs().get(2));


    assertTrue(rhs11.getManyToManyLhs().contains(lhs11));
    assertTrue(rhs11.getManyToManyLhs().contains(lhs22));
    assertTrue(rhs11.getManyToManyLhs().contains(lhs33));


    assertTrue(rhs22.getManyToManyLhs().contains(lhs11));
    assertTrue(rhs22.getManyToManyLhs().contains(lhs22));
    assertTrue(rhs22.getManyToManyLhs().contains(lhs33));

    assertTrue(rhs33.getManyToManyLhs().contains(lhs11));
    assertTrue(rhs33.getManyToManyLhs().contains(lhs22));
    assertTrue(rhs33.getManyToManyLhs().contains(lhs33));


  }

  @Disabled
  @Test
  public void testManyToSomeSync(){
    AssociationsMock.init();

    ManyToSomeLhs lhs1 = AssociationsManager.manyToSomeLhsBuilder().build().get();
    ManyToSomeLhs lhs2 = AssociationsManager.manyToSomeLhsBuilder().build().get();
    ManyToSomeLhs lhs3 = AssociationsManager.manyToSomeLhsBuilder().build().get();

    ManyToSomeRhs rhs1 = AssociationsManager.manyToSomeRhsBuilder().build().get();
    ManyToSomeRhs rhs2 = AssociationsManager.manyToSomeRhsBuilder().build().get();
    ManyToSomeRhs rhs3 = AssociationsManager.manyToSomeRhsBuilder().build().get();

    lhs1.addManyToSomeRhs(rhs1);
    lhs1.addManyToSomeRhs(rhs2);
    lhs1.addManyToSomeRhs(rhs3);

    lhs2.addManyToSomeRhs(rhs1);
    lhs2.addManyToSomeRhs(rhs2);
    lhs2.addManyToSomeRhs(rhs3);

    lhs3.addManyToSomeRhs(rhs1);
    lhs3.addManyToSomeRhs(rhs2);
    lhs3.addManyToSomeRhs(rhs3);

    assertTrue(lhs1.getManyToSomeRhs().contains(rhs1));
    assertTrue(lhs1.getManyToSomeRhs().contains(rhs2));
    assertTrue(lhs1.getManyToSomeRhs().contains(rhs3));
    assertTrue(lhs2.getManyToSomeRhs().contains(rhs1));
    assertTrue(lhs2.getManyToSomeRhs().contains(rhs2));
    assertTrue(lhs2.getManyToSomeRhs().contains(rhs3));
    assertTrue(lhs3.getManyToSomeRhs().contains(rhs1));
    assertTrue(lhs3.getManyToSomeRhs().contains(rhs2));
    assertTrue(lhs3.getManyToSomeRhs().contains(rhs3));

    //other side:

    ManyToSomeLhs lhs11 = AssociationsManager.manyToSomeLhsBuilder().build().get();
    ManyToSomeLhs lhs22 = AssociationsManager.manyToSomeLhsBuilder().build().get();
    ManyToSomeLhs lhs33 = AssociationsManager.manyToSomeLhsBuilder().build().get();

    ManyToSomeRhs rhs11 = AssociationsManager.manyToSomeRhsBuilder().build().get();
    ManyToSomeRhs rhs22 = AssociationsManager.manyToSomeRhsBuilder().build().get();
    ManyToSomeRhs rhs33 = AssociationsManager.manyToSomeRhsBuilder().build().get();

    rhs11.addManyToSomeLhs(lhs11);
    rhs11.addManyToSomeLhs(lhs22);
    rhs11.addManyToSomeLhs(lhs33);

    rhs22.addManyToSomeLhs(lhs11);
    rhs22.addManyToSomeLhs(lhs22);
    rhs22.addManyToSomeLhs(lhs33);

    rhs33.addManyToSomeLhs(lhs11);
    rhs33.addManyToSomeLhs(lhs22);
    rhs33.addManyToSomeLhs(lhs33);

    assertTrue(lhs11.getManyToSomeRhs().contains(rhs11));
    assertTrue(lhs11.getManyToSomeRhs().contains(rhs22));
    assertTrue(lhs11.getManyToSomeRhs().contains(rhs33));
    assertTrue(lhs22.getManyToSomeRhs().contains(rhs11));
    assertTrue(lhs22.getManyToSomeRhs().contains(rhs22));
    assertTrue(lhs22.getManyToSomeRhs().contains(rhs33));
    assertTrue(lhs33.getManyToSomeRhs().contains(rhs11));
    assertTrue(lhs33.getManyToSomeRhs().contains(rhs22));
    assertTrue(lhs33.getManyToSomeRhs().contains(rhs33));

  }

  @Test
  public void testSomeToOptSync() {
    AssociationsMock.init();

    // 3xL and  1xR
    SomeToOptRhsBuilder rhsBuilder = AssociationsManager.someToOptRhsBuilder();
    SomeToOptLhs lhs1 = AssociationsManager.someToOptLhsBuilder().build().get();
    SomeToOptLhs lhs2 = AssociationsManager.someToOptLhsBuilder().build().get();
    SomeToOptLhs lhs3 = AssociationsManager.someToOptLhsBuilder().build().get();

    // R <- L1, L2, L3
    SomeToOptRhs rhs = rhsBuilder.someToOptLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();

    assertTrue(rhs.getSomeToOptLhs().contains(lhs1));
    assertTrue(rhs.getSomeToOptLhs().contains(lhs2));
    assertTrue(rhs.getSomeToOptLhs().contains(lhs3));

    //TODO does not work since other side is not automatically set.
    assertEquals(rhs, lhs1.getSomeToOptRhs());
    assertEquals(rhs, lhs2.getSomeToOptRhs());
    assertEquals(rhs, lhs3.getSomeToOptRhs());


    //Other side:
    SomeToOptLhsBuilder lhsBuilder1 = AssociationsManager.someToOptLhsBuilder();
    SomeToOptLhsBuilder lhsBuilder2 = AssociationsManager.someToOptLhsBuilder();
    SomeToOptLhsBuilder lhsBuilder3 = AssociationsManager.someToOptLhsBuilder();

    SomeToOptRhs SomeToOptRhs = AssociationsManager.someToOptRhsBuilder().someToOptLhs(Sets.newHashSet(lhsBuilder1, lhsBuilder2, lhsBuilder3)).build().get();

    lhs1 = lhsBuilder1.build().get();
    lhs2 = lhsBuilder2.build().get();
    lhs3 = lhsBuilder3.build().get();

    assertTrue(SomeToOptRhs.getSomeToOptLhs().contains(lhs1));
    assertTrue(SomeToOptRhs.getSomeToOptLhs().contains(lhs2));
    assertTrue(SomeToOptRhs.getSomeToOptLhs().contains(lhs3));

    //Object was not loaded: EMPTY
    assertEquals(SomeToOptRhs, lhs1.getSomeToOptRhs());
    assertEquals(SomeToOptRhs, lhs2.getSomeToOptRhs());
    assertEquals(SomeToOptRhs, lhs3.getSomeToOptRhs());

  }

  @Disabled
  @Test
  public void testSomeToManySync() {
    AssociationsMock.init();

    SomeToManyRhs rhs1 = AssociationsManager.someToManyRhsBuilder().build().get();
    SomeToManyRhs rhs2 = AssociationsManager.someToManyRhsBuilder().build().get();
    SomeToManyRhs rhs3 = AssociationsManager.someToManyRhsBuilder().build().get();

    SomeToManyLhs lhs1 = AssociationsManager.someToManyLhsBuilder().build().get();
    SomeToManyLhs lhs2 = AssociationsManager.someToManyLhsBuilder().build().get();
    SomeToManyLhs lhs3 = AssociationsManager.someToManyLhsBuilder().build().get();

    lhs1.addSomeToManyRhs(rhs1);
    lhs1.addSomeToManyRhs(rhs2);
    lhs1.addSomeToManyRhs(rhs3);

    lhs2.addSomeToManyRhs(rhs1);
    lhs2.addSomeToManyRhs(rhs2);
    lhs2.addSomeToManyRhs(rhs3);

    lhs3.addSomeToManyRhs(rhs1);
    lhs3.addSomeToManyRhs(rhs2);
    lhs3.addSomeToManyRhs(rhs3);

    assertTrue(lhs1.getSomeToManyRhs().contains(rhs1));
    assertTrue(lhs1.getSomeToManyRhs().contains(rhs2));
    assertTrue(lhs1.getSomeToManyRhs().contains(rhs3));
    assertTrue(lhs2.getSomeToManyRhs().contains(rhs1));
    assertTrue(lhs2.getSomeToManyRhs().contains(rhs2));
    assertTrue(lhs2.getSomeToManyRhs().contains(rhs3));
    assertTrue(lhs3.getSomeToManyRhs().contains(rhs1));
    assertTrue(lhs3.getSomeToManyRhs().contains(rhs2));
    assertTrue(lhs3.getSomeToManyRhs().contains(rhs3));

    //other side:

    SomeToManyLhs lhs11 = AssociationsManager.someToManyLhsBuilder().build().get();
    SomeToManyLhs lhs22 = AssociationsManager.someToManyLhsBuilder().build().get();
    SomeToManyLhs lhs33 = AssociationsManager.someToManyLhsBuilder().build().get();

    SomeToManyRhs rhs11 = AssociationsManager.someToManyRhsBuilder().build().get();
    SomeToManyRhs rhs22 = AssociationsManager.someToManyRhsBuilder().build().get();
    SomeToManyRhs rhs33 = AssociationsManager.someToManyRhsBuilder().build().get();

    rhs11.addSomeToManyLhs(lhs11);
    rhs11.addSomeToManyLhs(lhs22);
    rhs11.addSomeToManyLhs(lhs33);

    rhs22.addSomeToManyLhs(lhs11);
    rhs22.addSomeToManyLhs(lhs22);
    rhs22.addSomeToManyLhs(lhs33);

    rhs33.addSomeToManyLhs(lhs11);
    rhs33.addSomeToManyLhs(lhs22);
    rhs33.addSomeToManyLhs(lhs33);

    assertTrue(lhs11.getSomeToManyRhs().contains(rhs11));
    assertTrue(lhs11.getSomeToManyRhs().contains(rhs22));
    assertTrue(lhs11.getSomeToManyRhs().contains(rhs33));
    assertTrue(lhs22.getSomeToManyRhs().contains(rhs11));
    assertTrue(lhs22.getSomeToManyRhs().contains(rhs22));
    assertTrue(lhs22.getSomeToManyRhs().contains(rhs33));
    assertTrue(lhs33.getSomeToManyRhs().contains(rhs11));
    assertTrue(lhs33.getSomeToManyRhs().contains(rhs22));
    assertTrue(lhs33.getSomeToManyRhs().contains(rhs33));

  }
}
