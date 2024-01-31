import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GemTextareaInput } from './gem-textarea-input.component';

describe('GemTextareaInput', () => {
  let component: GemTextareaInput;
  let fixture: ComponentFixture<GemTextareaInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GemTextareaInput ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GemTextareaInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
