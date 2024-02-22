import { Directive, ElementRef, Input, NgZone, OnDestroy, OnInit } from "@angular/core";
import { fromEvent, Subscription } from "rxjs";
import { MessageService } from "primeng/api";
import { Message } from "primeng/api/message";

/**
 * CopyDirective - Directive to handle copying text or objects to the clipboard.
 *
 * Inspired by https://netbasal.com/create-reusable-copy-to-clipboard-directive-in-angular-fc1139b9e755
 */
@Directive({
  selector: "[copy]",
  standalone: true,
})
export class CopyDirective implements OnInit, OnDestroy {
  /** The content to be copied to the clipboard. */
  @Input() copy: unknown;

  /** Message that should be shown when the content has successfully been copied */
  @Input() successMessage?: Message = {};

  /** Whether to show a success message or not */
  @Input() showSuccessMessage?: boolean = true;

  /** Default Success Message */
  private readonly DEFAULT_SUCCESS_MESSAGE: Message = {
    severity: "success",
    summary: "Success",
    detail: "Content successfully copied!",
  };

  /** Subscription for click event on the host element. */
  private clickSubscription: Subscription | null = null;

  /**
   * @param {ElementRef<HTMLElement>} host - Reference to the host element.
   * @param {NgZone} zone - Angular's execution context.
   * @param {MessageService} messageService - PrimeNG's message service for notifications.
   */
  constructor(
    private host: ElementRef<HTMLElement>,
    private zone: NgZone,
    private messageService: MessageService
  ) {}

  /** Initialize the directive. Subscribe to the click event of the host element. */
  ngOnInit(): void {
    this.zone.runOutsideAngular((): void => {
      this.clickSubscription = fromEvent(this.host.nativeElement, "click").subscribe((): void => {
        const contentToCopy: string = typeof this.copy === "string" ? this.copy : JSON.stringify(this.copy, null, 2);
        if (this.showSuccessMessage) {
          navigator.clipboard.writeText(contentToCopy).then((): void => {
            this.zone.run((): void => {
              this.messageService.add({ ...this.DEFAULT_SUCCESS_MESSAGE, ...this.successMessage });
            });
          });
        }
      });
    });
  }

  /** Cleanup. Unsubscribe from the click event to prevent memory leaks. */
  ngOnDestroy(): void {
    // For the sake of independence this deliberately does not use the @AutoUnsubscribe
    this.clickSubscription?.unsubscribe();
  }
}
