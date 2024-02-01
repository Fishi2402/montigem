import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemCheckBoxInput } from './gem-check-box-input.component';

describe('GemCheckBoxInput', () => {
  let component: GemCheckBoxInput;
  let fixture: ComponentFixture<GemCheckBoxInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GemCheckBoxInput ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GemCheckBoxInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
