set :images_dir, 'images'

# Build-specific configuration
configure :build do
  activate :sprockets
  activate :minify_css
  activate :relative_assets
end

require 'rack/reverse_proxy'
use Rack::ReverseProxy do
  host = ENV['GATEWAY_HOST'] || 'localhost'
  port = ENV['GATEWAY_PORT'] || '8080'
  reverse_proxy /^\/([^\.]+)$/, "http://#{host}:#{port}/$1"
end