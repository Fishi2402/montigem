/* (c) https://github.com/MontiCore/monticore */
package hardy;

// GEN-P


import umlp.rte.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Provides methods to change database entries related to the Person class
// There should be a small amount of instances that is not related to the number of Person instances
public class PersonDAO {
  // TODO: extract main residence into AssocMainResidenceDAO
  protected PreparedStatement selectPerson = null;
  protected PreparedStatement insertPerson = null;
  protected PreparedStatement setAgeStatement = null;
  protected PreparedStatement setNickname = null;

  AssocVisitsDAO assocVisitsDAO = new AssocVisitsDAO();
  AssocMainResidenceDAO assocMainResidenceDAO = new AssocMainResidenceDAO();

  public Optional<Person> selectPerson(long gemId){
    ensureSelectPersonCreated();

    try{
      selectPerson.clearParameters();
      selectPerson.setLong(1, gemId);
      ResultSet res = selectPerson.executeQuery();
      if(res.next()){
        PersonBuilder builder = FlensburgManager.personBuilder(gemId);
        builder.setINTERNAL_deser_mode(true);
        builder
            .age(res.getInt("age"))
            .nickname(res.getString("nickname"));

        Optional<Long> mainResidenceIdOpt = assocMainResidenceDAO.selectAddressIdForPerson(gemId);
        if(mainResidenceIdOpt.isPresent()) {
          builder.mainResidenceGem(new Gem<>(mainResidenceIdOpt.get(), () -> {
            Optional<Address> aOpt = FlensburgManager.addressOpt(mainResidenceIdOpt.get());
            if(aOpt.isPresent()){
              return new LoadResult<>(aOpt.get());
            }else{
              return new LoadResult<>(LoadingState.FAILED_TO_LOAD);
            }
          }));
        }

        builder.visitedGemListIds(assocVisitsDAO.selectVisitedIdsForVisitor(gemId));

        return builder.build();
      }else{
        return Optional.empty();
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureSelectPersonCreated() {
    if(selectPerson == null){
      selectPerson = DatabaseAccessManager.prepareStatement("SELECT id, age, nickname FROM person WHERE id = ?");
    }
  }

  public void insertPerson(Person p){
    ensureInsertPersonCreated();

    try{
      insertPerson.clearParameters();
      insertPerson.setLong(1, p.getGemId());
      insertPerson.setInt(2, p.getAge());
      insertPerson.setString(3, p.getNickname());
      insertPerson.execute();

      assocMainResidenceDAO.insertMainResidence(p.getGemId(), p.getMainResidenceGem().getGemId(), 0);

      int i = 0;
      List<AssociationDBEntry> entries = new ArrayList<>();
      for (Long visitedId : p.getVisitedGemList().getIDs()) {
        entries.add(new AssociationDBEntry(p.getGemId(), visitedId, i++));
      }
      // Bulk operation => only one call to db
      assocVisitsDAO.addAllVisits(entries);
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureInsertPersonCreated() {
    if(insertPerson == null){
      insertPerson = DatabaseAccessManager.prepareStatement("INSERT INTO person (id, age, nickname) VALUES (?, ?, ?)");
    }
  }

  public void updateAge(long gemId, int age) {
    if(setAgeStatement == null){
      setAgeStatement = DatabaseAccessManager.prepareStatement("UPDATE person SET age = ? WHERE id = ?");
    }

    try {
      setAgeStatement.clearParameters();
      setAgeStatement.setInt(1, age);
      setAgeStatement.setLong(2, gemId);
      setAgeStatement.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }

  public void updateNickname(long gemId, String nickname) {
    if(setNickname == null){
      setNickname = DatabaseAccessManager.prepareStatement("UPDATE person SET nickname = ? WHERE id = ?");
    }

    try {
      setNickname.clearParameters();
      setNickname.setString(1, nickname);
      setNickname.setLong(2, gemId);
      setNickname.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }
}
