package dtcockpit;

import java.util.Arrays;
import java.util.List;

public class DerivedInfoImpl extends DerivedInfoImplTOP {
  @Override
  public int calcSomeNumber() {
    return 42;
  }


  @Override
  public String calcSomeString() {
    return "None";
  }


  @Override
  public List<String> calcManyStrings() {
    return Arrays.asList("a", "b", "c");
  }

  @Override
  public MessageOfTheDay calcRandomMSG() {
    return DtcockpitManager.getMessageOfTheDayList().iterator().next();
  }

  @Override
  public List<Meeting> calcMonthlyMeetings() {
    return DtcockpitManager.getMeetingList();
  }

  @Override
  public Meeting calcImportantMeeting() {
    return DtcockpitManager.getMeetingList().iterator().next();
  }

  @Override
  public Meeting calcRandomMeeting() {
    return null;
  }

  @Override
  public int calcSizeManyStrings() {
    // TODO: Do we need this method?
    return calcManyStrings().size();
  }

  @Override
  public int calcSizeMonthlyMeetings() {
    // TODO: Do we need this method?
    return this.getMonthlyMeetings().size();
  }
}
