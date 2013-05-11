//= require 'angular'
//= require 'jquery-1.9.1'

//= require_self

var app = angular.module('Obelix', []);

app.service('Lobby', function($http, Device) {
  return {
    getDevices: function(cb) {
      $http.get('/obix').success(function(response) {
        cb($.map(response['childNodes'], function(ref) { return new Device(ref['href']); }));
      });
    }
  }
});

app.factory('Device', function($http) {
  var Device = function(url) {
    this.url = url;
    this.name = url.replace('/','');
    this.bools = [];
    this.fetch();
  };

  Device.prototype.fetch = function() {
    this.fetching = true;
    var d = this;
    var bools = [];
    $http.get(this.url).success(function(response) {
      angular.forEach(response['childNodes'], function(c) {
        if (c['tagName'] == 'bool') {
          bools.push({
            name: c['name'], 
            value: c['val'] ? "true" : "false", 
            readonly: !c['writable'],
            device: d,
            save: function() {
              var url = d.url + '/' + this.name;
              $http.put(url, {
                tagName: 'bool',
                val: this.value
              });
            }
          });
        }
      });
      d.bools = bools;
      d.fetching = false;
    });
  };
  return Device;
});

app.controller('DevicesCtrl', ['$scope','Lobby', function($scope, Lobby) {
  Lobby.getDevices(function(devices) {
    $scope.devices = devices;
  });
}]);
