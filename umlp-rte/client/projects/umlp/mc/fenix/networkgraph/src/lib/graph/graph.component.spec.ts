import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NetworkGraphComponent } from "./graph.component";

describe("NetworkComponent", () => {
  let component: NetworkGraphComponent;
  let fixture: ComponentFixture<NetworkGraphComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NetworkGraphComponent],
    });
    fixture = TestBed.createComponent(NetworkGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
