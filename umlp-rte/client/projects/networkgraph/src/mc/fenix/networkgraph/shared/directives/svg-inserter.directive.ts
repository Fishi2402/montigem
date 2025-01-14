import { Directive, ElementRef, Input, OnChanges } from "@angular/core";
import DOMPurify from "dompurify";

/**
 * The SvgInserterDirective is used to fetch, sanitize and insert an external SVG into the DOM. The SVG URL is provided
 * via the "svgInserter" attribute. An alternative text for accessibility can be provided via the "svgAlt" attribute.
 *
 * @remarks
 *   - The advantage of using this directive over embedding it into a simple `<img>` tag is that the SVG can be styled.
 *
 * @example
 *   <div svgInserter="https://example.com/image.svg" svgAlt="A description of the image"></div>
 */
@Directive({
  selector: "[svgInserter]",
  standalone: true,
})
export class SvgInserterDirective implements OnChanges {
  /** The URL of the SVG to fetch and insert. */
  @Input("svgInserter") svgUrl: string | undefined;

  /** The alternative text for the SVG, used for accessibility. */
  @Input() svgAlt: string = "";

  /** The current URL of the SVG, used to prevent re-fetching the SVG if the URL hasn't changed. */
  private currentUrl: string | undefined;

  /**
   * A reference to the host element.
   *
   * @param {ElementRef} elementReference - Element Reference
   */
  constructor(private elementReference: ElementRef) {}

  /**
   * When the inputs change, verify that the svg url has really changed. If so, fetch the new SVG, sanitize it, and
   * insert it into the DOM.
   */
  ngOnChanges(): void {
    // Only fetch the data when a URL is provided that has not yet been loaded
    if (this.svgUrl && this.svgUrl !== this.currentUrl) {
      // Update the current URL
      this.currentUrl = this.svgUrl;

      // Load the actual image
      fetch(this.svgUrl)
        .then((response: Response): Promise<string> => {
          if (!response.ok) throw new Error("Network response was not ok " + response.statusText);
          return response.text();
        })
        .then((data: string): void => {
          // Sanitize the SVG before injecting it into the DOM using DOMPurify
          this.elementReference.nativeElement.innerHTML = DOMPurify.sanitize(data);

          // Add accessibility attributes
          this.elementReference.nativeElement.setAttribute("role", "img");
        })
        .catch((error): void => {
          console.error("There was a problem with the fetch operation:", error);
        });
    }

    // Update the Aria-Label every time the alt input changes
    if (this.svgUrl && this.svgAlt !== "") {
      this.elementReference.nativeElement.setAttribute("aria-label", this.svgAlt);
    }
  }
}
