package hardy;

//+ GEN-T        : handwritten, testet generiertes product

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonBuilderTest {

    private PersonBuilder personBuilder;
    private PersonValidator validator;

    @Before
    public void before() {
        this.validator = new PersonValidator();
        this.personBuilder = new PersonBuilder(1001L, validator);
    }

    @Test
    public void testBuildValidPerson(){
        personBuilder.age(18).nickname("Hardy").build();
        assertTrue(validator.log.isEmpty());
    }

    @Test
    public void testBuildInvalidPersonNoNickname(){
        DataConsistencyViolationException dataConsistencyViolationException = null;
        try {
            personBuilder.age(18).build();
        }catch (DataConsistencyViolationException e){
            dataConsistencyViolationException = e;
        }

        assertNotNull(dataConsistencyViolationException);
        assertEquals(1, validator.log.size());

        LogEntry e = validator.log.getEntry(0);
        assertEquals("nickname not defined", e.getMessage());
    }
}
