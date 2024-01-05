import { screen } from "@testing-library/angular";
import { MessageService } from "primeng/api";
import { Component } from "@angular/core";
import { CopyDirective } from "./copy.directive";
import { ComponentFixture, TestBed } from "@angular/core/testing";

@Component({
  template: ` <button [copy]="dataToCopy">Click me</button>`,
})
class TestComponent {
  dataToCopy = { key: "value" };
}

describe("CopyDirective", () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let messageService: MessageService;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [CopyDirective],
      providers: [MessageService],
    });

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    messageService = TestBed.inject(MessageService);
    fixture.detectChanges();
  });

  it("should create an instance", () => {
    const directive: HTMLElement = screen.getByText("Click me");
    expect(directive).toBeDefined();
  });

  it("should copy data to clipboard", async () => {
    // Mocking clipboard API
    Object.assign(navigator, {
      clipboard: {
        writeText: jest.fn(),
      },
    });

    const addSpy = jest.spyOn(messageService, "add");

    const directive: HTMLElement = screen.getByText("Click me");
    directive.click();
    fixture.detectChanges();

    // Wait until subscribes finish
    fixture.whenStable().then(() => {
      expect(navigator.clipboard.writeText).toHaveBeenCalledWith(JSON.stringify({ key: "value" }, null, 2));
      expect(addSpy).toHaveBeenCalledWith({ severity: "success", summary: "Success", detail: "Copied!" });
    });
  });
});
