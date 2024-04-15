<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ids")}
<#assign route="route">
<#assign router="router">
public constructor(${route}: ActivatedRoute, ${router}: Router) {
  super();
  super.${router} = ${router};
  <#list ids as id>
    if(${route}.snapshot.paramMap.get("${id}") != null) {
      this.${id} = +${route}.snapshot.paramMap.get("${id}");
    }
  </#list>
}
