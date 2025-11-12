package umlp.backendrte.ocl;

public abstract class QueryBuilder {

  protected String _alias = "";
  protected String _where = "";
  protected boolean hasWhere = false;

  public QueryBuilder as(String alias) {
    this._alias = alias;
    return this;
  }

  public QueryBuilder where(String where) {
    if (!hasWhere) {
      this._where = where;
      hasWhere = true;
    }
    else {
      this._where += " AND " + where;
    }
    return this;
  }

  public abstract String build();

  public void clear() {
    this._alias = "";
    this._where = "";
    this.hasWhere = false;
  }

  protected String printAttr(String attr) {
    if (!_alias.isBlank()) {
      if (attr.equals("id")) {
        return _alias + "." + attr + "";
      }
      return _alias + ".\"" + attr + "\"";
    }
    return attr;
  }

  protected String printTable(String table) {
    if (!_alias.isBlank()) {
      return "\""  + table + "\" " + _alias;
    }
    return table;
  }

  protected String printWhere() {
    if (hasWhere) {
      return "WHERE " + _where;
    }
    return "";
  }

}
