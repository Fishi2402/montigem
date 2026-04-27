package test_iterate;

import jsweet.lang.Replace;

public class NonNestedIterates extends NonNestedIteratesTOP {
    public int initIter2Counter = 0;

    @Override
    protected void initIter2Iterable(){
      super.initIter2Iterable();
      initIter2Counter++;
      tsHook(initIter2Counter);
    }

    @Replace("""
        (<any>window).testing_initIter2Counter = curValue;
        """)
    protected void tsHook(int curValue){
      // replaced in transpiled code
    }
}
