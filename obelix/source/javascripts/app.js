//= require 'jquery'
//= require 'jquery-ui'
//= require 'angular'
//= require 'html5slider'
////require 'jquery.jsPlumb-1.5.2'
//= require 'sugar'
//= require_self

var app = angular.module('Obelix', []);

app.service('Lobby', ['$http', 'Device', 'Directory', function($http, Device, Directory) {
  return {
    getDeviceTree: function(callback) {
      var root = new Directory('');
      root.expanded = true;
      $http.get('/obix').success(function(response) {
        response['nodes'].each(function(node) {
          var href = node['href'];
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

app.factory('Device', ['$http', 'Storage', function($http, Storage) {
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
      this.load();
    },
    load: function() {
      console.log("Loading",this.href);
    }
  };
  
  return Device;
}]);

app.controller('MainCtrl', ['$scope','Lobby', function($scope, Lobby) {
  $scope.directory = null;
  $scope.allDevices = [];

  Lobby.getDeviceTree(function(root) {
    $scope.directory = root;
    $scope.allDevices = root.globDevices();
  });

  $scope.sidebarExpanded = true;
  
  $scope.placeDevice = function(device, position) {
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
      var helper = attrs['draggableHelper'];
      if (!helper) helper = 'clone';
      el.draggable({
        appendTo: 'body',
        helper: helper,
        start: function() {
          console.log("Start",this,arguments);
          $(this).addClass('disabled');
        },
        stop: function() {
          $(this).removeClass('disabled');
        }
      });
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
          //jsPlumb.repaintEverything();
        }
      });
    }
  }
}]);

// app.directive('plumbcontainer', function() {
//   return {
//     restrict: 'A',
//     link: function(scope, el, attrs) {
//       jsPlumb.ready(function() {
//         jsPlumb.Defaults.Container = el;
//       });
//     }
//   }
// });

// app.directive('plumb', function() {
//   return {
//     restrict: 'A',
//     link: function(scope, el, attrs) {
//       jsPlumb.addEndpoint(el, {isSource: true, isTarget:true, connector:[ "Bezier", { curviness:100 }], endpoint: ["Dot", {
//         radius: 6
//       }],paintStyle:{ fillStyle:"black"}, connectorStyle: { lineWidth: 4, strokeStyle: "#5b9ada"}});
//     }
//   }
// });