/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
export class Result<T> {
    /*private*/ value: T;

    successful: boolean;

    constructor(value: T, successful: boolean) {
        if (this.value === undefined) { this.value = null; }
        if (this.successful === undefined) { this.successful = false; }
        this.value = value;
        this.successful = successful;
    }

    public static ok<T>(value: T): Result<T> {
        return <any>(new Result(value, true));
    }

    public static error<T>(): Result<any> {
        return <any>(new Result(null, false));
    }

    public getValue(): T {
        return this.value;
    }

    public wasSuccessful(): boolean {
        return this.successful;
    }

    public wasUnsuccessful(): boolean {
        return !this.successful;
    }
}
Result["__class"] = "umlp.backendrte.common.Result";



