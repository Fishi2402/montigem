<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "associations")}
${cd4c.method("public void delete${typeName}(long gemId)")}

if(delete${typeName} == null){
  delete${typeName} = "MATCH(n:${typeName} {gemId: $p1}) DETACH DELETE n";
}
DatabaseAccessManager4Neo4j.executeWrite(delete${typeName},Values.parameters("p1", gemId));

