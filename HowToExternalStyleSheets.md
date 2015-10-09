# Introduction #

A short description on how to add external style sheets.


# Details #

The external style sheets have to be located in the directory

**<repository root>/obelix/vendor/assets/stylesheets**.

The external style sheet, e. g. 'not.mine.css', then has to be included like this

//= require 'not.mine'

or

//= require 'not.mine.css'

in the file

**<repository root>/obelix/source/stylesheets/app.scss**.


After building the web application with middleman (see
**<repository root>/obelix/README.md**)
<br />
only one CSS file, app.css, is generated, which<br />
contains the content of all project CSS files.