//# sourceURL=workflowControls.js

'use strict';

window.smartRApp.directive('workflowControls', [
    'smartRUtils',
    function(smartRUtils) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: '<div class="sr-workflow-controls" ng-transclude></div>',
            link: function(scope, element) {
                var controls = element.children()[0];
                var scrollbarWidth = smartRUtils.getScrollBarWidth();
                controls.style.bottom = scrollbarWidth + 'px';
                controls.style.right = scrollbarWidth + 105 + 'px';
            }
        };
    }
]);
