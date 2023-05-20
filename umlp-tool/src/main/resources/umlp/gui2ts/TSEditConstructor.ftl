<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ids")}
<#assign route="route">
public constructor(${route}: ActivatedRoute) {
  <#list ids as id>
    this.${id} = +${route}.snapshot.paramMap.get("${id}");
  </#list>
}
