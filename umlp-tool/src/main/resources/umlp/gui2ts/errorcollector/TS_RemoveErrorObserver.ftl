<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected boolean _removeErrorObserver(${name}ErrorObserver observer)")}

const index = this.observers.indexOf(o);
if (index !== -1) {
  return this.observers.splice(index, 1);
}
return null;