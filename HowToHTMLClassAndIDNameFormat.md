# Introduction #
A short description on how to format HTML class and ID names.

# Details #

If **possible**, apply the following guidelines for self-defined names
  * this-is-a-multi-word-class-name
  * this-is-a-multi-word-id-name

Allowed characters for ID and class names are
  * lower-case characters a-z
  * digits 0-9
  * and -

In app.js a **filter** named "_htmlNameNormalizer_" is defined which
formats an input string according to the naming convention. For its
usage see the file _/obelix/source/index.haml_ and _/obelix/source/javascripts/app.js_.


The filter is also available as interactive demo: [htmlNameNormalizer demo@codepen.io](http://codepen.io/anon/pen/quKIw).