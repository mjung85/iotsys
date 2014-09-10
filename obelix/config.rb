require 'closure-compiler'

after_configuration do
    sprockets.append_path "assets/vendor/stylesheets" 
end

set :images_dir, 'images'
set :debug_assets, true

# Build-specific configuration
configure :build do
  activate :sprockets
  activate :minify_css
  
  activate :minify_javascript
  set :js_compressor, ::Closure::Compiler.new

  activate :relative_assets
end

require 'rack/reverse_proxy'
use Rack::ReverseProxy do
  host = ENV['GATEWAY_HOST'] || 'localhost'
  port = ENV['GATEWAY_PORT'] || '8080'
  reverse_proxy /^\/([^\.]+)$/, "http://#{host}:#{port}/$1"
end
