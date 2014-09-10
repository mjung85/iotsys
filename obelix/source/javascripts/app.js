/*
 * Naming convention (apply if possible; also see filter htmlNameNormalizer): 
 * see https://code.google.com/p/iotsys/wiki/HowToHTMLClassAndIDNameFormat
 */

/*
 * AngularJS v1.2.1 is used.
 * https://code.angularjs.org/1.2.1/docs/api
 * 
 * Sugar v1.4.0 is used
 * If, at any point, you encounter code which might not look like ECMAScript, 
 * check out the sugar.js API: http://sugarjs.com/api
 * 
 * jQuery v2.0.3 is used (SizzleJS is included)
 * http://jquery.com/
 * 
 * jQuery UI v1.10.3 is used
 * http://jqueryui.com
 * 
 * jsPlumb v1.5.2 is usded for drawing connection endpoints and connections 
 * between devices.
 * http://jsplumb.org/doc/home.html
 * https://github.com/sporritt/jsplumb/
 * 
 * tourist.js is used for the UI tour (it uses qTip2)
 * https://easelinc.github.io/tourist/
 * 
 * CanvasJS v1.4.1 is used for drawing the statistic charts.
 * http://canvasjs.com/
 * 
 * qTip2 v2.2.0 is used by tourist.js and it is used for the splash and about modal 
 * dialog.
 * http://qtip2.com
 * 
 */
//= require 'sugar'
//= require 'jquery'
//= require 'jquery-ui'
//= require 'angular'
//= require 'html5slider'
//= require 'jquery.jsPlumb-1.5.2'
//= require 'URI'
//= require 'underscore'
//= require 'backbone'
//= require 'jquery.qtip'
//= require 'tourist'
//= require 'canvasjs'
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

    // expand: function() {
    // this.expanded = true;
    // if (this.subdirectories.count() == 1) {
    // // only one subdirectory, expand it
    // this.subdirectories[0].expand();
    // }
    // },

    // Recursively return all devices under this directory and its
    // subdirectories
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
    this.interval = (  null == this.interval || 
                      undefined == this.interval || 
                      isNaN(this.interval)) ? this.intervalMinimum : this.interval;
    this.updateInterval();
    this.href = href;
  };

  // A promise that will get resolved only when an old watch that we have in
  // localStorage is dead and we've created a new one
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
      return URI(this.href).segment(opName).toString(); // For now, operation
                                                        // href is same as name
    },
    
    add: function(href) {
      return this.op('add', href);
    },
    
    remove: function(href) {
      return this.op('remove', href);
    },
    
    op: function(op, href) {
      // var payload = {"is" : "obix:WatchIn", "tag" : "obj", "nodes" : [{"tag"
      // : "list", "name": "hrefs", "nodes":[{"tag": "uri", "val": href}]}]};
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
      $http.put(this.href, {'tag': this.type, 'val': this.value }, {
        headers: {
          'Content-Type': 'application/json'
        }
      }).success(function(response) {
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

  // Small utility "module" to de/serialize connections to localstorage, as they
  // aren't enumerable in the lobby
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

app.factory('Device', ['$http', '$q', '$timeout', '$filter', 'Storage', 'Property', 'Watch', 'Connection',function($http, $q, $timeout, $filter, Storage, Property, Watch, Connection) {
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
    this.obix = {
        contractList: {
          'is': null,
          'of': null,
          'in': null,
          'out': null
        }
    };
    this.statistics = {
        history: {
          enabled: false,
          properties: []
        },
        statisticBox: {
            expanded: false,
            chartsContainerContainer: null
        }
    };
    
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
      // console.log(response);
      if (response['is']) {
        this.obix.contractList['is'] = response['is'];
      }
      if (response['of']) {
        this.obix.contractList['of'] = response['of'];
      }
      if (response['in']) {
        this.obix.contractList['in'] = response['in'];
      }
      if (response['out']) {
        this.obix.contractList['out'] = response['out'];
      }
      
      // Parse properties
      var propertiesWithGroupCommEnabled = [];
      this.properties = response['nodes'].map(function(node) {
        if (node['tag'] == 'ref') {
          var names = node['name'] ? node['name'].split(' ') : '';
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
      
      var _self = this;
      // Retrieve the links to history enabled values
      response['nodes'].map(function(node) {
        if (node['is'] == 'obix:History') {
          var _href = _self.href + node.href.substring(node.href.indexOf('/'));
          _self.statistics.history.enabled = true;
          _self.statistics.history.properties.push({
            name: node.name.substring(0, node.name.lastIndexOf(' history')),
            href: _href,
            chart: null,
            chartDataPoints: [],
            chartContainerID: $filter('htmlNameNormalizer')(_href),
            chartLineColor: null
          });
        }  
      });

      // Set .groupCommEnabled=true for properties specified in the <ref>s
      propertiesWithGroupCommEnabled.each(function(name) {
        var p = this.properties.find({name:name});
        if (p) { p.groupCommEnabled = true; }
      }.bind(this));
    },

    // Updates property values from a watch response
    refresh: function(response) {
      console.log("Updating",this.href);
      response['nodes'].each(function(node) {
        // console.log("Searching",node['href']);
        var property = this.properties.find({href:node['href']});
        if (property) {
          // console.log("Update",property.name, node['val']);
          property.value = node['val'];
        }
      }.bind(this));
    },

    toggleStatisticBox: function() {
      this.statistics.statisticBox.expanded = !this.statistics.statisticBox.expanded;
    },
    
    destroy: function() {
      // Remove from watch
      var href = this.href;
      Watch.getInstance(function(w) { w.remove(href); });
      
      // Disconnect connected properties
      if (this.properties) {
        this.properties.each(function(p) {
          p.connections.each(function(c) { Connection.Freezer.remove(c); });
          // Remove endpoints
          p.jsPlumbEndpoints.each(function(e) { jsPlumb.removeAllEndpoints(e.element); });
        });
      }
      
      // Unplace
      this.placement = null;
      this.placementStorage.remove();
      
      // Remove alias
      this.nameStorage.remove();
    }
  };
  
  return Device;
}]);

/*
 * AngularJS service Sidebar
 * 
 * The object returned by this factory represents the current state 
 * of the sidebar. Its state is persisted in the localStorage of the browser. 
 */
app.factory('Sidebar', ['Storage', function(Storage) {
  var _expandedStorage = new Storage('sidebar_expanded');
  var _expanded = ('true' === _expandedStorage.get());
  var _lockedStorage = new Storage('sidebar_locked');
  var _locked = ('true' === _lockedStorage.get());
  var _segmentStorage = new Storage('sidebar_segment');
  var _segment = parseInt(_segmentStorage.get(), 10);
  if (Number.isNaN(_segment)) {
    _segment = 0
  }
  
  return {
    get expanded() {
      return _expanded;
    },
    set expanded(newValue) {
      if (! _locked) {
        _expanded = newValue;
        _expandedStorage.set(newValue);
      }
    },
    toggle: function() {
      if (! _locked) {
        _expanded = !_expanded;
        _expandedStorage.set(_expanded);
      }
    },
    get locked() {
      return _locked;
    }, 
    set locked(newValue) {
      _locked = newValue;
      _lockedStorage.set(newValue);
    },
    get segment() {
      return _segment;
    }, 
    set segment(newValue) {
      _segment = newValue;
      _segmentStorage.set(newValue)
    }
  };
}]);

/*
 * AngularJS service DeviceStatistics
 * 
 * This service is used to eventually add/remove devices to/from the statistic menu. 
 * 
 * lineColors[] contains the hex codes of the chart lines. Once a property has 
 * a line color assigned for its chart, it is not changed.
 *  
 * It provides the following methods:
 * .) bool isRegisteredDevice(device)
 * .) void addDevice(device)
 * .) void removeDevice(device)
 * .) void query()
 *    The historical values of each property of each registered device are 
 *    queried ca. every 5000 msec
 */
app.factory('DeviceStatistics', ['$http', '$interval', function($http, $interval) {
  var _devices = [];
  var _additionalDevicesData =  [];
  
  var lineColors = ['#C7A317', '#E41B17', '#7E354D', '#FF00FF', '#893BFF', '#F88017', '#F88017', '#F88017'];
  var lineColorIndex = 0;
  var lineColorsLength = lineColors.length;
  
  function getHistoryQueryPayload() {
    return ['<obj is="obix:HistoryFilter">'
      , '<int name="limit" val="10"/>'
      , '<abstime name="start" val="1970-01-01T00:00:00.000Z"/>'
      , '<abstime name="end" val="' 
      , new Date().toISOString() 
      , '"/>'
      , '</obj>'].join('');
  }
  
  return {
    devices: _devices,
    isRegisteredDevice: function(device) {
      return _devices.indexOf(device) >= 0;
    },
    addDevice: function(device) {
      if (this.isRegisteredDevice(device)) {
        return;
      }
      _devices.push(device);
    },
    removeDevice: function(device) {
      var deviceIndex = _devices.indexOf(device);
      if (-1 == deviceIndex) {
        return;
      }
      _devices.splice(deviceIndex, 1);
      device.statistics.history.properties.each(function(property) {
        property.chart = null;
      });
    },
    query: function() {
      $interval(function() {
        _devices.forEach(function(_device) {
          _device.statistics.history.properties.forEach(function(property){
            if (null == property.lineColor) {
              property.lineColor = lineColors[lineColorIndex % lineColorsLength];
              lineColorIndex++;
            }
            $http.post(property.href + '/query', getHistoryQueryPayload(), {
              headers: {
                'Content-Type': 'text/xml',
                'Accept': 'application/json'
              }
            }).success(function(data, status, headers, config) {
              /* A chart object is created only once for each property and also 
               * the data source containing the values of the property is created 
               * once. 
               * After the creation of the chart object, it remembers its data source
               * and therefore splice() is used to operate on the array created initially 
               * as the data source of the chart. Assiging a new empty array to 
               * property.chartDataPoints does not change the reference the chart uses for 
               * its data source. Avoid creating a new array object! 
               */
              property.chartDataPoints.splice(0);
              if (! data.nodes[3].nodes) {
                jQuery('#'+property.chartContainerID).text(property.name + ': Currently no data available.');
                return;
              }
              data.nodes[3].nodes.each(function(nodes){
                property.chartDataPoints.push({
                  x: new Date(nodes.nodes[0].val),
                  y: nodes.nodes[1].val
                });
              });
              if (null == property.chart) {
                property.chart = new CanvasJS.Chart(property.chartContainerID, {
                  theme: "theme2",
                  title:{
                    text: property.name
                  },
                  width: 260,
                  height: 200,
                  axisY: {
                    interval: 25
                  },
                  data: [{        
                    type: "line",
                    lineThickness: 2,  
                    dataPoints: property.chartDataPoints,
                    color: property.lineColor
                  }]
                });
              }
              property.chart.render();
              });
            });
        });
      }, 5000);
    }
  }
}]);

/*
 * AngularJS service ProjectMembers
 * 
 * This service provide the names and other details of the project members. It is 
 * put in the scope of the controller MainCtrl (see app.controller('MainCtrl')). Its 
 * usage can be seen in index.haml.
 */
app.factory('ProjectMembers', [function() {
  var ProjectMember = function(firstName, lastName, website) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.website = website;
  };
  
  return {
    leads: [new ProjectMember('Markus', 'J.', 'https://www.auto.tuwien.ac.at/people/view/Markus_Jung/')],
    //In alphabetical order of the first name
    contributors: [new ProjectMember('Clemens', 'P.'),
                   new ProjectMember('Esad', 'H.'),
                   new ProjectMember('Isolde', 'C.'),
                   new ProjectMember('Jomy', 'C.'),
                   new ProjectMember('Jürgen', 'S.'),
                   new ProjectMember('Jürgen', 'W.', 'https://www.auto.tuwien.ac.at/people/view/Juergen_Weidinger/'),
                   new ProjectMember('Luyu', 'Z.'),
                   new ProjectMember('Nam', 'G.'),
                   new ProjectMember('Ralph', 'H.'),
                   new ProjectMember('Robert', 'H.'),
                   new ProjectMember('Stefan', 'S.'),
                   new ProjectMember('Thomas', 'H.')]
  }
}]);

/*
 * AngularJS controller MainCtrl
 * 
 * Read https://code.google.com/p/iotsys/w/list to understand the communication 
 * between client and server.
 */
app.controller('MainCtrl', ['$scope','$q','$timeout', '$interval', 'Lobby','Watch','Connection', 'Sidebar', 'DeviceStatistics', 'ProjectMembers', function($scope, $q, $timeout, $interval, Lobby, Watch, Connection, Sidebar, DeviceStatistics, ProjectMembers) {
  // Modal-specific z-index
  jQuery.fn.qtip.modal_zindex = jQuery.fn.qtip.zindex + 1000;
  
  $scope.directory = null;
  $scope.allDevices = [];
  $scope.watch = null;
  $scope.statistics = DeviceStatistics;
  $scope.projectMembers = ProjectMembers;

  $scope.statistics.query();
  
  var devicesInstantiatedDefer = $q.defer();
  var devicesInstantiatedWithPropertiesDefer = $q.defer();
  var jsPlumbEndpointRectsRendereredDefer = $q.defer();
  
  // The rects are the connection endpoints (yellow rectangles).
  var expectedJsPlumbEndpointRects = null;
  var jsPlumbEndpointRectsVisibleCheckIntervalTimerPromise = $interval(function() {
    //jQuery('rect:visible') returns 0 in Google Chrome. 
    var rectsVisible = jQuery('rect:visible').length || jQuery('rect').length;
    if (rectsVisible === expectedJsPlumbEndpointRects) {
      $interval.cancel(jsPlumbEndpointRectsVisibleCheckIntervalTimerPromise);
      jsPlumbEndpointRectsRendereredDefer.resolve();
    }
  }, 250);
  
  $q.all([
    Watch.watchRecreatedDefer.promise,
    devicesInstantiatedDefer.promise
  ]).then(function(values) {
    // Promises resolved. Watch and devices are available; re-add them
    // to the new watch
    console.log("Re-adding placed devices to new watch");
    var watch = values[0];
    var placedDevices = values[1];
    placedDevices.each(function(device) { watch.add(device.href); });
  });

  // When devices are known, restore connections
  devicesInstantiatedWithPropertiesDefer.promise.then(function(placedDevices) {
    placedDevices.map(function(device){
      device.properties.map(function(property){
        if (property.groupCommEnabled) {
          expectedJsPlumbEndpointRects++;
        }
      });
    });
    Connection.Freezer.restore(function(propertyHref) {
      return placedDevices.map('properties').flatten().find({href:propertyHref});
    }, function(connection) {
      jsPlumbEndpointRectsRendereredDefer.promise.then(function() {
        console.log(connection);
        connection.jsPlumbConnection = jsPlumb.connect({
              source:  connection.fromProperty.jsPlumbEndpoints[0],
              target:  connection.toProperty.jsPlumbEndpoints[0]
            }, {
              parameters: {restored: true}
            });
        connection.jsPlumbConnection.obelixConnection = connection;
      });
    });
    
   $timeout(function(){
      jQuery('body').trigger('hideSplashScreen');
      jQuery('#tour-first-time-visitor').trigger('showTourHelp');
    }, 0);
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
  
  $scope.sidebar = Sidebar;
  
  jsPlumb.bind("connection", function(info) {
    console.log("Connection event", info);

    info.sourceEndpoint.addClass('connected');
    info.targetEndpoint.addClass('connected');
    
    if (info.connection.getParameter('restored')) {
      // Ignore connect events for restoredvconnections
      return; 
    }
                                                          
    var sourceProperty = info.sourceEndpoint.getParameters().property;
    var targetProperty = info.targetEndpoint.getParameters().property;
    
    info.connection.obelixConnection = new Connection(sourceProperty, targetProperty);
    info.connection.obelixConnection.jsPlumbConnection = info.connection;
    Connection.Freezer.add(info.connection.obelixConnection);
    console.log("the first connection event listener;")
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
  
  $scope.tourInProgress = false;
  
  $scope.placeDevice = function(device, position) {
    if (!device.placement) {
      // Initial placement, add to watch
      $scope.watch.add(device.href);
    }
    device.place(position);
  };
  
  $scope.destroyDevice = function(device) {
    if ($scope.tourInProgress) {
      return;
    }
    device.destroy();
    $scope.statistics.removeDevice(device);
  }

  $scope.clear = function() {
    $scope.allDevices.filter(function(device) {
      if(!!device.placement) {
        $scope.destroyDevice(device);
      }
    });
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
        },
        containment: jQuery('#canvas')
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
// return {
// restrict: 'A',
// link: function(scope, el, attrs) {
// el.droppable({
// greedy: true,
// drop: function(event,ui) {
// console.log('swallow');
// }
// });
// }
// }
// });


app.directive('includeStatisticsTemplate', ['$compile','$templateCache',function($compile, $templateCache) {
  return {
    restrict: 'A',
    terminal: true,
    scope: {statistics:'=includeStatisticsTemplate'},
    link: function(scope, element, attrs) {
      var template = $templateCache.get('statistics-template');
      element.append(template);
      $compile(element.contents())(scope.$new());
    }
  };
}]);

app.directive('includeDirectoryTemplate', ['$compile','$templateCache',function($compile, $templateCache) {
  return {
    restrict: 'A',
    terminal: true,
    scope: {directory:'=includeDirectoryTemplate'},
    link: function(scope, element, attrs) {
      var template = $templateCache.get('directory-template');
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
      if (attr.type === 'radio' || attr.type === 'checkbox') {
        return;
      }
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
          jsPlumb.Defaults.Connector = [ "Bezier", { stub: 30, curviness:50 }];
          jsPlumb.Defaults.Endpoint = ["Rectangle", { width: 12, height: 15}];
        });
    }
  };
});

app.directive('jsplumbEndpoint', ['$timeout', '$filter', function($timeout, $filter) {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var property = scope.$eval(attrs['jsplumbEndpoint']);
      if (!property.groupCommEnabled) return;
      var device = scope.$eval(attrs['device']);
      
      $timeout(function() {
        var ep = jsPlumb.addEndpoint(el, {
          isSource: true, 
          isTarget: true,
          cssClass: $filter('htmlNameNormalizer')(device.originalName),
          parent: el.parent(),
          maxConnections: -1,
          anchors: [[1, 0.5, 1, 0, 12,0], [0, 0.5, -1, 0, -12, 0]],
          paintStyle:{ fillStyle:"#ff0"}, 
          connectorStyle: { lineWidth: 7, strokeStyle: "#fff"},
          connectorHoverStyle: { strokeStyle:"#ff0000" },
          parameters: {property: property}
        });
        property.jsPlumbEndpoints.push(ep);
      },0);  
    }
  };
}]);

/*
 * AngularJS directive obelix-about-starter
 * 
 * This directive is responsible for displaying a splash screen (modal dialog) 
 * during the initialization of the web UI.
 * The directive uses the qtip2 library.
 * Check out their website: http://qtip2.com/
 */
app.directive('obelixSplashScreen', [function() {
  return {
    restrict: 'A',
    link: function(scope, elem, attrs) {
      jQuery(elem).qtip({
        prerender: true,
        content: {
          text: function() {
            var aboutClone = $('#about').clone();
            aboutClone
              .find('#about-content')
              .append('<div class="row-2"><div class="loading"></div><div class="text">Loading ... Please Wait</div></div>');
            return aboutClone;
          }
        },
        position: {
          my: 'center',
          at: 'center'
        }, 
        show: {
          delay: 0,
          ready: true,
          modal: {
              on: true,
              blur: false,
              escape: false,
              stealfocus: false
          }
        },
        hide: {
            event: 'hideSplashScreen'
        },
        style: {
          classes: 'qtip-light obelix-qtip'
        }, 
        events:  {
          hide: function(event, api) {
            api.destroy();
          }
        }
      });
    }
  };
}]);

/*
 * AngularJS directive obelix-logout-starter
 * 
 * This directive is responsible for terminating the current UI session.
 */
app.directive('obelixLogoutStarter', ['$window', function($window) {
  return {
    restrict: 'A',
    link: function(scope, elem, attrs) {
      elem
        .addClass('enabled')
        .attr('title', 'Logout')
        .click(function(){
          $window.location = '/logout';
        });
    }
  };
}]);

/*
 * AngularJS directive obelix-about-starter
 * 
 * This directive is responsible for displaying an 'About' modal dialog.
 * The directive uses the qtip2 library.
 * Check out their website: http://qtip2.com/
 */
app.directive('obelixAboutStarter', [function() {
  var aboutClone;
  return {
    restrict: 'A',
    link: function(scope, elem, attrs) {
      elem
        .addClass('enabled')
        .attr('title', 'About')
        .click(function(){
          jQuery('body').qtip({
            content: {
              text: function() {
                aboutClone = jQuery('#about').clone();
                aboutClone
                  .css('height', 'auto')
                  .find('#about-content')
                  .append(jQuery('#project-members').clone());
                aboutClone.find('button').click(function() {
                  jQuery('body').trigger('hideAboutScreen');
                });
                return aboutClone;
              },
            },
            position: {
                my: 'center',
                at: 'center'
            }, 
            show: {
              event: 'showAboutScreen',
              delay: 100,
              ready: true,
              modal: {
                  on: true,
                  blur: false,
                  escape: true,
                  stealfocus: false
              }
            },
            hide: {
                event: 'hideAboutScreen'
            },
            style: {
              classes: 'qtip-light obelix-qtip'
            }, 
            events: {
              visible: function(event, api) {
                aboutClone.animate({'margin-top': aboutClone.height()/-2}, 1000);
              }
            }
          });
        });
    }
  };
}]);

/*
 * AngularJS directive tour-device
 * 
 * This directive marks device boxes in the device menu of the sidebar with 
 * an unique id so that they can be referenced/highlighted during the website 
 * tour.
 */
app.directive('tourDevice', ['$filter', function($filter) {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var deviceName = $filter('htmlNameNormalizer')(scope.$eval(attrs['tourDevice']).originalName);
      if (deviceName === 'virtual-push-button') {
        el.attr('id', 'tour-device-button');
      } else if (deviceName === 'virtual-light') {
        el.attr('id', 'tour-device-light');
      } else if (deviceName === 'v-complex-sun-blind') {
        el.attr('id', 'tour-device-history')
      }
    }
  };
}]);

/*
 * AngularJS directive obelix-tour-first-time-visitor
 * 
 * This directive is responsible for displaying a hint bubble for first time 
 * visitors to take the website tour.
 * The directive uses the same tour library as the directive obelix-tour-starter.
 * Check out the comment of that directive.
 */
app.directive('obelixTourFirstTimeVisitor', ['Storage', function(Storage) {
  return {
    restrict: 'A',
    link: function(scope, elem, attrs) {

      var tourTakenStorage = new Storage('tourTaken');
      elem.qtip({
        content: {
          title: {
            button: jQuery('<span>x</span>').addClass('icon icon-remove qtip-close'),
            text: 'Need help?'
          },            
          text: 'Click on the "?" symbol to start the tour!'
        },
        show: {
          event: 'showTourHelp'
        },
        hide: {
          event: 'hideTourHelp'
        },
        position: {
            my: 'right top',
            at: 'left bottom'
        }, 
        style: {
          classes: 'qtip-light obelix-qtip'
        },
        events: {
          show: function(event, api) {
            if ('tourTaken' === tourTakenStorage.get()) {
              event.preventDefault();
            }
          }
        }
      });
    }
  };
}]);

/*
 * AngularJS directive obelix-tour-starter
 * 
 * This directive is responsible for the website tour.
 * Although it is a little bit long, it is relative simply.
 * It uses the tour library https://easelinc.github.io/tourist/.
 * Check out the doc/examples/FAQs!
 * 
 * -> Inside and outside the object that is returned, there are 
 * some helper functions.
 * 
 * -> The object tourSteps contains the text and configuration of 
 * each tour step/bubble.
 * 
 * -> A tour step has several options and most are self-explanatory.
 * 
 * -> For every tour step the property "setup" has to reference a function which 
 * contains at least:  
 *        function(tour, options) {
 *          obelixTour.step = this;
 *        }
 * Otherwise the title of a step of is not displayed in the bubble header.
 * 
 * -> Try to make each step as independent as possible, so that when reordering 
 * of the steps is required, it can be done easily (i. e. think about the functions 
 * referenced by the properties 'setup' and 'teardown').
 * 
 */
app.directive('obelixTourStarter', ['$timeout', 'Sidebar', 'Storage', function($timeout, Sidebar, Storage) {
  
  function toggleSidebarButton(enableToggle) {
    Sidebar.locked = !enableToggle;
  }
  
  function showSidebar(showSidebar) {
    Sidebar.expanded = showSidebar;
  }
  
  function showSidebarSegmentDevices() {
    Sidebar.segment = 0;
  }
  
  function showSidebarSegmentStatistics() {
    Sidebar.segment = 1;
  }
  
  function showSidebarSegmentSettings() {
    Sidebar.segment = 2;
  }
  
  return {
    restrict: 'A',
    link: function(scope, elem, attr) {
      
      function tourInProgress(started) {
        $timeout(function() {
          scope.tourInProgress = started;
        }, 0);
      }
      
      function toggleTourStarter(toggle) {
        if (toggle) {
          jQuery(elem)
            .off()
            .on('click', obelixTour.start)
            .attr('title', 'Start the UI tour')
            .addClass('enabled')
            .removeClass('disabled')
        } else {
          jQuery(elem)
            .off()
            .attr('title', 'UI tour has started')
            .addClass('disabled')
            .removeClass('enabled');
        }
      }
      
      var obelixTour = {};
      
      var tourSteps = [{
          title: 'Menu Button',
          content: '<p>This is the menu toggle button. Click it to open/close the menu.</p>',
          closeButton: true,
          highlightTarget: true,
          target: jQuery('div#toggle-sidebar'),
          my: 'left center',
          at: 'right center',
          bind: ['onToggleButtonClick'],
          onToggleButtonClick: function(tour, options, view, element) {
            tour.next();
          },
          setup: function(tour, options) {
            obelixTour.step = this;
            toggleSidebarButton(true);
            showSidebar(false);
            this.target.bind('click', this.onToggleButtonClick);
          },
          teardown: function(tour, options) {
            this.target.unbind('click', this.onToggleButtonClick);
            toggleSidebarButton(false);
          }
        }, {
          title: 'Menu', 
          content: '<p>The menu contains three main sections: "Devices", "Statistics" and "Settings".</p>',
          closeButton:true,
          highlightTarget: false,
          nextButton: true,
          target: jQuery('div#sidebar-sublayer'),
          my: 'left center',
          at: 'right center',
          setup: function(tour, options) {
            obelixTour.step = this;
            jQuery('#sidebar')
              .addClass('tour-highlight')
              .children('.content:first')
                .scrollTop(0);
          },
          teardown: function(tour, options) {
            jQuery('#sidebar').removeClass('tour-highlight');
          }
        }, {
          title: 'Section "Settings"', 
          content: '<p>This section provides the possibility to control some aspects of this client. </p>',
          closeButton:true,
          highlightTarget: true,
          nextButton: true,
          target: jQuery('div#segment-settings'),
          my: 'top left',
          at: 'bottom right',
          setup: function(tour, options) {
            obelixTour.step = this;
            $timeout(function() {
              jQuery('#sidebar > .content:first').scrollTop(0);
              showSidebarSegmentSettings();
            }, 0);
          },
          teardown: function(tour, options) {
            $timeout(function() {
              showSidebarSegmentStatistics();
              scope.directory.subdirectories[1].expanded=true;
            }, 0);
          }
        }, {
          title: 'Section "Statistics"', 
          content: '<p>This section displays some statistics of devices (e. g. value history). </p>',
          closeButton:true,
          highlightTarget: true,
          nextButton: true,
          target: jQuery('div#segment-statistics'),
          my: 'top left',
          at: 'bottom right',
          setup: function(tour, options) {
            obelixTour.step = this;
            $timeout(function() {
              jQuery('#sidebar > .content:first').scrollTop(0);
              showSidebarSegmentStatistics();
            }, 0);
          },
          teardown: function(tour, options) {
            $timeout(function() {
              scope.directory.subdirectories[1].expanded=true;
              showSidebarSegmentDevices();
            }, 0);
          }  
        }, {
          title: 'Section "Devices"', 
          content: '<p>This section contains the list of all known devices (represented as blocks), which are organized in lists (a list may contain sublists). A list can be opened/closed by clicking on it.</p>',
          closeButton:true,
          highlightTarget: true,
          nextButton: true,
          target: jQuery('div#segment-devices'),
          my: 'top left',
          at: 'bottom right',
          setup: function(tour, options) {
            obelixTour.step = this;
            showSidebarSegmentDevices();
            // document.getElementById('#canvas
            // .content').scrollIntoView(false);
          },
          teardown: function(tour, options) {
          }
        }, {
         title: 'Virtual Push Button',
         content: '<p>Drag and drop the virtual push button device block onto the grid.</p>',
         closeButton: true,
         highlightTarget: true,
         nextButton: false,
         my: 'bottom center',
         at: 'top center',
         setup: function(tour, options) {
           obelixTour.step = this;
           document.getElementById('tour-device-button').scrollIntoView(false);
           options.tourDeviceDropZone
             .css({'top': '30px', 'left': '50%'})
             .addClass('tour-highlight')
             .droppable()
             .droppable({
               accept: 'label#tour-device-button',
               drop: function( event, ui ) {
                 options.droppedDeviceButton = jQuery(event.target);
                 options.droppedDeviceButton
                   .droppable()
                   .droppable('destroy');
                 tour.next();
               }
             });
           return {
             target: jQuery('label#tour-device-button')
           }
         },
         teardown: function(tour, options) {
           options.tourDeviceDropZone
             .removeClass('tour-highlight');
           options.droppedDevices.push(jQuery('#canvas .device.virtual-push-button').draggable('disable'));
         }
        }, {
         title: 'Virtual Light',
         content: '<p>Drag and drop the virtual light device block onto the grid.</p>',
         closeButton: true,
         highlightTarget: true,
         nextButton: false,
         my: 'bottom left',
         at: 'top right',
         setup: function(tour, options) {
           obelixTour.step = this;
           document.getElementById('tour-device-light').scrollIntoView(false);
           options.tourDeviceDropZone
             .css({'top': '280px', 'left': '30%'})
             .addClass('tour-highlight')
             .droppable()
             .droppable({
               accept: 'label#tour-device-light',
               drop: function( event, ui ) {
                 options.droppedDeviceLight = jQuery(event.target)
                 options.droppedDeviceLight
                   .droppable()
                   .droppable('destroy');
                 tour.next();
               }
             });
           return {
             target: jQuery('label#tour-device-light')
           }
         },
         teardown: function(tour, options) {
           options.tourDeviceDropZone
             .removeClass('tour-highlight')
           options.droppedDevices.push(jQuery('#canvas .device.virtual-light').draggable('disable'));
         }
        }, {
         title: 'Device Box (1/2)',
         content: '<p>A device box can be deleted from the configuration by clicking on the button "x" (during the tour the button\'s functionality has been disabled). A device box contains interactive elements to modify its behaviour. It can also be  moved around using the mouse (but during the tour it is not possible to move around the device boxes of the virtual push button, virtual light and virtual complex sun blind).</p>',
         closeButton: true,
         highlightTarget: true,
         nextButton: true,
         my: 'top left',
         at: 'bottom center',
         setup: function(tour, options) {
           obelixTour.step = this;
           return {
             target: options.droppedDevices[0]
           }
         },
         teardown: function(tour, options) {
         }
        }, {
         title: 'Endpoint',
         content: '<p>A device box may have endpoints like this one, which can be connected to other endpoints.</p>',
         closeButton: true,
         highlightTarget: true,
         nextButton: true,
         my: 'left top',
         at: 'right center',
         setup: function(tour, options) {
           obelixTour.step = this;
           return {
             target: jQuery('#canvas ._jsPlumb_endpoint.virtual-push-button')
           }
         },
         teardown: function(tour, options) {
         }
        }, {
         title: 'Connect Endpoints',
         content: '<p>Connect the endpoint of the virtual push button with the endpoint of the virtual light by drawing a line. Click with the primary mouse button on a endpoint and keep the mouse button pressed. Move the mouse pointer over to the other endpoint and release the mouse button afterwards.</p><p>A connection can be deleted by double-clicking on the connection line. All connections of a device are deleted when the device is deleted.</p>',
         nextButton: true,
         closeButton: true,
         highlightTarget: true,
         my: 'left top',
         at: 'right center',
         setup: function(tour, options) {
           obelixTour.step = this;
           jQuery('#canvas ._jsPlumb_endpoint.virtual-light, #canvas ._jsPlumb_endpoint.virtual-push-button')
               .addClass('tour-highlight');
           return {
             target: jQuery('#canvas ._jsPlumb_endpoint.virtual-push-button')
           }
         },
         teardown: function(tour, options) {
           jQuery('#canvas ._jsPlumb_endpoint.virtual-light, #canvas ._jsPlumb_endpoint.virtual-push-button')
             .removeClass('tour-highlight');
         }
        }, {
          title: 'Statistics',
          content: '<p>Drag and drop the virtual complex sun blind device block onto the grid.</p>',
          closeButton: true,
          highlightTarget: true,
          nextButton: false,
          my: 'bottom left',
          at: 'top right',
          setup: function(tour, options) {
            obelixTour.step = this;
            document.getElementById('tour-device-history').scrollIntoView(false);
            options.tourDeviceDropZone
              .css({'top': '280px', 'left': '60%'})
              .addClass('tour-highlight')
              .droppable()
              .droppable({
                accept: 'label#tour-device-history',
                drop: function( event, ui ) {
                  options.droppedDeviceHistory = jQuery(event.target)
                  options.droppedDeviceHistory
                    .droppable()
                    .droppable('destroy');
                  tour.next();
                }
              });
            return {
              target: jQuery('label#tour-device-history')
            }
          },
          teardown: function(tour, options) {
            options.tourDeviceDropZone
              .removeClass('tour-highlight')
            options.droppedDevices.push(jQuery('#canvas .device.v-complex-sun-blind').draggable('disable'));
          }
        }, {
          title: 'Device Box (2/2)',
          content: '<p>Statistics of a device box can be displayed in the sidebar section "Statistics" by clicking on the button "%". If a device has its statistical functionality enabled the button will turn and stay green until the button is clicked again. Clicking a second time, removes the device from the statistic. If the statistical functionality is not enabled for a device the button will turn and stay gray. Click on the button "%" to add this device to the statistic and set the sliders to a few different positions. Clicking on the device name in the statistic section will toggle the statistic information display of a device. The charts are interactive (tooltips are displayed when the mouse pointer is hovered over a datapoint) and redrawn every 5 seconds and the latest 10 values are dislpayed.</p>',
          closeButton: true,
          highlightTarget: true,
          nextButton: true,
          my: 'center left',
          at: 'center right',
          setup: function(tour, options) {
            obelixTour.step = this;
            $timeout(function() {
              jQuery('#sidebar > .content:first').scrollTop(0);
              showSidebarSegmentStatistics();
            }, 0);
            return {
              target: options.droppedDevices[2]
            }
          },
          teardown: function(tour, options) {
          }
         }, {
          title: 'Logout',
          content: '<p>Click this symbol to end the current oBeliX session.</p>',
          nextButton: true,
          closeButton: true,
          my: 'top right',
          at: 'bottom left',
          setup: function(tour, options) {
            obelixTour.step = this;
            return {
              target: jQuery('#logout-starter')
            }
          }
         }];
      
      var tourStarterRebindStep = {
        title: 'End Of Tour',
        content: '<p>This is the end of the tour. Thanks!',
        closeButton: true,
        target: elem,
        nextButton: true,
        my: 'top right',
        at: 'bottom left',
        setup: function(tour, options) {
          obelixTour.step = this;
        },
        teardown: function(tour, options) {
          options.setDragOnDroppedDevices(true);
          options.droppedDevices = [];
          toggleSidebarButton(true);
          tourInProgress(false);
          toggleTourStarter(true);
        }
      };
      
      var tour = new Tourist.Tour({
        tipClass: 'QTip',
        tipOptions: {
          style: {
            classes: 'qtip-light qtip-shadow obelix-tour obelix-qtip'
          },
          overwrite: false,
          content: {
            title: 'Obelix Tour'
          },
          show: {
            solo: true
          },
          events: {
            show: function(event, api) {
              var qtip = jQuery(event.target);
              var closeButton = qtip.find('.tour-close');
              var qtipTitleBar = qtip.find('.qtip-titlebar');
              
              closeButton.each(function() {
                if (! jQuery(this).hasClass('qtip-close')) {
                  jQuery(this)
                    .addClass('qtip-close')
                    .html('<span class="icon icon-remove">x</span>')
                    .appendTo(qtipTitleBar);
                } else {
                  jQuery(this).detach();
                }
              });
              jQuery('.qtip-title', qtipTitleBar).html(obelixTour.step.title);  
            }  
          }
        },
        steps: tourSteps,
        stepOptions: {
          tourDeviceDropZone: jQuery('#canvas #tour-device-drop-zone'),
          droppedDevices: [],
          setDragOnDroppedDevices: function (boolDrag) {
            var stringDrag = boolDrag ? 'enable' : 'disable';
            this.droppedDevices.map(function(device) {
              device.draggable(stringDrag);
            });
          }
        },
        successStep: tourStarterRebindStep,
        cancelStep: tourStarterRebindStep
      });
      
      obelixTour.start = function (event) {
        var tourTakenStorage = new Storage('tourTaken');
        tourTakenStorage.set('tourTaken');
        toggleTourStarter(false);
        tourInProgress(true);
        tour.start();
      }
      toggleTourStarter(true);
    }
  }
}]);

/*
 * AngularJS directive toggle-device-statistic
 * 
 * This directive implements the behaviour of the device button "%".
 * It expects one argument: a device object.  
 */
app.directive('toggleDeviceStatistic', ['DeviceStatistics', function(DeviceStatistics) {
  var inStatisticsHTMLClass = 'in-statistic';
  return {
    restrict: 'A',
    link: function(scope, elem, attrs) {
      var device = scope.$eval(attrs['toggleDeviceStatistic']);
      if (elem.hasClass(inStatisticsHTMLClass)) {
        elem.attr('title', 'Remove device from the statistics menu');
      } else {
        elem.attr('title', 'Add device to the statistics menu');        
      }
      elem.click(function(){
          if (! device.statistics.history.enabled) {
            elem
              .addClass('no-statistic')
              .attr('title', 'History function is not enabled on this device');
            return;
          }
          if (elem.hasClass(inStatisticsHTMLClass)) {
            DeviceStatistics.removeDevice(device);
            elem
              .removeClass(inStatisticsHTMLClass)
              .attr('title', 'Add device to the statistics menu');
          } else {
            DeviceStatistics.addDevice(device);
            elem
              .addClass(inStatisticsHTMLClass)
              .attr('title', 'Remove device from the statistics menu');
          }
        });
    }
  };
}]);

/*
 * AngularJS filter comparatorOpEnc
 * 
 * In order to not "break" the device layout because of long names 
 * in a drop-down list, the filter comparatorOpEnc encodes the 
 * original name with a shorter name.
 * Hint: if you do not understand the symbols in the short names, 
 * read a regular expression tutorial (e. g. 
 * http://www.regular-expressions.info/tutorial.html)  
 */
app.filter('comparatorOpEnc', function() {
  return function(operation, device) {
    if (angular.isString(operation)) {
      var encodedOp = operation;
      var deviceContractIs = device.obix.contractList.is;
      if ('iot:StringComparator' == deviceContractIs) {
        switch(operation) {
        case 'eq':          encodedOp = 'eq'; break;
        case 'startsWith':  encodedOp = '^x*'; break;
        case 'endsWith':    encodedOp = '*x$'; break;
        case 'contains':    encodedOp = '*x*'; break;
        default:            encodedOp = operation; break;
        }
      } else if ('iot:Comparator' == deviceContractIs) {
        switch(operation) {
        case 'lt':  encodedOp = '<'; break;
        case 'lte': encodedOp = '\u2264'; break;
        case 'eq':  encodedOp = '='; break;
        case 'gte': encodedOp = '\u2265'; break;
        case 'gt':  encodedOp = '>'; break;
        default:    encodedOp = operation; break;
        }
      }
      return encodedOp;
    } else {
      return operation;
    }
  }
});

/*
 * AngularJS filter htmlNameNormalizer
 * 
 * This filter implements the suggested name format for HTML class and ID 
 * names: 
 * .use-dashes-for-a-self-defined-multi-word-class
 * #use-dashes-for-a-self-defined-multi-word-id
 * 
 * If the input is a string, the returned string will only contain the 
 * following characters: -|a-z|0-9
 */
app.filter('htmlNameNormalizer', function() {
  var _uppercaseCharRe = /[A-Z]+/g;
  var _reExecArray;
  var _lastMatch;
  var _convertedNameArray;
  var _match;
  
  function _getNormalizedName(name) {
    _lastMatch = 0;
    _convertedNameArray = [];
    while ((_reExecArray = _uppercaseCharRe.exec(name)) !== null) {
        _convertedNameArray.push(name.substring(_lastMatch, _reExecArray.index));
        _convertedNameArray.push('-');
        _match = _reExecArray[0];
        _convertedNameArray.push(_match.toLowerCase());
        _lastMatch = _reExecArray.index + _match.length;
    }
    _convertedNameArray.push(name.substring(_lastMatch));
    return _convertedNameArray.join('')
      .trim()
      .replace(/ +-*/g, '-')
      .replace(/[^-a-z0-9]/g, '')
      .replace(/-+/g, '-')
      .replace(/^-/g, '')
	  .replace(/-$/g, '');
  }

  return function(name) {
    if (angular.isString(name)) {
      return _getNormalizedName(name);
    }
    return name;
  }
});
