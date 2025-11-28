<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "cdName", "attributes", "optionalAttributes", "listAttributes", "listAssocs", "gemAssocs")}
${cd4c.method("public Optional<${typeName}> select${typeName}(long gemId)")}
  ensureSelect${typeName}Created();
  try(Session session = DatabaseAccessManager4Neo4j.getSession()) {
    org.neo4j.driver.Result res = session.run(select${typeName}, Values.parameters("p1", gemId));
    if(res.hasNext()){
      Optional<${typeName}> obj = unpack${typeName}(res.next());
      return obj;
    }else{
      return Optional.empty();
    }
  }

