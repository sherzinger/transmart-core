//# sourceURL=workflowWarnings.js

'use strict';

window.smartRApp.directive('workflowWarnings', [
    function() {
        return {
            restrict: 'E',
            scope: {
                warnings: '='
            },
            template:
            '<div class="sr-warning-box" ng-style="{\'visibility\': visibility}">' +
            '    {{text}}' +
            '</div>',
            link: function(scope) {
                scope.$watch('warnings', function() {
                    scope.visibility = $.isEmptyObject(scope.warnings) ? 'hidden' : 'visible';
                    scope.text = '';
                    for (var warn in scope.warnings) {
                        if (scope.warnings.hasOwnProperty(warn)) {
                            scope.text += scope.warnings[warn] + '\n';
                        }
                    }
                }, true);
            }
        };
    }
]);
