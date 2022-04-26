package hardy;

//+ GEN-T        : handwritten, testet generiertes product

import org.junit.Before;
import org.junit.Test;

public class PersonBuilderTest {

    private PersonBuilder personBuilder;

    @Before
    public void before() {
        this.personBuilder = new PersonBuilder();
    }

// todo: das ist kein test! Wenn dann schon anständige Tests machen
    @Test
    public void somethingTest(){
        personBuilder.age(18).nickname("Hardy");
    }
}
