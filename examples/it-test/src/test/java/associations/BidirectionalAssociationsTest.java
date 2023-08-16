package associations;

import associations.commands.associations.OneToSome_add;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    assertEquals(true, lhs.getOptToManyRhs().contains(rhs1));
    assertEquals(true, lhs.getOptToManyRhs().contains(rhs2));
    assertEquals(true, lhs.getOptToManyRhs().contains(rhs3));

    OptToManyRhsBuilder rhsBuilder1 = AssociationsManager.optToManyRhsBuilder();
    OptToManyRhsBuilder rhsBuilder2 = AssociationsManager.optToManyRhsBuilder();
    OptToManyRhsBuilder rhsBuilder3 = AssociationsManager.optToManyRhsBuilder();

    OptToManyLhs optToManyLhs = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true, optToManyLhs.getOptToManyRhs().contains(rhs1));
    assertEquals(true, optToManyLhs.getOptToManyRhs().contains(rhs2));
    assertEquals(true, optToManyLhs.getOptToManyRhs().contains(rhs3));
    //Object was not loaded: EMPTY
    assertEquals(optToManyLhs, rhs1.getOptToManyLhs());
    assertEquals(optToManyLhs, rhs2.getOptToManyLhs());
    assertEquals(optToManyLhs, rhs3.getOptToManyLhs());

  }

  //TODO generation error. 'Some' is not generated correctly. Edited the class diagram.

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
    assertEquals(lhs, rhs2.getOptToSomeLhs());
    assertEquals(lhs, rhs3.getOptToSomeLhs());


    //Other side:
    OptToSomeRhsBuilder rhsBuilder1 = AssociationsManager.optToSomeRhsBuilder();
    OptToSomeRhsBuilder rhsBuilder2 = AssociationsManager.optToSomeRhsBuilder();
    OptToSomeRhsBuilder rhsBuilder3 = AssociationsManager.optToSomeRhsBuilder();

    OptToSomeLhs optToSomeLhs = AssociationsManager.optToSomeLhsBuilder().optToSomeRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true, optToSomeLhs.getOptToSomeRhs().contains(rhs1));
    assertEquals(true, optToSomeLhs.getOptToSomeRhs().contains(rhs2));
    assertEquals(true, optToSomeLhs.getOptToSomeRhs().contains(rhs3));
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


  @Test
  public void testOneToOneSync() {
    AssociationsMock.init();

    OneToOneLhsBuilder lhsBuilder = AssociationsManager.oneToOneLhsBuilder();
    OneToOneRhs rhs = AssociationsManager.oneToOneRhsBuilder().oneToOneLhs(lhsBuilder).build().get();
    OneToOneLhs lhs = lhsBuilder.oneToOneRhs(rhs).build().get();
    assertEquals(rhs, lhs.getOneToOneRhs());
  }


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

    assertEquals(true, lhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs3));

    //other side:
    /* TODO
    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();
    //No value present
    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs3));
    assertEquals(oneToManyLhs, rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs3.getOneToManyLhs());
    */

  }

  //TODO generation error. 'Some' is not generated correctly. Edited the class diagram.

  @Test
  public void testOneToSomeSync() {
    AssociationsMock.init();

    OneToSomeLhsBuilder lhsBuilder = AssociationsManager.oneToSomeLhsBuilder();

    OneToSomeRhs rhs1 = AssociationsManager.oneToSomeRhsBuilder().oneToSomeLhs(lhsBuilder).build().get();
    OneToSomeRhs rhs2 = AssociationsManager.oneToSomeRhsBuilder().oneToSomeLhs(lhsBuilder).build().get();
    OneToSomeRhs rhs3 = AssociationsManager.oneToSomeRhsBuilder().oneToSomeLhs(lhsBuilder).build().get();

    OneToSomeLhs lhs = lhsBuilder.build().get();

    assertEquals(lhs, rhs1.getOneToSomeLhs());
    assertEquals(lhs, rhs2.getOneToSomeLhs());
    assertEquals(lhs, rhs3.getOneToSomeLhs());


    assertEquals(true, lhs.getOneToSomeRhs().contains(rhs1));
    assertEquals(true, lhs.getOneToSomeRhs().contains(rhs2));
    assertEquals(true, lhs.getOneToSomeRhs().contains(rhs3));

    /* TODO
    OneToSomeRhsBuilder rhsBuilder1 = AssociationsManager.oneToSomeRhsBuilder();
    OneToSomeRhsBuilder rhsBuilder2 = AssociationsManager.oneToSomeRhsBuilder();
    OneToSomeRhsBuilder rhsBuilder3 = AssociationsManager.oneToSomeRhsBuilder();

    OneToSomeLhs oneToSomeLhs = AssociationsManager.oneToSomeLhsBuilder().oneToSomeRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true, oneToSomeLhs.getOneToSomeRhs().contains(rhs1));
    assertEquals(true, oneToSomeLhs.getOneToSomeRhs().contains(rhs2));
    assertEquals(true, oneToSomeLhs.getOneToSomeRhs().contains(rhs3));
    assertEquals(oneToSomeLhs, rhs1.getOneToSomeLhs());
    assertEquals(oneToSomeLhs, rhs2.getOneToSomeLhs());
    assertEquals(oneToSomeLhs, rhs3.getOneToSomeLhs());
    */

  }


  @Test
  public void testManyToOptSync() {
    AssociationsMock.init();

    ManyToOptRhs rhsBuilder = AssociationsManager.manyToOptRhsBuilder();

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

  @Test
  public void testManyToOneSync() {
    AssociationsMock.init();

    ManyToOneRhsBuilder rhsBuilder = AssociationsManager.manyToOneRhsBuilder();

    ManyToOneLhs lhs1 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhsBuilder).build().get();
    ManyToOneLhs lhs2 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhsBuilder).build().get();
    ManyToOneLhs lhs3 = AssociationsManager.manyToOneLhsBuilder().manyToOneRhs(rhsBuilder).build().get();

    ManyToOneRhs rhs = rhsBuilder.manyToOneLhs(Sets.newHashSet(lhs1, lhs2, lhs3)).build().get();

    assertEquals(true, rhs.getManyToOneLhs().contains(lhs1));
    assertEquals(true, rhs.getManyToOneLhs().contains(lhs1));
    assertEquals(true, rhs.getManyToOneLhs().contains(lhs1));


    /*TODO
    ManyToOneLhs lhsBuilder1 = AssociationsManager.manyToOneLhsBuilder();
    ManyToOneLhsBuilder lhsBuilder2 = AssociationsManager.manyToOneLhsBuilder();
    ManyToOneLhsBuilder lhsBuilder3 = AssociationsManager.manyToOneLhsBuilder();


    ManyToOneRhs manyToOneRhs = AssociationsManager.manyToOneRhsBuilder().manyToOneLhs(Sets.newHashSet(lhsBuilder1, lhsBuilder2, lhsBuilder3)).build().get();

    lhs1 = lhsBuilder1.build().get();
    lhs2 = lhsBuilder2.build().get();
    lhs3 = lhsBuilder3.build().get();

    assertEquals(true, manyToOneRhs.getManyToOneLhs().contains(lhs1));
    assertEquals(true, manyToOneRhs.getManyToOneLhs().contains(lhs2));
    assertEquals(true, manyToOneRhs.getManyToOneLhs().contains(lhs3));

    assertEquals(manyToOneRhs, lhs1.getManyToOneRhs());
    assertEquals(manyToOneRhs, lhs2.getManyToOneRhs());
    assertEquals(manyToOneRhs, lhs3.getManyToOneRhs());
    */
  }

  //TODO does not work since other side is not automatically set.
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

    assertTrue(rhs1.getManyToManyLhs().contains(lhs1));
    assertTrue(rhs1.getManyToManyLhs().contains(lhs2));
    assertTrue(rhs1.getManyToManyLhs().contains(lhs3));

    assertTrue(rhs2.getManyToManyLhs().contains(lhs1));
    assertTrue(rhs2.getManyToManyLhs().contains(lhs2));
    assertTrue(rhs2.getManyToManyLhs().contains(lhs3));

    assertTrue(rhs3.getManyToManyLhs().contains(lhs1));
    assertTrue(rhs3.getManyToManyLhs().contains(lhs2));
    assertTrue(rhs3.getManyToManyLhs().contains(lhs3));

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
    rhs22.addManyToManyLhs(lhs11);
    rhs33.addManyToManyLhs(lhs11);

    rhs11.addManyToManyLhs(lhs22);
    rhs22.addManyToManyLhs(lhs22);
    rhs33.addManyToManyLhs(lhs22);

    rhs11.addManyToManyLhs(lhs33);
    rhs22.addManyToManyLhs(lhs33);
    rhs33.addManyToManyLhs(lhs33);

    // Check both sides
    assertTrue(lhs11.getManyToManyRhs().contains(rhs11));
    assertTrue(lhs11.getManyToManyRhs().contains(rhs22));
    assertTrue(lhs11.getManyToManyRhs().contains(rhs33));
    assertTrue(lhs22.getManyToManyRhs().contains(rhs11));
    assertTrue(lhs22.getManyToManyRhs().contains(rhs22));
    assertTrue(lhs22.getManyToManyRhs().contains(rhs33));
    assertTrue(lhs33.getManyToManyRhs().contains(rhs11));
    assertTrue(lhs33.getManyToManyRhs().contains(rhs22));
    assertTrue(lhs33.getManyToManyRhs().contains(rhs33));

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

  /*
    @Test
    public void testManyToSomeSync(){
      AssociationsMock.init();

      ManyToSomeLhsBuilder lhsBuilder = AssociationsManager.optToManyLhsBuilder();
      OptToManyLhsBuilder lhsBuilder2 = AssociationsManager.optToManyLhsBuilder();
      OptToManyLhsBuilder lhsBuilder3 = AssociationsManager.optToManyLhsBuilder();

      OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
      OptToManyRhs rhs2 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
      OptToManyRhs rhs3 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();

      OptToManyLhs lhs = lhsBuilder.build().get();

      assertEquals(lhs, rhs1.getOptToManyLhs());
      assertEquals(3 , lhs.getOptToManyRhs().size());
      assertEquals(rhs1, lhs.getOptToManyRhs(1));



      ManyToSomeLhs lhs1 = AssociationsManager.manyToSomeLhsBuilder().build().get();
      ManyToSomeLhs lhs2 = AssociationsManager.manyToSomeLhsBuilder().build().get();
      ManyToSomeLhs lhs3 = AssociationsManager.manyToSomeLhsBuilder().build().get();


      //assertEquals(lhs, rhs.getManyToSomeLhs());
    }
  */

  //Tests not ready
  @Test
  public void testSomeToOptSync() {
    AssociationsMock.init();

    SomeToOptLhs lhs1 = AssociationsManager.someToOptLhsBuilder().build().get();

    SomeToOptRhs rhs1 = AssociationsManager.someToOptRhsBuilder().someToOptLhs(Collections.singleton(lhs1)).build().get();

    assertEquals(rhs1, lhs1.getSomeToOptRhs());

    assertTrue(rhs1.getSomeToOptLhs().contains(lhs1));

  }

  @Test
  public void testSomeToOneSync() {
    AssociationsMock.init();

    /* TODO
    OneToManyLhsBuilder lhsBuilder = AssociationsManager.oneToManyLhsBuilder();

    OneToManyRhs rhs1 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();
    OneToManyRhs rhs2 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();
    OneToManyRhs rhs3 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();

    OneToManyLhs lhs = lhsBuilder.build().get();

    assertEquals(lhs, rhs1.getOneToManyLhs());
    assertEquals(lhs, rhs2.getOneToManyLhs());
    assertEquals(lhs, rhs3.getOneToManyLhs());

    assertEquals(true, lhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs3));

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs3));
    assertEquals(oneToManyLhs, rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs3.getOneToManyLhs());

     */
  }

  @Test
  public void testSomeToManySync() {
    AssociationsMock.init();


    OneToManyLhs lhs = AssociationsManager.oneToManyLhsBuilder().build().get();

    OneToManyRhs rhs1 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhs).build().get();
    OneToManyRhs rhs2 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhs).build().get();
    OneToManyRhs rhs3 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhs).build().get();


    assertEquals(lhs, rhs1.getOneToManyLhs());
    assertEquals(lhs, rhs2.getOneToManyLhs());
    assertEquals(lhs, rhs3.getOneToManyLhs());

    assertEquals(true, lhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs3));

    /* TODO
    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs3));
    assertEquals(oneToManyLhs, rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs3.getOneToManyLhs());
    */
  }

  @Test
  public void testSomeToSomeSync() {
    AssociationsMock.init();

    /* TODO
    OneToManyLhsBuilder lhsBuilder = AssociationsManager.oneToManyLhsBuilder();

    OneToManyRhs rhs1 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();
    OneToManyRhs rhs2 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();
    OneToManyRhs rhs3 = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(lhsBuilder).build().get();

    OneToManyLhs lhs = lhsBuilder.build().get();

    assertEquals(lhs, rhs1.getOneToManyLhs());
    assertEquals(lhs, rhs2.getOneToManyLhs());
    assertEquals(lhs, rhs3.getOneToManyLhs());

    assertEquals(true, lhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, lhs.getOneToManyRhs().contains(rhs3));

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhs1, rhs2, rhs3)).build().get();


    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs1));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs2));
    assertEquals(true, oneToManyLhs.getOneToManyRhs().contains(rhs3));
    assertEquals(oneToManyLhs, rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs, rhs3.getOneToManyLhs());
    */
  }


}
