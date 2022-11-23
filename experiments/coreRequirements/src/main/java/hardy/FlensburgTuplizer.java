package hardy;

// GEN-P

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.PojoEntityTuplizer;

// Hibernate kann die richtige Klasse nicht finden, wenn ein Objekt der entsprechenden Builder-Klasse übergeben wird,
// was durch Überschreiben von determineConcreteSubclassEntityName behoben wird.
public class FlensburgTuplizer extends PojoEntityTuplizer {
  public FlensburgTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
    super(entityMetamodel, mappedEntity);
  }

  @Override
  public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
    // Durch das vorgegebene interface ist instanceof hier unvermeidbar
    if(entityInstance instanceof HumanBuilder){
      return "hardy.Human";
    }

    if(entityInstance instanceof PersonBuilder){
      return "hardy.Person";
    }

    if(entityInstance instanceof AddressBuilder){
      return "hardy.Address";
    }

    if(entityInstance instanceof CarBuilder){
      return "hardy.Car";
    }

    return super.determineConcreteSubclassEntityName(entityInstance, factory);
  }
}
