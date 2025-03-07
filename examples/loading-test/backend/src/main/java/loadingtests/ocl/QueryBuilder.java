package loadingtests.ocl;

public class QueryBuilder {
  protected String _select;
  protected String _from;

  public void select(String select) {
    this._select = select;
  }

  public void from(String from) {
    this._from = from;
  }

  public String build() {
    return _select + _from;
  }

}
