<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory)")}

<#list classList as className>
  if(entityInstance instanceof ${className}Builder){
  return "${className}";
  }
</#list>
return super.determineConcreteSubclassEntityName(entityInstance, factory);