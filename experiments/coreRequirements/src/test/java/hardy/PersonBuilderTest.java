package hardy;

import org.junit.Before;
import org.junit.Test;

public class PersonBuilderTest {

    private PersonBuilder personBuilder;

    @Before
    public void before() {
        this.personBuilder = new PersonBuilder();
    }

    @Test
    public void somethingTest(){
        personBuilder.age(18).nickname("Hardy");
    }
}