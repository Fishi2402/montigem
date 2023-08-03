package associations;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BidirectionalAssociationsTest {

  // TODO: Impl setting the other side
  // TODO: Filter attributes for "umlp.builder.IsBuildable"

  @Test
  public void testOptToOptSync(){
    AssociationsMock.init();

    OptToOptLhs lhs = AssociationsManager.optToOptLhsBuilder().build().get();
    OptToOptRhs rhs = AssociationsManager.optToOptRhsBuilder().build().get();

    lhs.setOptToOptRhs(rhs);
    assertEquals(rhs, lhs.getOptToOptRhs());
  }


  @Test
  public void testOptToOneSync(){
    AssociationsMock.init();

    OptToOneRhs rhs = AssociationsManager.optToOneRhsBuilder().build().get();
    OptToOneLhs lhs = AssociationsManager.optToOneLhsBuilder().optToOneRhs(rhs).build().get();

    assertEquals(rhs, lhs.getOptToOneRhs());
  }


  @Test
  public void testOptToManySync(){
    AssociationsMock.init();

    OptToManyLhsBuilder lhsBuilder = AssociationsManager.optToManyLhsBuilder();

    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
    OptToManyRhs rhs2 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();
    OptToManyRhs rhs3 = AssociationsManager.optToManyRhsBuilder().optToManyLhs(lhsBuilder).build().get();

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

    assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs1) );
    assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs2) );
    assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs3) );
    assertEquals(optToManyLhs , rhs1.getOptToManyLhs());
    assertEquals(optToManyLhs , rhs2.getOptToManyLhs());
    assertEquals(optToManyLhs , rhs3.getOptToManyLhs());

  }


  @Test
  public void testOptToSomeSync(){
    AssociationsMock.init();

    OptToSomeLhsBuilder lhsBuilder = AssociationsManager.optToSomeLhsBuilder();

    OptToSomeRhs rhs1 = AssociationsManager.optToSomeRhsBuilder().optToSomeLhs(lhsBuilder).build().get();
    OptToSomeRhs rhs2 = AssociationsManager.optToSomeRhsBuilder().optToSomeLhs(lhsBuilder).build().get();
    OptToSomeRhs rhs3 = AssociationsManager.optToSomeRhsBuilder().build().get();

    OptToSomeLhs lhs = lhsBuilder.optToSomeRhs(rhs1).build().get();
   //  lhs = lhsBuilder.optToSomeRhs(rhs2).build().get();
   //  lhs = lhsBuilder.optToSomeRhs(rhs2).build().get();



    assertEquals(lhs, rhs1.getOptToSomeLhs());
    assertEquals(lhs, rhs2.getOptToSomeLhs());
    assertEquals(lhs, rhs3.getOptToSomeLhs());

    assertEquals( rhs1, lhs.getOptToSomeRhs());
    //assertEquals(true, lhs.getOptToSomeRhs().contains(rhs2));
    //assertEquals(true, lhs.getOptToSomeRhs().contains(rhs3));

    //Other side:


  }


  @Test
  public void testOneToOptSync(){
    AssociationsMock.init();

    OneToOptLhsBuilder lhsBuilder = AssociationsManager.oneToOptLhsBuilder();
    OneToOptRhs rhs = AssociationsManager.oneToOptRhsBuilder().oneToOptLhs(lhsBuilder).build().get();
    OneToOptLhs lhs = lhsBuilder.oneToOptRhs(rhs).build().get();
    assertEquals(rhs, lhs.getOneToOptRhs());

  }


  @Test
  public void testOneToOneSync(){
    AssociationsMock.init();

    OneToOneLhsBuilder lhsBuilder = AssociationsManager.oneToOneLhsBuilder();
    OneToOneRhs rhs = AssociationsManager.oneToOneRhsBuilder().oneToOneLhs(lhsBuilder).build().get();
    OneToOneLhs lhs = lhsBuilder.oneToOneRhs(rhs).build().get();
    assertEquals(rhs, lhs.getOneToOneRhs());
  }


  @Test
  public void testOneToManySync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }


  @Test
  public void testOneToSomeSync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }


  @Test
  public void testManyToOptSync(){
    AssociationsMock.init();

    ManyToOptRhsBuilder rhsBuilder = AssociationsManager.manyToOptRhsBuilder();

    ManyToOptLhs lhs1 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhsBuilder).build().get();
    ManyToOptLhs lhs2 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhsBuilder).build().get();
    ManyToOptLhs lhs3 = AssociationsManager.manyToOptLhsBuilder().manyToOptRhs(rhsBuilder).build().get();

    ManyToOptRhs rhs = rhsBuilder.build().get();

    assertEquals(rhs, lhs1.getManyToOptRhs());
    assertEquals(rhs, lhs2.getManyToOptRhs());
    assertEquals(rhs, lhs3.getManyToOptRhs());

    assertEquals(lhs1, rhs.getManyToOptLhs());
    assertEquals(lhs2, rhs.getManyToOptLhs());
    assertEquals(lhs3, rhs.getManyToOptLhs());

    OptToManyRhsBuilder rhsBuilder1 = AssociationsManager.optToManyRhsBuilder();
    OptToManyRhsBuilder rhsBuilder2 = AssociationsManager.optToManyRhsBuilder();
    OptToManyRhsBuilder rhsBuilder3 = AssociationsManager.optToManyRhsBuilder();

    OptToManyLhs optToManyLhs = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

   // rhs1 = rhsBuilder1.build().get();
    //rhs2 = rhsBuilder2.build().get();
    //rhs3 = rhsBuilder3.build().get();

   // assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs2) );
//    assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs3) );
  //  assertEquals(true , optToManyLhs.getOptToManyRhs().contains(rhs1) );
//    assertEquals(optToManyLhs , rhs1.getOptToManyLhs());
//    assertEquals(optToManyLhs , rhs2.getOptToManyLhs());
//    assertEquals(optToManyLhs , rhs3.getOptToManyLhs());

  }

  @Test
  public void testManyToOneSync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }

  @Test
  public void testManyToManySync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }

  @Test
  public void testManyToSomeSync(){
    AssociationsMock.init();

    OptToManyLhsBuilder lhsBuilder = AssociationsManager.optToManyLhsBuilder();

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

  @Test
  public void testSomeToOptSync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }

  @Test
  public void testSomeToOneSync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }

  @Test
  public void testSomeToManySync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }

  @Test
  public void testSomeToSomeSync(){
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

    OneToManyRhsBuilder rhsBuilder1 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder2 = AssociationsManager.oneToManyRhsBuilder();
    OneToManyRhsBuilder rhsBuilder3 = AssociationsManager.oneToManyRhsBuilder();

    OneToManyLhs oneToManyLhs = AssociationsManager.oneToManyLhsBuilder().oneToManyRhs(Sets.newHashSet(rhsBuilder1, rhsBuilder2, rhsBuilder3)).build().get();

    rhs1 = rhsBuilder1.build().get();
    rhs2 = rhsBuilder2.build().get();
    rhs3 = rhsBuilder3.build().get();

    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs1) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs2) );
    assertEquals(true , oneToManyLhs.getOneToManyRhs().contains(rhs3) );
    assertEquals(oneToManyLhs , rhs1.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs2.getOneToManyLhs());
    assertEquals(oneToManyLhs , rhs3.getOneToManyLhs());
  }




}
