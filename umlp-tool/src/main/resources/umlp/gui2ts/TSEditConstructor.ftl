<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ids")}
<#assign route="route">
<#assign router="router">
public constructor(${route}: ActivatedRoute, ${router}: Router) {
  super();
  super.${router} = ${router};
  <#list ids as id>
    this.${id} = +${route}.snapshot.paramMap.get("${id}");
  </#list>
}
