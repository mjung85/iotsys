# Obelix

## Getting started

Get ruby and rubygems. Install bundler (`gem install bundler`) and bundle up (run `bundle` in obelix directory).

## Building

Run

  bundle exec middleman build

Sync files over to iotsys-gateway (for now manually)

  rsync -av build/ ../iotsys-gateway/res/obelix

where they'll be served by the gateway.

## Development

Start middleman with:

  bundle exec middleman

Middleman will now serve obelix at http://localhost:4567/ and per default proxy all gateway requests to http://localhost:8080/