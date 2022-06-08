package hardy;

//+ GEN-T        : handwritten, testet generiertes product

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PersonBuilderTest {

    private PersonBuilder personBuilder;
    private ListMultimap<Person, String> errors = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);

    @BeforeEach
    public void before() {
        Log.init();
        Log.enableFailQuick(false);
        FlensburgMock.init();
        this.personBuilder = new PersonBuilder(1001L);
        errors.clear();
        PersonErrorCollector.init();
        PersonErrorCollector.addErrorObserver(new PersonErrorObserver() {
            @Override
            public void notifyNicknameInvalid(Person person, String msg) {
                errors.put(person, msg);
            }
            @Override
            public void notifyAgeInvalid(Person person, String msg) {
                errors.put(person, msg);
            }

            @Override
            public void notifyOwnsInvalid(Person person, String msg) {
                errors.put(person, msg);
            }

            @Override
            public void notifyMainResidenceInvalid(Person person, String msg) {
                errors.put(person, msg);
            }
        });
    }

    @Test
    public void testBuildValidPerson(){
        int id = 1001;
        FlensburgMock.secretlyAdaptnextGemIds(id);

        Address mainResidence = FlensburgManager.addressBuilder().city("Flensburg").build().get();

        String name = "Hardy";
        int age = 18;
        personBuilder
            .age(age)
            .nickname(name)
            .mainResidence(mainResidence)
            .build();
        assertTrue(Log.getFindings().isEmpty(), "Log not empty: " + Log.getFindings());

        Optional<Person> personOpt = FlensburgManager.personOpt(id);

        assertTrue(personOpt.isPresent());
        Person person = personOpt.get();

        assertTrue(errors.isEmpty());

        assertEquals(name, person.getNickname());
        assertEquals(age, person.getAge().intValue());
    }

    @Test
    public void testBuildInvalidPersonNoNickname(){
        int id = 1001;
        FlensburgMock.secretlyAdaptnextGemIds(id);

        Address mainResidence = FlensburgManager.addressBuilder().city("Flensburg").build().get();

        PersonBuilder builder = personBuilder
            .age(18)
            .mainResidence(mainResidence);
        Optional<Person> personOpt = builder.build();

        assertFalse(personOpt.isPresent());
        assertEquals(1, errors.size());

        List<String> msgs = errors.get(builder);
        assertEquals(1, msgs.size());

        String msg = msgs.get(0);
        assertTrue(msg.toLowerCase().contains("nickname"));
        assertTrue(msg.toLowerCase().contains("invalid"));

        assertFalse(FlensburgManager.personOpt(id).isPresent());
    }
}
