import { ComponentFixture, TestBed } from "@angular/core/testing";

import { GemGraph } from "./GemGraph";

describe("NetworkComponent", () => {
  let component: GemGraph;
  let fixture: ComponentFixture<GemGraph>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GemGraph],
    });
    fixture = TestBed.createComponent(GemGraph);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
