package dtcockpit;

import umlp.backendrte.common.Gem;
import umlp.backendrte.common.GemList;

import java.util.List;

public class DerivedInfo extends DerivedInfoTOP {

  @Override
  public int getSomeNumber() {
    return 42;
  }

  @Override
  public String getSomeString() {
    return "None";
  }

  @Override
  public List<String> getManyStringsList() {
    return List.of();
  }

  @Override
  public MessageOfTheDay getRandomMSG() {
    return DtcockpitManager.getMessageOfTheDayList().iterator().next();
  }

  @Override
  public List<Meeting> getMonthlyMeetingsList() {
    return DtcockpitManager.getMeetingList();
  }

  @Override
  public GemList<Meeting> getMonthlyMeetingsDerivedGemList() {
    return null;
  }

  @Override
  public Meeting getImportantMeeting() {
    return DtcockpitManager.getMeetingList().iterator().next();
  }

  @Override
  public Gem<Meeting> getImportantMeetingDerivedGem() {
    return getImportantMeetingGem();
  }

  @Override
  public Meeting getRandomMeeting() {
    return null;
  }

  @Override
  public Gem<Meeting> getRandomMeetingDerivedGem() {
    return getImportantMeetingGem();
  }


}
