package hardy;

import umlp.rte.DatabaseAccessManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssocVisitsDAO {
  protected PreparedStatement addVisits = null;
  protected PreparedStatement removeVisits = null;
  protected PreparedStatement setVisits = null;
  protected PreparedStatement selectVisitedIdsForVisitor = null;
  protected PreparedStatement selectVisitorIdsForVisited = null;

  private void ensureAddVisitsCreated() {
    if(addVisits == null){
      addVisits = DatabaseAccessManager.prepareStatement("INSERT INTO assoc_visits (visitor_id,  visited_id) VALUES (?, ?)");
    }
  }

  public void addVisits(long visitorGemId, long visitedGemId, int indexInList) {
    // TODO: use index
    ensureAddVisitsCreated();

    try {
      addVisits.clearParameters();
      addVisits.setLong(1, visitorGemId);
      addVisits.setLong(2, visitedGemId);
      addVisits.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  public List<Long> selectVisitedIdsForVisitor(long visitorId){
    ensureSelectVisitedIdsForVisitorCreated();

    try {
      selectVisitedIdsForVisitor.clearParameters();
      selectVisitedIdsForVisitor.setLong(1, visitorId);
      List<Long> res = new ArrayList<>();
      ResultSet rs = selectVisitedIdsForVisitor.executeQuery();
      while(rs.next()){
        res.add(rs.getLong(1));
      }
      return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void ensureSelectVisitedIdsForVisitorCreated(){
    if(selectVisitedIdsForVisitor == null){
      selectVisitedIdsForVisitor = DatabaseAccessManager.prepareStatement("SELECT visited_id FROM assoc_visits WHERE visitor_id = ?;");
    }
  }

  public List<Long> selectVisitorIdsForVisited(long visitedId){
    ensureSelectVisitorIdsForVisitedCreated();

    try {
      selectVisitorIdsForVisited.clearParameters();
      selectVisitorIdsForVisited.setLong(1, visitedId);
      List<Long> res = new ArrayList<>();
      ResultSet rs = selectVisitorIdsForVisited.executeQuery();
      while(rs.next()){
        res.add(rs.getLong(1));
      }
      return res;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void ensureSelectVisitorIdsForVisitedCreated(){
    if(selectVisitorIdsForVisited == null){
      selectVisitorIdsForVisited = DatabaseAccessManager.prepareStatement("SELECT visitor_id FROM assoc_visits WHERE visited_id = ?;");
    }
  }

  public void removeVisits(long gemId, long addressGemId, int indexInList) {
    // TODO: use index
    ensureRemoveVisitsCreated();

    try {
      removeVisits.clearParameters();
      removeVisits.setLong(1, gemId);
      removeVisits.setLong(2, addressGemId);
      removeVisits.execute();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

  private void ensureRemoveVisitsCreated() {
    if(removeVisits == null){
      removeVisits = DatabaseAccessManager.prepareStatement("DELETE FROM assoc_visits WHERE visitor_id = ? AND visited_id = ?");
    }
  }

  public void updateVisits(long gemId, Long oldAddressGemId, long addressGemId, int index) {
    // TODO: use index
    ensureUpdateVisitsCreated();

    try {
      setVisits.clearParameters();
      setVisits.setLong(1, addressGemId);
      setVisits.setLong(2, gemId);
      setVisits.setLong(3, oldAddressGemId);
      setVisits.execute();
    }catch (SQLException e){
      // TODO: how do we handle sql errors
      throw new RuntimeException(e);
    }
  }

  private void ensureUpdateVisitsCreated() {
    if(setVisits == null){
      setVisits = DatabaseAccessManager.prepareStatement("UPDATE assoc_visits SET visited_id = ? WHERE visitor_id = ? AND visited_id = ?");
    }
  }


}
