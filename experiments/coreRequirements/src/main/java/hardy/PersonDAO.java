package hardy;

// GEN-P


import umlp.rte.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// Provides methods to change database entries related to the Person class
// There should be a small amount of instances that is not related to the number of Person instances
public class PersonDAO {
  protected PreparedStatement insertPerson = null;
  protected PreparedStatement setAgeStatement = null;
  protected PreparedStatement setNickname = null;
  protected PreparedStatement addVisited = null;
  protected PreparedStatement removeVisited = null;
  protected PreparedStatement setVisited = null;

  public void insertPerson(Person p){
    if(insertPerson == null){
      insertPerson = DatabaseAccessManager.prepareStatement("INSERT INTO person (id, age, nickname) VALUES (?, ?, ?)");
    }

    try{
      insertPerson.clearParameters();
      insertPerson.setLong(1, p.getGemId());
      insertPerson.setInt(2, p.getAge());
      insertPerson.setString(3, p.getNickname());
      insertPerson.execute();
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
