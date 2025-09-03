import { Injectable } from "@angular/core";

/** Utility service that lets you capture and restore the focus on an element. */
@Injectable({
  providedIn: "root",
})
export class FocusService {
  /** Store the last focused element */
  private lastFocusedElement: HTMLElement | null = null;

  /** Capture the current focus. */
  public captureCurrentFocus(): void {
    this.lastFocusedElement = document.activeElement as HTMLElement;
  }

  /** Restore the focus to the last focused element. */
  public restoreFocus(): void {
    if (this.lastFocusedElement) {
      this.lastFocusedElement.focus();
      this.lastFocusedElement = null;
    }
  }
}
