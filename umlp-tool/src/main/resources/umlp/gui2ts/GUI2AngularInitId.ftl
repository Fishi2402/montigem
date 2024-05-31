<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("id")}

if(${route}.snapshot.paramMap.get("${id}") != null) {
  this.${id} = +${route}.snapshot.paramMap.get("${id}");
}