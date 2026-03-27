package mydomain;

public class PersonBuilder extends PersonBuilderTOP {
  public PersonBuilder(long gemId) {
    super(gemId);
  }

  @Override
  public boolean isBuildable() {
    return super.isBuildable() /* TODO: other tests fail: & checkOCLConstraints()*/ ;
  }
}
