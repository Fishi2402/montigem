package loadingtests.ocl;

public class QueryBuilder {
  protected String _select;
  protected String _from;

  public QueryBuilder select(String... select) {
    StringBuilder sb = new StringBuilder();
    String sep = "";
    for (String s : select) {
      sb.append(s).append(sep);
      sep = ",";
    }
    this._select = sb.toString();
    return this;
  }

  public QueryBuilder from(String from) {
    this._from = from;
    return this;
  }

  public String build() {
    return "select " + _select + " from " + _from;
  }

}
