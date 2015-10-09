# oBeliX #

oBeliX is a HTML5 client for the IoTSyS gateway, enabling direct manipulation of available oBIX objects, as well as drag and drop construction of control logic based on gateway's group communication feature.

## Overview ##

oBeliX is implemented on top of the [AngularJS](http://angularjs.org) framework with heavy use of the two-way databinding capabilities provided by the framework and follows the MVC pattern.

The resulting application consists of three files: an HTML document containing the AngularJS template, the javascript file containing application source concatened with the framework sources, and the CSS file. These static files reside in `iotsys-gateway/res/obelix` and are served by the gateway's builtin web server as the root resource. This means that simply opening http://gateway:port/ in the browser will start oBeliX.

Once loaded, the application proceeds to retrieve and parse objects from the oBIX lobby (available at relative URL `/obix`) and builds up the sidebar representing the device tree.

As the gateway currently offers no persistence mechanism, HTML5's localStorage is used to persist positions of objects in the canvas.

### Development ###

The static files described above are generated from the following source files (residing in `obelix/source`)

  * `index.haml`: a [HAML](http://haml.info/) template from which `index.html` is generated. Contains the initial DOM template is then further transformed by the AngularJS once the app starts executing.

  * `javascripts/app.js`: javascript source of the app itself. Note that header includes a specially formatted comment containing include directives for [Sprockets](https://github.com/sstephenson/sprockets). Out of `app.js` and included javascripts, Sprockets builds a single javascript file, resulting in simpler deployment and faster loading of the application.

  * `stylesheets/app.scss`: [SASS](http://sass-lang.com) template from which `app.css` is generated. We use SASS as it offers variables and mixins, and also enables easier generation of cross-browser compatible CSS. For the later, a special SASS library, [Compass](http://compass-style.org) is used.

  * `vendor/javscripts/*`: External libraries (AngularJS, jQuery etc.) used by the `app.js`

Building the application is handled by [Middleman](http://middlemanapp.com), which comes with a builtin development server. The development server monitors the above source files for changes, and after each change rebuilds the project.

#### Getting started with Middleman ####

To install middleman:

  * Get ruby 1.9 or higher with rubygems (package system for ruby) first.
  * Install the bundler ruby gem (`gem install bundler`), then from the obelix root directory run

(For Windows consider the rails installer: http://railsinstaller.org/en)

`bundle`

This will now install middleman and its dependencies.

Now, to start middleman server, run (from `obelix/`):

`bundle exec middleman`

Middleman will now serve oBeliX at http://localhost:4567/ and per default proxy all requests for anything that isn't the above three files to http://localhost:8080/. To override the gateway URL, use GATEWAY\_HOST and GATEWAY\_PORT environment variables, i.e.

`GATEWAY_HOST=192.168.0.10 GATEWAY_PORT=1234 bundle exec middleman`

When authentication is enabled (`/iotsys-gateway/config/iotsys.properties`), login at http://localhost:8080 first, before connecting to http://localhost:4567.

#### Building ####

Once you've finished developing a new feature, or fixing a bug, you need to rebuild the static files and copy them over to `iotsys-gateway`. To build, run:

`bundle exec middleman build`

This will create static files in `./build`, which can be copied with following command:

`rsync -av build/ ../iotsys-gateway/res/obelix`