set :images_dir, 'images'

# Build-specific configuration
configure :build do
  activate :sprockets
  activate :minify_css
  activate :relative_assets
end

require 'rack/reverse_proxy'
use Rack::ReverseProxy do 
   reverse_proxy /^\/([^\.]+)$/, 'http://localhost:8080/$1'
end

