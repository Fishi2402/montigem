package hardy;

// HWC-T
// HWC-P: Will be converted to frontend code

public class PersonPageSim extends PersonPageSimTOP {
  public PersonPageSim(Person p) {
    super(p);
  }

  @Override
  public void decreaseAge() {
    p.setAge(p.getAge() - 1);
    FlensburgManager.updatePerson(p);
  }
}
