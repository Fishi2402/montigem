<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "attributeNames", "leftAssociations", "rightAssociations")}
${cd4c.method("public ${type?cap_first} merge(${type?cap_first} other)")}

<#list attributeNames as attribute>
  this.${attribute}ValidationMessages.push(...other.${attribute}ValidationMessages);
</#list>
  let otherSize = 0;
<#list rightAssociations as association>
  <#assign name = association.getName()>
  <#if association.getRight().getMCQualifiedType().printType() == association.getLeft().getMCQualifiedType().printType()>
    <#assign name = "right" + name?cap_first>
  </#if>
  <#if association.getRight().getCDCardinality().isMult()>
  otherSize = other.get${name?cap_first}ValidationResults().length;
  if(otherSize !== 0) {
    let thisSize = this.get${name?cap_first}ValidationResults().length;
    if(thisSize === 0) {
      this.${name}ValidationResults = other.get${name?cap_first}ValidationResults();
    } else if(thisSize !== otherSize) {
      throw new Error("Cannot merge results for right association ${name} because the number of results is not equal.");
    } else {
      for(let i = 0; i < this.get${name?cap_first}ValidationResults().length; i++) {
        this.get${name?cap_first}ValidationResults()[i] = this.get${name?cap_first}ValidationResults()[i].merge(other.get${name?cap_first}ValidationResults()[i]);
      }
    }
  }
  <#else>
  if(other.has${name?cap_first}ValidationResult()) {
    if(this.has${name?cap_first}ValidationResult()) {
      this.set${name?cap_first}ValidationResult(this.get${name?cap_first}ValidationResult().merge(other.get${name?cap_first}ValidationResult()));
    } else {
      this.set${name?cap_first}ValidationResult(other.get${name?cap_first}ValidationResult());
    }
  }
  </#if>
</#list>

<#list leftAssociations as association>
  <#assign type = association.getLeft().getMCQualifiedType().printType()>
  <#assign name = association.getName()>
  <#if type == association.getRight().getMCQualifiedType().printType()>
    <#assign name = "left" + name?cap_first>
  </#if>
  <#if association.getLeft().getCDCardinality().isMult()>
  otherSize = other.get${name?cap_first}ValidationResults().length;
  if(otherSize !== 0) {
    let thisSize = this.get${name?cap_first}ValidationResults().length;
    if(thisSize === 0) {
      this.${name}ValidationResults = other.get${name?cap_first}ValidationResults();
    } else if(thisSize !== otherSize) {
      for(const ${name}ValidationResult of other.get${name?cap_first}ValidationResults()) {
        let otherId = ${name}ValidationResult.getDomainObjectId();
        if(otherId === 0) {
          throw new Error("Cannot merge results for left association ${name} because the number of results is not equal and there are results with id 0.");
        } else {
          let existing = this.get${name?cap_first}ValidationResults().find((r) => r.getDomainObjectId() === otherId);
          if(existing === undefined) {
            this.add${name?cap_first}ValidationResult(${name}ValidationResult);
          } else {
            let index = this.get${name?cap_first}ValidationResults().indexOf(existing);
            this.get${name?cap_first}ValidationResults()[index] = existing.merge(${name}ValidationResult);
          }
        }
      }
    } else {
      for(let i = 0; i < this.get${name?cap_first}ValidationResults().length; i++) {
        this.get${name?cap_first}ValidationResults()[i] = this.get${name?cap_first}ValidationResults()[i].merge(other.get${name?cap_first}ValidationResults()[i]);
      }
    }
  }
  <#else>
  if(other.has${name?cap_first}ValidationResult()) {
    if(this.has${name?cap_first}ValidationResult()) {
      this.set${name?cap_first}ValidationResult(this.get${name?cap_first}ValidationResult().merge(other.get${name?cap_first}ValidationResult()));
    } else {
      this.set${name?cap_first}ValidationResult(other.get${name?cap_first}ValidationResult());
    }
  }
  </#if>
</#list>
return this;
