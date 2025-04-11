<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classList")}
${cd4c.method("protected boolean canDeser(String tp)")}

switch (tp) {
<#list classList as className>
  case "${className}": {
    return true;
  }
</#list>
  default: {
    return super.canDeser(tp);
  }
}
