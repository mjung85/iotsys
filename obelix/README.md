# Obelix

Get ruby and rubygems. Install bundler

  gem install bundler

Bundle:
  
  cd obelix && bundle

Build:

  middleman build

Sync files over to iotsys-gateway (for now manually)

  rsync -av build/ ../iotsys-gateway/res/obelix
