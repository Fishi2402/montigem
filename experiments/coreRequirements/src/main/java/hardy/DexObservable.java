/* (c) https://github.com/MontiCore/monticore */
/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Why the hell is this copy of a standard class here?
 * TODO: remove this class and replace it by a library element?
 * 
 * Part of the runtime for dex.
 * (used by the generator, but independent of a concrete Dex-Model)
 */

package hardy;

/**
 * This interface represents an observable object, or "data"
 * in the model-view paradigm. It can be implemented to represent an 
 * object that the application wants to have observed. 
 * <p>
 * An observable object can have one or more observers. An observer 
 * may be any object that implements interface <tt>DexObserver</tt>. After an 
 * observable instance changes, an application calling the 
 * <code>DexObservable</code>'s <code>notifyObservers</code> method  
 * causes all of its observers to be notified of the change by a call 
 * to their <code>update</code> method. 
 * <p>
 * The order in which notifications will be delivered is unspecified.   
 * The default implementation provided in the DexObservable class will
 * notify Observers in the order in which they registered interest, but 
 * subclasses may change this order, use no guaranteed order, deliver 
 * notifications on separate threads, or may guarantee that their
 * subclass follows this order, as they choose.
 * <p>
 * Note that this notification mechanism is has nothing to do with threads 
 * and is completely separate from the <tt>wait</tt> and <tt>notify</tt> 
 * mechanism of class <tt>Object</tt>.
 * <p>
 * When an observable object is newly created, its set of observers is 
 * empty. Two observers are considered the same if and only if the 
 * <tt>equals</tt> method returns true for them.
 *
 * @see     java.util.Observable#notifyObservers()
 * @see     java.util.Observable#notifyObservers(Object)
 * @see     java.util.Observer
 * @see     java.util.Observer#update(java.util.Observable, Object)
 */
@Deprecated
public interface DexObservable {

    /**
     * Adds an observer to the set of observers for this object, provided 
     * that it is not the same as some observer already in the set. 
     * The order in which notifications will be delivered to multiple 
     * observers is not specified. See the class comment.
     *
     * @param   o   an observer to be added.
     * @throws NullPointerException   if the parameter o is null.
     */
    void addObserver(DexObserver o);

    /**
     * Deletes an observer from the set of observers of this object. 
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   o   the observer to be deleted.
     */
    void deleteObserver(DexObserver o);

    /**
     * If this object has changed, as indicated by the 
     * <code>hasChanged</code> method, then notify all of its observers 
     * and then call the <code>clearChanged</code> method to 
     * indicate that this object has no longer changed. 
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other 
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, Object)
     */
    void notifyObservers();

    /**
     * If this object has changed, as indicated by the 
     * <code>hasChanged</code> method, then notify all of its observers 
     * and then call the <code>clearChanged</code> method to indicate 
     * that this object has no longer changed. 
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   arg   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, Object)
     */
    void notifyObservers(Object arg);

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    void deleteObservers();

}
