This experiments demonstrates the capabilities of a python client

* generating a python client using the CDDecGen
  * using a [shared library](runtime-package)
  * [pydantic](https://docs.pydantic.dev/) as a type system
* testing the generated python using [GraalPy](https://www.graalvm.org/python/) and jUnit
  * **Note:** GraalPy is _really_ flaky on windows and requires Java17,
    which is why this project is not run by default
