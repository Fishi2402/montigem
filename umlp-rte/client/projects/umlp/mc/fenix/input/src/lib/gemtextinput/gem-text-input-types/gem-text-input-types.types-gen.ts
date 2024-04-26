
export interface GemTextInputValidity {
  status?: string;
  message?: string;
}

export class Option {
  value: string;
  display: string;

  constructor(value: string, display?: string) {
    this.value = value;
    if (!display) {
      this.display = value;
    }
  }
}
