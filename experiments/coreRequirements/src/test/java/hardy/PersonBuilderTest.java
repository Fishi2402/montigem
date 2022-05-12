package hardy;

//+ GEN-T        : handwritten, testet generiertes product

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class PersonBuilderTest {

    private PersonBuilder personBuilder;
    private PersonValidator validator;

    @Before
    public void before() {
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
        assertTrue(validator.log.isEmpty());

        Optional<Person> personOpt = FlensburgManager.personOpt(id);

        assertTrue(personOpt.isPresent());
        Person person = personOpt.get();

        assertEquals(name, person.getNickname());
        assertEquals(age, person.getAge());
    }

    @Test
    public void testBuildInvalidPersonNoNickname(){
        int id = 1001;
        FlensburgMock.secretlyAdaptnextGemIds(id);

        IllegalStateException illegalStateException = null;
        try {
            personBuilder.age(18).build();
        }catch (IllegalStateException e){
            illegalStateException = e;
        }

        assertNotNull(illegalStateException);
        assertEquals(1, validator.log.size());

        LogEntry e = validator.log.getEntry(0);
        assertEquals("nickname not defined", e.getMessage());

        assertFalse(FlensburgManager.personOpt(id).isPresent());
    }
}
