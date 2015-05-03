angular.module('SteroidsApplication', [
  'supersonic'
])
.controller('IndexController', function($scope, supersonic) {
      supersonic.ui.screen.setAllowedRotations(["landscapeLeft", "landscapeRight"]);
});
