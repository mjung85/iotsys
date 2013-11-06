# Obelix

## Getting started

Get ruby and rubygems. Install bundler (`gem install bundler`) and run `bundle` from the obelix directory.

## Building assets

Run

  bundle exec middleman build

This will create all obelix static files in ./build

Sync files over to iotsys-gateway (for now manually)

  rsync -av build/ ../iotsys-gateway/res/obelix

where they'll be served by the gateway.

## Development

Start middleman with:

  bundle exec middleman

Middleman will now serve obelix at http://localhost:4567/ and per default proxy all requests for obix resources to http://localhost:8080/. To override the gateway URL, use GATEWAY_HOST and GATEWAY_PORT environment variables, i.e.

  GATEWAY_HOST=192.168.0.10 GATEWAY_PORT=1234 bundle exec middleman

