//= require 'jquery-1.9.1'
//= require 'angular'
//= require 'bootstrapSwitch'
//= require_self

var app = angular.module('Obelix', []);

app.service('Lobby', function($http, Device) {
  IGNORED_DEVICES = ['/about', '/watchService', '/enums']; // TODO

  return {
    getDevices: function(cb) {
      $http.get('/obix').success(function(response) {
        cb($.map(response['childNodes'], function(ref) { return new Device(ref['href']); }));
      });
    }
  }
});

app.factory('Device', function($http) {
  var Property = function(href, type, name, value, readonly) {
    this.href = href;
    this.type = type;
    this.numeric = (this.type == 'int' || this.type == 'real');
    this.name = name;
    this.value = value;
    this.readonly = readonly;

    if (this.type == 'real') {
      this.value = Math.round(this.value * 100) / 100.0;
    }
  };

  Property.parse = function(el) {
    if (el['tagName'] == 'bool' || el['tagName'] == 'int' || el['tagName'] == 'real') {
      return new Property(el['href'], el['tagName'], el['name'], el['val'], !el['writable']);
    }
  };

  Property.prototype = {
    serialize: function() {
      return {'tagName': this.type, 'href': this.href, 'val': this.value };
    }
  };

  var Device = function(url) {
    this.url = url;
    this.name = url.replace('/','');
    this.properties = [];
    this.fetch();
    return this;
  };

  Device.prototype = {
    load: function(response) {
      var d = this;
      this.properties = [];
      angular.forEach(response['childNodes'], function(c) {
          var p = Property.parse(c);
          if (p) {
            d.properties.push(p);
          } else {
            // console.log("Don't know how to parse",c,"yet");
          }
      });
    },

    fetch: function() {
      this.fetching = true;
      $http.get(this.url).success(function(response) {
        this.load(response);
        this.fetching = false;
      }.bind(this));
    },

    update: function(property) {
      $http.put(this.url, property.serialize()).success(function(response) {
        console.log(response);
        this.load(response);
      }.bind(this));
    }
  };

  return Device;
});

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

app.directive('bootstrapSwitch', function() {
    return {
        // Restrict it to be an attribute in this case
        restrict: 'A',
        // responsible for registering DOM listeners as well as updating the DOM
        link: function(scope, element, attrs) {
            $(element).bootstrapSwitch();
              
              // X = data.el; 
              // $(data.el).trigger('change');
              // $(data.el).val(data.value);
            // });
        }
    };
});

app.controller('DevicesCtrl', ['$scope','Lobby', function($scope, Lobby) {
  Lobby.getDevices(function(devices) {
    $scope.devices = devices;
  });
}]);
