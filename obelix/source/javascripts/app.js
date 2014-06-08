//= require 'jquery'
//= require 'jquery-ui'
//= require 'angular'
//= require 'html5slider'
//= require 'jquery.jsPlumb-1.5.2'
//= require 'sugar'
//= require 'URI'
//= require 'backbone'
//= require_self

var app = angular.module('Obelix', []);

app.service('Lobby', ['$http', 'Device', 'Directory', function($http, Device, Directory) {
  return {
    getDeviceTree: function(callback) {
      var root = new Directory('');
      root.expanded = true;
      $http.get('/obix').success(function(response) {
    	  console.log(response);
        response['nodes'].each(function(node) {
          var href = node['href'];
          if (!href.startsWith('/')) href = '/' + href;
          var href_components = href.split('/').compact(true);
          var device_name = href_components.pop();
          if (node['displayName']) device_name = node['displayName'];
          var device_directory = root.make(href_components);
          device_directory.devices.push(new Device(href, device_name));
        });
        callback(root);
      });
    }
  };
}]);

app.factory('Storage', function() {
  var Storage = function(key) {
    this.key = key;
    return this;
  };

  Storage.get = function(key) {
      var result = localStorage.getItem(key);
      if (!result) {
        return null;
      } else if (result.charAt(0) === "{" || result.charAt(0) === "[") {
        return angular.fromJson(result);
      } else {
        return result;
      }
  };
  
  Storage.set = function(key, value) {
      if (angular.isObject(value) || angular.isArray(value)) {
        value = angular.toJson(value);
      }
      localStorage.setItem(key, value);
  };

  Storage.remove = function(key) {
    localStorage.removeItem(key);
  };
  
  Storage.prototype = {
    get: function() { return Storage.get(this.key); },
    set: function(value) { Storage.set(this.key, value); },
    remove: function() { Storage.remove(this.key); }
  };

  return Storage;
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
      this.subdirectories.each(function(s) { result = result.concat(s.globDevices()); });
      return result;
    }
  };

  return Directory;
});

app.factory('Watch', ['$http', '$timeout', '$q', 'Storage', function($http, $timeout, $q, Storage) {
  var Watch = function(href) {
    this.intervalStorage = new Storage('watch_interval');
    this.interval = this.intervalStorage.get();
    this.intervalMinimum = 1000;
    this.intervalMaximum = 10000;
    this.interval = (	null == this.interval || 
    					undefined == this.interval || 
    					isNaN(this.interval)) ? this.intervalMinimum : this.interval;
    this.updateInterval();
    this.href = href;
  };

  // A promise that will get resolved only when an old watch that we have in localStorage is dead and we've created a new one
  Watch.watchRecreatedDefer = $q.defer();

  Watch.getInstance = function(callback) {
    var storage = new Storage('watch');
    var watchHref = storage.get();
    if (!watchHref) {
      $http.post('/watchService/make').success(function(response) {
        var watchHref = response['href'];
        storage.set(watchHref);
        console.log("Made new watch ", watchHref);
        callback(new Watch(watchHref));
      });
    } else {
      var ok = false;
      $http.get(watchHref).success(function(response) {
        if (response['tag'] != 'err') ok = true;
      }).then(function() {
        if (ok) {
          console.log("Reusing watch"+watchHref);
          callback(new Watch(watchHref));
        } else {
          console.log("Old watch "+watchHref+" seems gone. Recreating...");
          storage.remove();
          Watch.getInstance(function(newWatch) {
            Watch.watchRecreatedDefer.resolve(newWatch);
            callback(newWatch);
          });
        }
      });
    }
  };

  Watch.prototype = {
    updateInterval: function() {
      this.interval = isNaN(this.interval) ? this.intervalStorage.get() : parseInt(this.interval, 10);
      if (this.interval < this.intervalMinimum) this.interval = this.intervalMinimum;
      if (this.interval > this.intervalMaximum) this.interval = this.intervalMaximum;
      console.log("Watch interval is", this.interval);
      this.intervalStorage.set(this.interval);
    },
    hrefForOperation: function(opName) {
      return URI(this.href).segment(opName).toString(); // For now, operation href is same as name
    },
    add: function(href) {
      return this.op('add', href);
    },
    remove: function(href) {
      return this.op('remove', href);
    },
    op: function(op, href) {
      // var payload = {"is" : "obix:WatchIn", "tag" : "obj", "nodes" : [{"tag" : "list", "name": "hrefs", "nodes":[{"tag": "uri", "val": href}]}]};
      var payload = '<obj is="obix:WatchIn"><list name="hrefs"><uri val="'+href+'" /></list></obj>';

      $http({
        url: this.hrefForOperation(op),
        method: 'POST',
        headers: {
          'Content-Type': 'application/xml',
          'Accept': 'application/json'
        },
        data: payload
      }).success(function(response) {
        if (response['is'] != "obix:WatchOut" && response['is'] != "obix:Nil") {
          console.log("Error "+op+" device "+href+" to watch",response);
        } else {
          console.log(op+" device "+href+" to watch");
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
        if (! this.intervalStorage.get()) {
        	this.intervalStorage.set(this.interval = this.intervalMinimum);
        }
        $timeout(this.tick.bind(this), this.intervalStorage.get());
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

      this.jsPlumbEndpoints = [];
      this.connections = [];
      this.groupCommEnabled = false;

      this.range = false;
      this.href = node['href'];
      this.type = node['tag'];
      this.numeric = (this.type == 'int' || this.type == 'real');
      this.name = node['name'];
      this.displayName = node['displayName'] || this.name;
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
    write: function(property) {
      $http.put(this.href, {'tag': this.type, 'val': this.value }).success(function(response) {
        if(response['tag'] && response['tag'] == 'err') {
          console.log("Error updating " + this.href, response);
        }
      }.bind(this));
    },
    connect: function(connection, join) {
      var action = join ? 'joinGroup' : 'leaveGroup';
      var url = URI(this.href).segment('groupComm').segment(action).toString();
      $http.post(url, '<str val="'+connection.ipv6+'"/>', {headers: {
        'Content-Type': 'application/xml'
      }}).success(function() {
        if (join) {
          this.connections.push(connection);
        } else {
          this.connections.remove(connection);
        }
        console.log(action,this.href,connection.ipv6);
      }.bind(this));
    }
  };

  return Property;
}]);

app.factory('Connection', ['Storage', function(Storage) {
  // If ipv6 is provided, this is a restored connection
  var Connection = function(fromProperty, toProperty, ipv6) {
    this.jsPlumbConnection = null;
    this.fromProperty = fromProperty;
    this.toProperty = toProperty;

    if (ipv6) {
      console.log("Restored connection",ipv6,"with",fromProperty,toProperty);
      this.ipv6 = ipv6;
    } else {
      this.ipv6 = "FF15::"+Connection.incrementCounter();
    }
    this.fromProperty.connect(this, true);
    this.toProperty.connect(this, true);

    return this;
  };

  Connection.prototype.destroy = function() {
    this.fromProperty.connect(this, false);
    this.toProperty.connect(this, false);
    jsPlumb.detach(this.jsPlumbConnection);
  };

  Connection.counter = null;
  // Returns current counter value, then increases and saves it to storage
  Connection.incrementCounter = function() {
    var storage = new Storage('connections_counter');
    if (!Connection.counter) {
      Connection.counter = storage.get() || 1;
    }
    var result = Connection.counter++;
    storage.set(Connection.counter);
    return result;
  };

  // Small utility "module" to de/serialize connections to localstorage, as they aren't enumerable in the lobby
  Connection.Freezer = (function(){
    var storage = new Storage('connections');
    var list = storage.get() || [];

    return {
      add: function(connection) {
        list.push({from:connection.fromProperty.href, to: connection.toProperty.href, ipv6: connection.ipv6});
        storage.set(list);
      },
      remove: function(connection) {
        list.remove({ipv6: connection.ipv6});
        storage.set(list);
      },
      restore: function(propertyLookupCallback, connectionRestoredCallback) {
        list.each(function(c) {
          var fromProperty = propertyLookupCallback(c.from);
          var toProperty = propertyLookupCallback(c.to);
          connectionRestoredCallback(new Connection(fromProperty, toProperty, c.ipv6));
        });
      }
    };
  })();

  return Connection;
}]);

app.factory('Device', ['$http', '$q', 'Storage', 'Property', 'Watch', function($http, $q, Storage, Property, Watch) {
  var Device = function(href, name) {
    this.loadedDefer = $q.defer();
    this.href = href;
    this.placementStorage = new Storage('device_'+this.href+'_placement');
    this.placement = this.placementStorage.get();
    if (this.placement) {
      this.load();
    }

    this.nameStorage = new Storage('device_'+this.href+'_name');
    this.name = this.nameStorage.get() || name.replace(/&#x([0-9a-f]{1,4});/gi, function(match, numStr) {
          var num = parseInt(numStr, 16);
          return String.fromCharCode(num);
    });
    this.originalName = name;
    
    return this;
  };

  Device.prototype = {
    rename: function() {
      if (this.name.isBlank()) {
        this.nameStorage.remove();
        this.name = this.originalName;
      } else {
        this.nameStorage.set(this.name);
      }
    },

    place: function(position) {
      this.placement = {left: position.left, top: position.top};
      this.placementStorage.set(this.placement);
      if (!this.loaded && !this.loading) this.load();
    },

    load: function() {
      this.loading = true;
      $http.get(this.href).success(function(response) {
        this.parse(response);
        this.loaded = true;
        this.loading = false;
        this.loadedDefer.resolve();
      }.bind(this));
    },

    parse: function(response) {
      // Parse properties
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
          // Make href absolute (concatenate with device href)
          node['href'] = URI(node['href']).absoluteTo(this.href).toString();
          return new Property(node);
        }
      }.bind(this)).filter({valid:true});

      // Set .groupCommEnabled=true for properties specified in the <ref>s
      propertiesWithGroupCommEnabled.each(function(name) {
        var p = this.properties.find({name:name});
        if (p) { p.groupCommEnabled = true; }
      }.bind(this));
    },

    // Updates property values from a watch response
    refresh: function(response) {
      // console.log("Updating",this.href);
      response['nodes'].each(function(node) {
        // console.log("Searching",node['href']);
        var property = this.properties.find({href:node['href']});
        if (property) {
          // console.log("Update",property.name, node['val']);
          property.value = node['val'];
        }
      }.bind(this));
    },

    destroy: function() {
      // Remove from watch
      var href = this.href;
      Watch.getInstance(function(w) { w.remove(href); });

      // Disconnect connected properties
      this.properties.each(function(p) {
        p.connections.each(function(c) { c.destroy(); });
        // Remove endpoints
        p.jsPlumbEndpoints.each(function(e) { jsPlumb.deleteEndpoint(e); });
      });

      // Unplace
      this.placement = null;
      this.placementStorage.remove();

      // Remove alias
      this.nameStorage.remove();
    }
  };
  
  return Device;
}]);

app.controller('MainCtrl', ['$scope','$q','$timeout','Lobby','Watch','Connection',function($scope, $q, $timeout, Lobby, Watch, Connection) {
  $scope.directory = null;
  $scope.allDevices = [];
  $scope.watch = null;

  var devicesInstantiatedDefer = $q.defer();
  var devicesInstantiatedWithPropertiesDefer = $q.defer();

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

  // When devices are known, we want to restore connections
  devicesInstantiatedWithPropertiesDefer.promise.then(function(placedDevices) {
    Connection.Freezer.restore(function(propertyHref) {
      return placedDevices.map('properties').flatten().find({href:propertyHref});
    }, function(connection) {
      $timeout(function(){
        console.log(connection);
        connection.jsPlumbConnection = jsPlumb.connect({
          source:connection.fromProperty.jsPlumbEndpoints[0],
          target:connection.toProperty.jsPlumbEndpoints[0]
        }, {parameters: {restored: true}});
        connection.jsPlumbConnection.obelixConnection = connection;
      },100); // TODO: fix timeout and connect when endpoints get available
    });
  });

  Lobby.getDeviceTree(function(root) {
    $scope.directory = root;
    $scope.allDevices = root.globDevices();

    var placedDevices = $scope.allDevices.filter(function(device) {
      return !!device.placement;
    });
    devicesInstantiatedDefer.resolve(placedDevices);

    $q.all(placedDevices.map(function(d) {
      return d.loadedDefer.promise;
    })).then(function(values) {
      devicesInstantiatedWithPropertiesDefer.resolve(placedDevices);
    });
  });

  Watch.getInstance(function(watch) {
    $scope.watch = watch;
    watch.startPolling(function(deviceJson) {
      var device = $scope.allDevices.find({href:deviceJson['href']});
      if (device) {
        console.log("Watch reports", deviceJson);
        device.refresh(deviceJson);
      }
    });
  });

  $scope.sidebarExpanded = false;
  
  $scope.placeDevice = function(device, position) {
    if (!device.placement) {
      // Initial placement, add to watch
      $scope.watch.add(device.href);
    }
    device.place(position);
  };

  $scope.clear = function() {
    // TODO: clear all connections, then devices!
  };

  jsPlumb.bind("connection", function(info) {
    console.log("Connection event", info);

    info.sourceEndpoint.addClass('connected');
    info.targetEndpoint.addClass('connected');

    if (info.connection.getParameter('restored')) return; // Ignore connect events for restored connections

    var sourceProperty = info.sourceEndpoint.getParameters().property;
    var targetProperty = info.targetEndpoint.getParameters().property;
    
    info.connection.obelixConnection = new Connection(sourceProperty, targetProperty);
    info.connection.obelixConnection.jsPlumbConnection = info.connection;
    Connection.Freezer.add(info.connection.obelixConnection);
  });

  jsPlumb.bind("connectionDetached", function(info) {
    [info.sourceEndpoint, info.targetEndpoint].each(function(ep) {
      if (ep.connections.length == 0) ep.removeClass('connected');
    });
  });

  jsPlumb.bind("dblclick", function(connection, e) {
    connection.obelixConnection.destroy();
    Connection.Freezer.remove(connection.obelixConnection);
    connection.obelixConnection = null;
  });

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


app.directive('includeDirectoryTemplate', ['$compile','$templateCache',function($compile, $templateCache) {
  return {
    restrict: 'A',
    terminal: true,
    scope: {directory:'=includeDirectoryTemplate'},
    link: function(scope, element, attrs) {
      var template = $templateCache.get('directory_template');
      element.append(template);
      $compile(element.contents())(scope.$new());
    }
  };
}]);

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
  };
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

app.directive('inlineEditor', ['$parse', function($parse) {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, el, attrs, ngModelCtrl) {
          var fn = $parse(attrs['inlineEditor']);

          el.bind("keydown keypress", function(event) {
            if (event.which === 13 || event.which === 27) {
              el.blur();
              event.preventDefault();
            }
          });

          el.bind('blur', function() {
            scope.$apply(function() {
              fn(scope);
              ngModelCtrl.$setViewValue(el.val());
            });
          });

          el.focus();
        }
    };
}]);

app.directive('jsplumbContainer', function() {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      jsPlumb.ready(function() {
        jsPlumb.Defaults.Container = el;
      });
    }
  };
});

app.directive('jsplumbEndpoint', ['$timeout', function($timeout) {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var property = scope.$eval(attrs['jsplumbEndpoint']);
      if (!property.groupCommEnabled) return;

      $timeout(function() {
        var ep = jsPlumb.addEndpoint(el, {
          isSource: true, 
          isTarget: true,
          maxConnections: -1,
          connector:[ "Bezier", { stub: 30, curviness:50 }], 
          endpoint: ["Rectangle", { width: 8, height: 8}],
          anchors: [[1, 0.5, 1, 0, 8,0], [0, 0.5, -1, 0, -8, 0]],
          paintStyle:{ fillStyle:"#666"}, 
          connectorStyle: { lineWidth: 4, strokeStyle: "#5b9ada"},
          parameters: {property: property}
        });
        property.jsPlumbEndpoints.push(ep);
      },0);  
    }
  };
}]);