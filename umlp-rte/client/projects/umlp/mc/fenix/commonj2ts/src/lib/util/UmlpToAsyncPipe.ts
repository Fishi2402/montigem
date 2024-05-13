import {AsyncPipe} from "@angular/common";
import {OnDestroy, Pipe, PipeTransform} from "@angular/core";
import {Observable, Subscribable} from "rxjs";

@Pipe({
  name: "umlpToAsync",
  standalone: true,
})
export class UmlpToAsyncPipe implements PipeTransform {

  transform<T>(obj: Promise<T> | T | null | undefined): Promise<T> {
    if (!!obj) {
      return Promise.resolve(obj);
    }
    return null;
  }

}
