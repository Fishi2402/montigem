<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "attributeNames", "leftAssociations", "rightAssociations")}
${cd4c.method("public ${type?cap_first} merge(${type?cap_first} other)")}

<#list attributeNames as attribute>
  this.${attribute}ValidationMessages.addAll(other.${attribute}ValidationMessages);
</#list>
  int otherSize = 0;
<#list rightAssociations as association>
  <#assign name = association.getName()>
  <#if association.getRight().getMCQualifiedType().printType() == association.getLeft().getMCQualifiedType().printType()>
    <#assign name = "right" + name?cap_first>
  </#if>
  <#if association.getRight().getCDCardinality().isMult()>
  otherSize = other.get${name?cap_first}ValidationResults().size();
  if(otherSize != 0) {
    int thisSize = this.get${name?cap_first}ValidationResults().size();
    if(thisSize == 0) {
      this.${name}ValidationResults = other.get${name?cap_first}ValidationResults();
    } else if(thisSize != otherSize) {
      throw new IllegalArgumentException("Cannot merge results for right association ${name} because the number of results is not equal.");
    } else {
      for(int i = 0; i < this.get${name?cap_first}ValidationResults().size(); i++) {
        this.get${name?cap_first}ValidationResults().set(i, this.get${name?cap_first}ValidationResults().get(i).merge(other.get${name?cap_first}ValidationResults().get(i)));
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
  otherSize = other.get${name?cap_first}ValidationResults().size();
  if(otherSize != 0) {
    int thisSize = this.get${name?cap_first}ValidationResults().size();
    if(thisSize == 0) {
      this.${name}ValidationResults = other.get${name?cap_first}ValidationResults();
    } else if(thisSize != otherSize) {
      for(${type}ValidationResult ${name}ValidationResult : other.get${name?cap_first}ValidationResults()) {
        long otherId = ${name}ValidationResult.getDomainObjectId();
        if(otherId == 0) {
          throw new IllegalArgumentException("Cannot merge results for left association ${name} because the number of results is not equal and there are results with id 0.");
        } else {
          Optional<${type}ValidationResult> existing = Optional.empty();
          for(${type}ValidationResult vr : this.get${name?cap_first}ValidationResults()) {
            if(vr.getDomainObjectId() == otherId) {
              existing = Optional.of(vr);
              break;
            }
          }
          if(existing.isEmpty()) {
            this.add${name?cap_first}ValidationResult(${name}ValidationResult);
          } else {
            int index = this.get${name?cap_first}ValidationResults().indexOf(existing.get());
            this.get${name?cap_first}ValidationResults().set(index, existing.get().merge(${name}ValidationResult));
          }
        }
      }
    } else {
      for(int i = 0; i < this.get${name?cap_first}VcalidationResults().size(); i++) {
        this.get${name?cap_first}ValidationResults().set(i, this.get${name?cap_first}ValidationResults().get(i).merge(other.get${name?cap_first}ValidationResults().get(i)));
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
