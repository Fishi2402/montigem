package dtcockpit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DerivedInfoImpl extends DerivedInfoImplTOP {
  @Override
  public int getSomeNumber() {
    return 42;
  }


  @Override
  public String getSomeString() {
    return "None";
  }


  @Override
  public List<String> getManyStrings() {
    return Arrays.asList("a", "b", "c");
  }

  @Override
  public MessageOfTheDay getRandomMSG() {
    return DtcockpitManager.getMessageOfTheDayList().iterator().next();
  }

  @Override
  public List<Meeting> getMonthlyMeetings() {
    return DtcockpitManager.getMeetingList();
  }

  @Override
  public Meeting getImportantMeeting() {
    return DtcockpitManager.getMeetingList().iterator().next();
  }

  @Override
  public Meeting getRandomMeeting() {
    return null;
  }

  @Override
  public int sizeManyStrings() {
    // TODO: Do we need this method?
    return getManyStrings().size();
  }

  @Override
  public int sizeMonthlyMeetings() {
    // TODO: Do we need this method?
    return this.getMonthlyMeetings().size();
  }
}
