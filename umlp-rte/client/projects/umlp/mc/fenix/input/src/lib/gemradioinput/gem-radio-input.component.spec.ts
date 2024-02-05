import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemRadioInput } from './gem-radio-input.component';

describe('GemRadioInput', () => {
  let component: GemRadioInput;
  let fixture: ComponentFixture<GemRadioInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GemRadioInput ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GemRadioInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
