/* (c) https://github.com/MontiCore/monticore */

package hardy;

/*
 * Why the hell is this copy of a standard class here?
 * TODO: remove this class and replace it by a library element?
 * 
 * Part of the runtime for dex.
 * (used by the generator, but independent of a concrete Dex-Model)
 */

/**
 * A class can implement the <code>DexObserver</code> interface when it
 * wants to be informed of changes in observable objects.
 *
 * @see     java.util.Observable
 */
public interface DexObserver {
    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>DexObservable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code>
     *                 method.
     */
    void update(DexObservable o, Object arg);
}
