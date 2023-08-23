/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
/**
 * signifies whether a lazily loadable object has been loaded or not
 * @enum
 * @property {LoadingState} NOT_YET_LOADED
 * there has been no attempt to load the object yet
 * @property {LoadingState} LOADED
 * the object has been loaded
 * @property {LoadingState} EMPTY
 * if the object only exists optionally ({@link java.util.Optional})
 * and the object does not exist,
 * then empty signifies the non-existence of the object
 * @property {LoadingState} NOT_AUTHORIZED
 * if one recieves this, one is not authorized
 * to access the object in question,
 * whether it exists or not
 * @property {LoadingState} FAILED_TO_LOAD
 * there has been an error loading the object
 * e.g. the server could not be reached
 * @class
 */
export enum LoadingState {
    
    /**
     * there has been no attempt to load the object yet
     */
    NOT_YET_LOADED, 
    /**
     * the object has been loaded
     */
    LOADED, 
    /**
     * if the object only exists optionally ({@link java.util.Optional})
     * and the object does not exist,
     * then empty signifies the non-existence of the object
     */
    EMPTY, 
    /**
     * if one recieves this, one is not authorized
     * to access the object in question,
     * whether it exists or not
     */
    NOT_AUTHORIZED, 
    /**
     * there has been an error loading the object
     * e.g. the server could not be reached
     */
    FAILED_TO_LOAD
}


