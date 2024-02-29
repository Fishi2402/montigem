package dtcockpit;

import umlp.backendrte.common.NotificationScope;

import java.util.Optional;
public class DtcockpitManagerNoRoomDBImpl extends DtcockpitManagerImpl {

  @Override
  protected  void _doAddRoom (Room o, NotificationScope scope) {
    knownRooms.put(o.getGemId(), o);
  }

  @Override
  protected Optional<Room> _doRoomOpt (long gemId) {
    if(knownRooms.containsKey(gemId)){
      return Optional.ofNullable(knownRooms.get(gemId));
    }
    return Optional.empty();
  }
  public  static  void init () {
    DtcockpitDAOFactory.init();
    DtcockpitManagerNoRoomDBImpl impl = new DtcockpitManagerNoRoomDBImpl();
    impl.dtcockpitObservers.add(new DtcockpitDbObserver());
    DtcockpitManagerNoRoomDBImpl.setInstance(impl);
  }
}

