//= require 'jquery-2.0.3'
//= require 'angular-1.0.7'
//= require 'html5slider'
//= require 'jquery.jsPlumb-1.5.2'
//= require 'sugar-1.4.0'
//= require_self

var app = angular.module('Obelix', []);

app.service('Lobby', function($http, Device, Directory) {
  return {
    getDeviceTree: function(cb) {
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

        cb(root);
      });
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

  Directory.prototype.make = function(components) {
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
  };

  Directory.prototype.toggle = function() {
    this.expanded = !this.expanded;
    // if (this.expanded) this.expand();
  };

  // Directory.prototype.expand = function() {
  //   this.expanded = true;
  //   if (this.subdirectories.count() == 1) {
  //     // only one subdirectory, expand it
  //     this.subdirectories[0].expand();
  //   }
  // };  

  return Directory;
});

app.factory('Device', function($http) {
  var Device = function(href, name) {
    this.href = href;
    this.name = name;
    return this;
  };

  Device.prototype.drop = function(left, top) {
    this.dropped = true;
    this.droppedLeft = left;
    this.droppedTop = top;
  };

  Device.prototype.fetch = function() {

  };

  return Device;
});

app.controller('MainCtrl', ['$scope','Lobby', function($scope, Lobby) {
  $scope.directory = null;

  Lobby.getDeviceTree(function(root) {
    $scope.directory = root;
  });

  $scope.sidebarExpanded = true;

  $scope.droppedDevices = [];
  $scope.deviceDropped = function(dev, position) {
    $scope.sidebarExpanded = false;
    dev.fetch();
    dev.drop(position.left, position.top);
    $scope.droppedDevices.push(dev);
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
      el.draggable({
        appendTo: 'body',
        helper: 'clone',
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

app.directive('droppable', function($parse) {
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
});

app.directive('plumbcontainer', function() {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      jsPlumb.ready(function() {
        jsPlumb.Defaults.Container = el;
      });
    }
  }
});

app.directive('plumb', function() {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      jsPlumb.addEndpoint(el, {isSource: true, isTarget:true, connector:[ "Bezier", { curviness:100 }], endpoint: ["Dot", {
        radius: 6
      }],paintStyle:{ fillStyle:"black"}, connectorStyle: { lineWidth: 4, strokeStyle: "#5b9ada"}});
    }
  }
});