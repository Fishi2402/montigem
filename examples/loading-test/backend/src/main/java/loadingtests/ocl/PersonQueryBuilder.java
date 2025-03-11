package loadingtests.ocl;

import umlp.backendrte.ocl.QueryBuilder;

public class PersonQueryBuilder extends QueryBuilder {

  public String build() {
    return "SELECT "
        + printAttr("id") + ", "
        + printAttr("name") + ", "
        + printAttr("age")
        + " FROM "
        + printTable("person") + " "
        + printWhere();
  }

}
