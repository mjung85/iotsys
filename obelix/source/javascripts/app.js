//= require 'sugar-1.4.0'
//= require 'jquery-2.0.3'
//= require 'angular-1.0.7'
//= require 'html5slider'
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
    this.name = name.replace('+',' ');
    this.subdirectories = [];
    this.devices = [];
    this.expanded = false;
    return this;
  };

  Directory.prototype.make = function(components) {
    if (components.isEmpty()) return this;

    var head = components.shift();
    
    var subdirectory = this.subdirectories.find({name:head});
    if (!subdirectory) {
      subdirectory = new Directory(head);
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
  return Device;
});

app.controller('MainCtrl', ['$scope','Lobby', function($scope, Lobby) {
  $scope.directory = null;

  Lobby.getDeviceTree(function(root) {
    $scope.directory = root;
  });

  $scope.sidebarExpanded = true;
}]);

app.directive('draggable', function() {
  return {
    restrict: 'A',
    link: function(scope, el, attrs) {
      var device = scope.$eval("device");
      el.draggable({
        revert: true,
        appendTo: 'body',
        helper: 'clone',
        start: function() {
          $(this).addClass('disabled');
        },
        stop: function() {
          $(this).removeClass('disabled');
        }
        // helper: function() {
        //   return $('<div class="dragger"></div>');
        // }
      });
    }
  };
});
