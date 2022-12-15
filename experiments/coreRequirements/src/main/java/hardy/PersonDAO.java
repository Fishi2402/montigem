package hardy;

// GEN-P


import umlp.rte.DatabaseAccessManager;
import umlp.rte.Gem;
import umlp.rte.LoadResult;
import umlp.rte.LoadingState;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// Provides methods to change database entries related to the Person class
// There should be a small amount of instances that is not related to the number of Person instances
public class PersonDAO {
  protected PreparedStatement selectPerson = null;
  protected PreparedStatement selectPerson_AssocMainResidence = null;
  protected PreparedStatement insertPerson = null;
  protected PreparedStatement insertPerson_AssocMainResidence = null;
  protected PreparedStatement setAgeStatement = null;
  protected PreparedStatement setNickname = null;
  protected PreparedStatement addVisited = null;
  protected PreparedStatement removeVisited = null;
  protected PreparedStatement setVisited = null;

  // TODO: Assocs

  public Optional<Person> selectPerson(long gemId){
    if(selectPerson == null){
      selectPerson = DatabaseAccessManager.prepareStatement("SELECT id, age, nickname FROM person WHERE id = ?");
      selectPerson_AssocMainResidence = DatabaseAccessManager.prepareStatement("SELECT person_id, address_id FROM assoc_main_residence WHERE person_id = ?");
    }

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

        // Assoc main residence
        // TODO: extract
        selectPerson_AssocMainResidence.clearParameters();
        selectPerson_AssocMainResidence.setLong(1, gemId);
        ResultSet res2 = selectPerson_AssocMainResidence.executeQuery();
        // For owns: while instead of if
        if(res2.next()){
          long address_id = res2.getLong("address_id");
          builder.mainResidenceGem(new Gem<>(address_id, () -> {
            Optional<Address> aOpt = FlensburgManager.addressOpt(address_id);
            if(aOpt.isPresent()){
              return new LoadResult<>(aOpt.get());
            }else{
              return new LoadResult<>(LoadingState.FAILED_TO_LOAD);
            }
          }));
        }

        return builder.build();
      }else{
        return Optional.empty();
      }
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public void insertPerson(Person p){
    if(insertPerson == null){
      insertPerson = DatabaseAccessManager.prepareStatement("INSERT INTO person (id, age, nickname) VALUES (?, ?, ?)");
      // TODO: one dao per table/assoc?
      insertPerson_AssocMainResidence = DatabaseAccessManager.prepareStatement("INSERT INTO assoc_main_residence (person_id, address_id) VALUES (?, ?)");
    }

    try{
      insertPerson.clearParameters();
      insertPerson.setLong(1, p.getGemId());
      insertPerson.setInt(2, p.getAge());
      insertPerson.setString(3, p.getNickname());
      insertPerson.execute();

      insertPerson_AssocMainResidence.clearParameters();
      insertPerson_AssocMainResidence.setLong(1, p.getGemId());
      insertPerson_AssocMainResidence.setLong(2, p.getMainResidenceGem().getGemId());
      insertPerson_AssocMainResidence.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
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

  public void addVisited(long gemId, long addressGemId, int indexInList) {
    // TODO: use index
    if(addVisited == null){
      addVisited = DatabaseAccessManager.prepareStatement("INSERT INTO assoc_visits (visitor_id,  visited_id) VALUES (?, ?)");
    }

    try {
      addVisited.clearParameters();
      addVisited.setLong(1, gemId);
      addVisited.setLong(2, addressGemId);
      addVisited.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }

  public void removeVisited(long gemId, long addressGemId, int indexInList) {
    // TODO: use index
    if(removeVisited == null){
      removeVisited = DatabaseAccessManager.prepareStatement("DELETE FROM assoc_visits WHERE visitor_id = ? AND visited_id = ?");
    }

    try {
      removeVisited.clearParameters();
      removeVisited.setLong(1, gemId);
      removeVisited.setLong(2, addressGemId);
      removeVisited.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }

  public void updateVisited(long gemId, Long oldAddressGemId, long addressGemId, int index) {
    // TODO: use index
    if(setVisited == null){
      setVisited = DatabaseAccessManager.prepareStatement("UPDATE assoc_visits SET visited_id = ? WHERE visitor_id = ? AND visited_id = ?");
    }

    try {
      setVisited.clearParameters();
      setVisited.setLong(1, addressGemId);
      setVisited.setLong(2, gemId);
      setVisited.setLong(3, oldAddressGemId);
      setVisited.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }
  
}
