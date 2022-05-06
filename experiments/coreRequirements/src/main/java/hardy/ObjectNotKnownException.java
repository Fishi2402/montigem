package hardy;

// todo: Notwendig?
// todo: generell gilt wir machen keinen Wildwuchs an Exceptions.
// Exceptions sind zu vermeiden und daher gesondert zu begründen was deren Zweck sein soll.
// Exceptions gehören nicht in den normalen Programmablauf!

public class ObjectNotKnownException extends Exception {

    public ObjectNotKnownException(String message) {
        super(message);
    }
}
