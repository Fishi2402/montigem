/* (c) https://github.com/MontiCore/monticore */
package restaurantorg;

public class RestaurantOrgBaseClassObjectMapper extends RestaurantOrgBaseClassObjectMapperTOP {
  public RestaurantOrgBaseClassObjectMapper() {
    this.baseDeSers.put("java.time.LocalDate", new LocalDateDeser());
  }
}
