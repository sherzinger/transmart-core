//# sourceURL=tabContainer.js

'use strict';

window.smartRApp.directive('tabContainer',
    ['smartRUtils', '$timeout', function(smartRUtils, $timeout) {
        return {
            restrict: 'E',
            transclude: true,
            template:
            '<div id="heim-tabs" style="margin-top: 25px;">' +
            '    <ul>' +
            '        <li class="heim-tab" ng-repeat="tab in tabs">' +
            '            <a href="#{{tab.id}}"' +
            '               ng-style="{\'color\': tab.disabled ? \'grey\' : \'black\', \'pointer-events\': tab.disabled ? \'none\' : null}">' +
                '                {{tab.name}}' +
            '            </a>' +
            '        </li>' +
            '    </ul>' +
            '    <ng-transclude-replace></ng-transclude-replace>' +
            '</div>',
            controller: function($scope) {
                $scope.tabs = [];
                this.addTab = function(tab) {
                    $scope.tabs.push(tab);
                };
            },
            link: function() {
                $timeout(function() { // init jQuery UI tabs after DOM has rendered
                    $('#heim-tabs').tabs();
                });
            }
        };
    }]);
