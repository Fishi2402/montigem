/* (c) https://github.com/MontiCore/monticore */
package hardy;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Long> {
  List<Person> findAllByAgeAfter(Long age);
}
