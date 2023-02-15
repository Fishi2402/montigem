// HWC-P

// when a HWC-P module file is added, it is only imported and used in GEN-P module
// The GEN-P module is used as usual in other modules
// We may consider naming HWC-P file differently (e.g., with -bot suffix), since unlike other
// TOP-mechanisms, the GEN-P file is used instead of HWC-P

// maybe will be generated later (derived from input parameter with reference values)
import { FormsModule } from "@angular/forms";

// Generator may assume that the name is always module
export const module: {
  declarations?: any[], imports?: any[], exports?: any[], providers?: any[]
} = {
  imports: [FormsModule]
};
