Change Log
==========

Version 2.0.10 *(2026-06-29)*
-----------------------------

* Make release build reproducable.

Version 2.0.9 *(2026-06-29)*
-----------------------------

* Implement targets support in assignment expr `a, b, c = (.., .., ..)`.
* Implement parsing star_expressions in assignments `t = a, b, c`.
* Fix throwing `only single target (not tuple) can be annotated`.
* Implement `tuple_iterator` and integrate it in `for` statement.
* Improve unpacking error message to throw ValueError similar to Python.
* Remove screen orientation.
* Disable tuple assignment on GPU as NYI.

Version 2.0.8 *(2026-06-28)*
-----------------------------

* Remove singing by debug config.
* Update HILT library to 2.60.
* Support x86 ABI build target.
* Setup version code function for different ABIs.

Version 2.0.7 *(2026-06-27)*
-----------------------------

* Change the version code calculation for each ABI.
* Update Toolbar options depending on current route.
* Not crash if no browser is provided when open url.

Version 2.0.6 *(2026-06-26)*
-----------------------------

* Support ABI builds.
* Update fastlane config for F-Droid.

Version 2.0.5 *(2026-06-25)*
-----------------------------

* Add fastlane config/screenshots.
* Update Proguard rules for release.

Version 2.0.4 *(2026-06-22)*
-----------------------------

* Implement `del` statement with identifiers.
* Implement `try ... except ... else ... finally` statement.
* Support active exception at the runtime.

Version 2.0.3 *(2026-06-16)*
-----------------------------

* Implement Set Comprehension on the interpreter target.
* Implement Dict Comprehension on the interpreter target.
* Add support for function return type annotations.
* Add more arity checks to bin function.
* Implement builtin `callable` function.
* Implement invert operator.
* Support for ... else stmt.
* Move files screen to toolbar.
* Show badge on active tab.

Version 2.0.2 *(2026-06-11)*
-----------------------------

* Disable Minify for now.

Version 2.0.1 *(2026-06-10)*
-----------------------------

* Replace returning str error with exception to be able to catch them at user level.
* Implement ArithmeticError and ZeroDivisionError.
* Implement reversed and improve iter.

Version 2.0.0 *(2026-06-07)*
-----------------------------

* Implement Subset of python from the Python 3.15 reference.
* Support GPU Programming.
* Support up to 8 themes.
* Add shipped samples and remove saving files to be able to update GPU syntax easily.
* Update SDK to 37.

Version 1.6.2 *(2024-07-17)*
-----------------------------

* Update SDK to 34.
* Update TreeView to 1.2.0
* Update CodeView to 1.3.9.

Version 1.6.1 *(2023-09-27)*
-----------------------------

* Remove Github Sponsor action

Version 1.6.0 *(2023-08-21)*
-----------------------------

* Add support for `penUp` and `penDown` functions #3

Version 1.5.3 *(2023-08-18)*
-----------------------------

* Add support for comments #2
* Add the new logo

Version 1.5.1 *(2023-02-27)*
-----------------------------

* Fix editor height with keyboard

Version 1.4.3 *(2022-10-25)*
-----------------------------

* Fix Code formatter issues, and improve format for binary expressions

Version 1.4.2 *(2022-10-21)*
-----------------------------

* Introduce Code formatter for Lilo Programming language
* Make diagnostic instance reusable

Version 1.4.1 *(2022-10-11)*
-----------------------------

* Add support for execution seek bar
* Add support for pause and consume menu icons
* hide consume menu icon when execution is finished
* Improve parse logical, factor and term expression
* Add Vibrate circle to the preload examples


Version 1.3.2 *(2022-09-23)*
-----------------------------

* Add preloaded lilo packages for new users
* Add support for delete on swapping

Version 1.3.1 *(2022-09-21)*
-----------------------------

* Fix Number of parameter when parsing function declaration
* Fix Control flow execution Scope
* Fix Recursion functions
* Add support for `show` keyword
* Add support for `hide` keyword

Version 1.3.0 *(2022-09-15)*
-----------------------------

* Change turtle pointer design to be a small turtle on the screen
* Add support for stop and re execute actions
* Add support for `speed` keyword
* Add support for `sleep` keyword

Version 1.2.1 *(v-08-30)*
-----------------------------

* Fix crash when user type wrong script many times
* Add support for `background` instruction

Version 1.2.0 *(2022-07-11)*
-----------------------------

* Add support for built in functions
* Consume that call expression must end with `TOKEN_CLOSE_PAREN`

Version 1.1.0 *(2022-07-09)*
-----------------------------

* Increase the auto complete item width.
* Add support for array, with index operator.
* Add support for else if and else branches.
* Add support for special assignments *=, -=, *=, /=, %=.
* Add support for reminder operator %

Version 1.0.0 *(2022-07-02)*
-----------------------------

* Init release
