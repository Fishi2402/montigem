package hardy;

//+ GEN-T        : handwritten, testet generiertes product

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import umlp.rte.ValidationMessage;

import java.util.Optional;

import static org.junit.Assert.*;

public class PersonBuilderTest {

    private PersonBuilder personBuilder;
    private PersonValidator validator;

    @Before
    public void before() {
        Log.init();
        Log.enableFailQuick(false);
        FlensburgMock.init();
        this.validator = new PersonValidator();
        this.personBuilder = new PersonBuilder(1001L, validator);
    }

    @Test
    public void testBuildValidPerson(){
        int id = 1001;
        FlensburgMock.secretlyAdaptnextGemIds(id);

        String name = "Hardy";
        int age = 18;
        personBuilder.age(age).nickname(name).build();
        assertTrue(Log.getFindings().isEmpty());

        Optional<Person> personOpt = FlensburgManager.personOpt(id);

        assertTrue(personOpt.isPresent());
        Person person = personOpt.get();

        assertEquals(name, person.getNickname());
        assertEquals(age, person.getAge().intValue());
    }

    @Test
    public void testBuildInvalidPersonNoNickname(){
        int id = 1001;
        FlensburgMock.secretlyAdaptnextGemIds(id);

        Optional<Person> personOpt = personBuilder.age(18).build();

        assertFalse(personOpt.isPresent());
        assertEquals(1, personBuilder.getValidationMessages().size());

        ValidationMessage e = personBuilder.getValidationMessages().get(0);
        assertEquals("nickname not defined", e.getMessage());

        assertFalse(FlensburgManager.personOpt(id).isPresent());
    }
}
