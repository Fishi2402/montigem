/* (c) https://github.com/MontiCore/monticore */
package attributes;


public  class AttributesBaseClassObjectMapper extends AttributesBaseClassObjectMapperTOP {

  public AttributesBaseClassObjectMapper() {
    this.baseDeSers.put("java.time.Duration", new DurationDeSer());
  }

}
