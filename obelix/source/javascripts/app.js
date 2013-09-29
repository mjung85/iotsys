//= require 'jquery'
//= require 'jquery-ui'
//= require 'angular'
//= require 'html5slider'
//= require 'jquery.jsPlumb-1.5.2'
//= require 'sugar'
//= require 'URI'
//= require_self

var app = angular.module('Obelix', []);

app.constant('WATCH_POLL_INTERVAL', 1000);

app.service('Lobby', ['$http', 'Device', 'Directory', function($http, Device, Directory) {
  return {
    getDeviceTree: function(callback) {
      var root = new Directory('');
      root.expanded = true;
      $http.get('/obix').success(function(response) {
        response['nodes'].each(function(node) {
          var href = node['href'];
          if (!href.startsWith('/')) href = '/' + href;
          var href_components = href.split('/').compact(true);
          var device_name = href_components.pop();
          var device_directory = root.make(href_components);

          device_directory.devices.push(new Device(href, device_name));
        });
        callback(root);
      });
    }
  }
}]);

app.service('Storage', function() {
  return {
    get: function(key) {
      var result = localStorage.getItem(key);
      if (!result) {
        return null;
      } else if (result.charAt(0) === "{" || result.charAt(0) === "[") {
        return angular.fromJson(result);
      } else {
        return result;
      }
    },
    set: function(key, value) {
      if (angular.isObject(value) || angular.isArray(value)) {
        value = angular.toJson(value);
      }
      localStorage.setItem(key, value);
    },
    remove: function(key) {
      localStorage.removeItem(key);
    }
  }
});

app.factory('Directory', function() {
  var Directory = function(name) {
    this.name = name;
    this.subdirectories = [];
    this.devices = [];
    this.expanded = false;
    return this;
  };

  Directory.prototype = {
    make: function(components) {
      if (components.isEmpty()) return this;

      var name = components.shift();
      name = name.replace('+',' ');
      
      var subdirectory = this.subdirectories.find({name:name});
      if (!subdirectory) {
        subdirectory = new Directory(name);
        this.subdirectories.push(subdirectory);
      }

      if (components.isEmpty()) {
        return subdirectory;
      } else {
        return subdirectory.make(components);
      }
    },

    toggle: function() {
      this.expanded = !this.expanded;
      // if (this.expanded) this.expand();
    },

    //expand: function() {
    //   this.expanded = true;
    //   if (this.subdirectories.count() == 1) {
    //     // only one subdirectory, expand it
    //     this.subdirectories[0].expand();
    //   }
    //},

    // Recursively return all devices under this directory and its subdirectories
    globDevices: function() {
      var result = [];
      result = result.concat(this.devices);
      this.subdirectories.each(function(s) { result = result.concat(s.devices); });
      return result;
    }
  };

  return Directory;
});

app.factory('Watch', ['$http', '$timeout', '$q', 'Storage', 'WATCH_POLL_INTERVAL', function($http, $timeout, $q, Storage, WATCH_POLL_INTERVAL) {
  var Watch = function(href) {
    this.href = href;
  };

  // A promise that will get resolved only when an old watch that we have in localStorage is dead and we creat a new one
  Watch.watchRecreatedDefer = $q.defer();

  Watch.getInstance = function(callback) {
    var watchHref = Storage.get('watch');
    if (!watchHref) {
      $http.post('/watchService/make').success(function(response) {
        var watchHref = response['href'];
        Storage.set('watch', watchHref);
        console.log("Made new watch ", watchHref);
        callback(new Watch(watchHref));
      });
    } else {
      var ok = false;
      $http.get(watchHref).success(function(response) {
        if (response['tag'] != 'err') ok = true;
      }).then(function() {
        if (ok) {
          callback(new Watch(watchHref));
        } else {
          console.log("Old watch "+watchHref+" seems gone. Recreating...");
          Storage.remove('watch');
          Watch.getInstance(function(newWatch) {
            Watch.watchRecreatedDefer.resolve(newWatch);
            callback(newWatch);
          });
        }
      });
    }
  };

  Watch.prototype = {
    hrefForOperation: function(opName) {
      return URI(this.href).segment(opName).toString(); // For now, operation href is same as name
    },
    add: function(href) {
      // var payload = {"is" : "obix:WatchIn", "tag" : "obj", "nodes" : [{"tag" : "list", "name": "hrefs", "nodes":[{"tag": "uri", "val": href}]}]};

      var payload = '<obj is="obix:WatchIn"><list name="hrefs"><uri val="'+href+'" /></list></obj>';

      $http({
        url: this.hrefForOperation('add'),
        method: 'POST',
        headers: {
          'Content-Type': 'application/xml',
          'Accept': 'application/json'
        },
        data: payload
      }).success(function(response) {
        if (response['is'] != "obix:WatchOut") {
          console.log("Error adding device "+href+" to watch",response);
        } else {
          console.log("Added device "+href+" to watch");
        }
      });
    },
    poll: function(callback) {
      $http.post(this.hrefForOperation('pollChanges')).success(function(response) {
        var devices = response['nodes'][0]['nodes'];
        if (devices) {
          devices.each(callback);
        }
      });
    },
    tick: function() {
      if (this.polling) {
        this.poll(this.polling);
        $timeout(this.tick.bind(this), WATCH_POLL_INTERVAL);
      }
    },
    startPolling: function(callback) {
      this.polling = callback;
      this.tick();
    }
  };

  return Watch;
}]);

app.factory('Property', ['$http', function($http) {
  var Property = function(node) {
    if (!['bool', 'int', 'real', 'str', 'enum'].find(node['tag'])) {
      this.valid = false;
      return;
    } else {
      this.valid = true;

      this.groupCommEnabled = false;
      this.range = false;
      this.href = node['href'];
      this.type = node['tag'];
      this.numeric = (this.type == 'int' || this.type == 'real');
      this.name = node['name'];
      this.value = node['val'];
      this.readonly = !node['writable'];

      if (this.type == 'real') {
        this.value = Math.round(this.value * 100) / 100.0;
      }

      if (this.numeric && node['max'] && !this.readonly) {
        this.range = {min: node['min'], max: node['max'], step: Math.abs(this.rangeMax - this.rangeMin)/100.0};
      }

      if (this.type == 'enum') {
        this.range = Property.Enum.range(node['range']);
      }

      // Display class
      if (this.numeric) {
        this.klass = 'numeric';
      } else {
        this.klass = this.type;
      }
    }
  };

  Property.Enum = {
    ranges: {},
    range: function(href) {
      var result = this.ranges[href];
      if (!result) {
        result = [];
        this.ranges[href] = result;
        $http.get(href).success(function(response) {
          result.add(response['nodes'].map('name'));
        }.bind(this));
      }
      return result;
    }
  };

  Property.prototype = {
    serialize: function() {
      return {'tag': this.type, 'val': this.value };
    }
  };

  return Property;
}]);

app.factory('Device', ['$http', 'Storage', 'Property', function($http, Storage, Property) {
  var Device = function(href, name) {
    this.href = href;
    this.name = name;
    this.placement = Storage.get('device_'+this.href+'_placement');
    if (this.placement) {
      this.load();
    }
    return this;
  };

  Device.prototype = {
    place: function(position) {
      this.placement = {left: position.left, top: position.top};
      Storage.set('device_'+this.href+'_placement', this.placement);
      if (!this.loaded && !this.loading) this.load();
    },

    load: function() {
      this.loading = true;
      $http.get(this.href).success(function(response) {
        this.parse(response);
        this.loaded = true;
        this.loading = false;
      }.bind(this));
    },

    parse: function(response) {
      var propertiesWithGroupCommEnabled = [];
      this.properties = response['nodes'].map(function(node) {
        if (node['tag'] == 'ref') {
          var names = node['name'].split(' ');
          var gcIndex = names.indexOf('groupComm');
          if (gcIndex != -1) {
            names.splice(gcIndex,1);
            propertiesWithGroupCommEnabled.push(names[0]);
          }
          return false;
        } else {
          return new Property(node);
        }
      }).filter({valid:true});

      // Set .groupcommEnabled=true for properties specified in the <ref>s
      propertiesWithGroupCommEnabled.each(function(name) {
        var p = this.properties.find({name:name});
        if (p) { p.groupCommEnabled = true; }
      }.bind(this));
    },

    update: function(property) {
      var absolutePropertyHref = URI(property.href).absoluteTo(this.href).toString();
      $http.put(absolutePropertyHref, property.serialize()).success(function(response) {
        if(response['tag'] && response['tag'] == 'err') {
          console.log("Error updating " + absolutePropertyHref, response);
        }
        // this.parse(response);
      }.bind(this));
    },
  };
  
  return Device;
}]);

app.controller('MainCtrl', ['$scope','$q','Lobby','Watch', function($scope, $q, Lobby, Watch) {
  $scope.directory = null;
  $scope.allDevices = [];
  $scope.watch = null;

  var devicesInstantiatedDefer = $q.defer();

  $q.all([
    Watch.watchRecreatedDefer.promise, 
    devicesInstantiatedDefer.promise
  ]).then(function(values) {
    // Promises resolved. We now have both watch and devices and can re-add them to the new watch
    console.log("Re-adding placed devices to new watch");
    var watch = values[0];
    var placedDevices = values[1];
    placedDevices.each(function(device) { watch.add(device.href); });
  });

  Lobby.getDeviceTree(function(root) {
    $scope.directory = root;
    $scope.allDevices = root.globDevices();

    var placedDevices = $scope.allDevices.filter(function(device) {
      return !!device.placement;
    });
    devicesInstantiatedDefer.resolve(placedDevices);
  });

  Watch.getInstance(function(watch) {
    $scope.watch = watch;
    watch.startPolling(function(deviceJson) {
      var device = $scope.allDevices.find({href:deviceJson['href']});
      if (device) {
        device.parse(deviceJson);
      }
    });
  });

  $scope.sidebarExpanded = false;
  
  $scope.placeDevice = function(device, position) {
    if (!device.placement) {
      // Initial placement, add to watch
      $scope.watch.add(device.href);
    }
    $scope.sidebarExpanded = false;
    device.place(position);
  };
}]);

// Simple angularJS directives for use of jquery-ui draggable
//
// On source, define draggable="model"
// On drop target define droppable="callback(draggable)"
// callback(model, position) will be called on drop
app.directive('draggable', function() {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var device = scope.$eval(attrs['draggable']);

      var options = {
        appendTo: 'body',
        helper: 'clone',
        start: function() {
          $(this).addClass('disabled');
        },
        stop: function() {
          $(this).removeClass('disabled');
        }
      };

      if (attrs['draggableDistance']) options.distance = attrs['draggableDistance'];
      if (attrs['draggableHelper']) options.helper = attrs['draggableHelper'];

      if (attrs['draggableViaJsplumb']) {
        jsPlumb.draggable(el, options);  
      } else {
        el.draggable(options);        
      }
    }
  };
});

// app.directive('notDroppable', function() {
//   return {
//     restrict: 'A',
//     link: function(scope, el, attrs) {
//       el.droppable({
//         greedy: true,
//         drop: function(event,ui) {
//           console.log('swallow');
//         }
//       });
//     }
//   }
// });

app.directive('droppable', ['$parse',function($parse) {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var callback = $parse(attrs['droppable'])(scope);
      el.droppable({
        drop: function(event, ui) {
          var currentPos = ui.helper.position();
          var draggable = angular.element(ui.draggable);
          var model = draggable.scope().$eval(draggable.attr('draggable'));
          callback(model, ui.helper.position());
          scope.$apply();
          jsPlumb.repaintEverything();
        }
      });
    }
  }
}]);

app.directive('ngModelOnblur', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, elm, attr, ngModelCtrl) {
            if (attr.type === 'radio' || attr.type === 'checkbox') return;
            elm.unbind('input').unbind('keydown').unbind('change');
            elm.bind('blur', function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(elm.val());
                });
            });
        }
    };
});

app.directive('jsplumbContainer', function() {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      jsPlumb.ready(function() {
        jsPlumb.Defaults.Container = el;
      });
    }
  }
});

app.directive('jsplumbEndpointIf', ['$timeout', function($timeout) {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var value = scope.$eval(attrs['jsplumbEndpointIf']);
      if (!value) return;

      $timeout(function() {
        jsPlumb.addEndpoint(el, {
          isSource: true, 
          isTarget: true, 
          connector:[ "Bezier", { stub: 30, curviness:50 }], 
          endpoint: ["Rectangle", { width: 8, height: 8}],
          anchors: [[1, 0.5, 1, 0, 8,0], [0, 0.5, -1, 0, -8, 0]],
          paintStyle:{ fillStyle:"#666"}, 
          connectorStyle: { lineWidth: 4, strokeStyle: "#5b9ada"},
          connectorClass: 'conn'
        });
      },0);  
    }
  }
}]);