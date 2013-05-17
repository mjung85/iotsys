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

  Property.Enum = {
    ranges: {},
    range: function(href) {
      var result = this.ranges[href];
      if (!result) {
        result = [];
        this.ranges[href] = result;
        $http.get(href).success(function(response) {
          angular.forEach(response['childNodes'], function(n) { result.push(n['name']) });
        });
      }
      return result;
    }
  };

  Property.parse = function(el, device) {
    if (el['tagName'] == 'bool' || el['tagName'] == 'int' || el['tagName'] == 'real' || el['tagName'] == 'enum') {
      var p = new Property(el['href'], el['tagName'], el['name'], el['val'], !el['writable']);
      if (p.type == 'enum') {
        p.range = Property.Enum.range(el['range']);
      }
      p.device = device;
      return p;
    }
  };

  Property.prototype = {
    serialize: function() {
      var result = {'tagName': this.type, 'href': this.href, 'val': this.value };
      return result;
    },
    joinGroup: function(group) {
      var url = [this.device.url,this.href,'groupComm/joinGroup'].join('/');
      $http.post(url, '<str val="'+group.ipv6()+'"/>', {headers: {
        'Content-Type': 'application/xml'
      }}).success(function() {
        console.log(this,"joined", group.id);
      }.bind(this));
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
      var propertiesWithGroupcomm = [];
      this.properties = [];
      angular.forEach(response['childNodes'], function(c) {
          if (c['tagName'] == 'ref') {
            var names = c['name'].split(' ');
            var gcIndex = names.indexOf('groupComm')
            if (gcIndex != -1) {
              names.splice(gcIndex,1);
              propertiesWithGroupcomm.push(names[0]);
            }
          } else {
              var p = Property.parse(c, this);
              if (p) {
                this.properties.push(p);
              } else {
                // console.log("Don't know how to parse",c,"yet");
              }
          }
      }.bind(this));

      // Mark groupcomm properties
      angular.forEach(propertiesWithGroupcomm, function(name) {
        this.property(name).groupcomm = true;
      }.bind(this));
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
    },

    property: function(name) {
      for (var i=0;i<this.properties.length;i++) {
        if(this.properties[i].name == name) return this.properties[i];
      } 
    }
  };

  Device.Group = function(id) {
    this.id = id;
  };

  Device.Group.prototype = {
    ipv6: function() {
      return "FF02:FFFF::"+this.id;
    }
  }

  Device.Group.counter = 2;
  Device.Group.next = function() {
    Device.Group.counter += 1;
    return new Device.Group(Device.Group.counter);
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

app.controller('DevicesCtrl', ['$scope','Lobby','Device', function($scope, Lobby, Device) {
  $scope.selectedProperties = [];

  Lobby.getDevices(function(devices) {
    $scope.devices = devices;
  });

  $scope.selectProperty = function(p) {
    console.log("SELECT",p);
    if (!p.groupcomm) return;
    var index = $scope.selectedProperties.indexOf(p);
    if (index != -1) {
      $scope.selectedProperties.splice(index, 1);
    } else {
      $scope.selectedProperties.push(p);
    }
    p.selected = !p.selected;
  }

  $scope.createGroup = function() {
    var group = Device.Group.next();
    angular.forEach($scope.selectedProperties, function(p) {
      p.joinGroup(group);
      p.selected = false;
    });
    $scope.selectedProperties = [];
  }
}]);
