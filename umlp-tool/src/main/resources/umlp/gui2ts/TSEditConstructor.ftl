<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ids")}
<#assign route="route">
<#assign router="router">
public constructor(${route}: ActivatedRoute, protected ${router}: Router) {
  <#list ids as id>
    this.${id} = +${route}.snapshot.paramMap.get("${id}");
  </#list>
}
