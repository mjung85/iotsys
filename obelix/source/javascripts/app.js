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

app.factory('Device', function($http, $timeout) {
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

  Property.enumRanges = {
    "/enums/operationTypes": ['+','-','*','/','%']
  };

  Property.parse = function(el) {
    if (el['tagName'] == 'bool' || el['tagName'] == 'int' || el['tagName'] == 'real' || el['tagName'] == 'enum') {
      var p = new Property(el['href'], el['tagName'], el['name'], el['val'], !el['writable']);
      if (p.type == 'enum') {
        if (Property.enumRanges[el['range']]) {
          p.range = el['range'];
        } else {
          return null; // not supported for now
        }
      }
      return p;
    }
  };

  Property.prototype = {
    validValues: function() {
      if (this.type == 'enum') {
        return Property.enumRanges[this.range];
      }
    },
    serialize: function() {
      var result = {'tagName': this.type, 'href': this.href, 'val': this.value };
      return result;
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
        if (this.autofetching) {
          $timeout(this.fetch.bind(this), 1000);
        }
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

// app.directive('uiEvent', ['$parse',
//   function ($parse) {
//     return function (scope, elm, attrs) {
//       var events = scope.$eval(attrs.uiEvent);
//       angular.forEach(events, function (uiEvent, eventName) {
//         var fn = $parse(uiEvent);
//         elm.bind(eventName, function (evt) {
//           var params = Array.prototype.slice.call(arguments);
//           //Take out first paramater (event object);
//           params = params.splice(1);
//           scope.$apply(function () {
//             fn(scope, {$event: evt, $params: params});
//           });
//         });
//       });
//     };
// }]);

// app.directive('bootstrapSwitch', function() {
//     return {
//         restrict: 'A',
//         link: function(scope, element, attrs) {
//           console.log("Linked");
//           $(element).bootstrapSwitch().on('switch-change', function(el) {
//             var iel = $(this).find('input');
//             console.log("Switch changed", iel);
//             console.log("Checked",iel.prop('checked'), iel[0].checked);
//             // $(this).find('input').trigger('click'); // so angular can pick this up in the binding
//             return;
//             scope.$apply(attrs.bootstrapSwitchChange);
//           });
//         }
//     };
// });

app.controller('DevicesCtrl', ['$scope','Lobby', function($scope, Lobby) {
  Lobby.getDevices(function(devices) {
    $scope.devices = devices;
  });
}]);
