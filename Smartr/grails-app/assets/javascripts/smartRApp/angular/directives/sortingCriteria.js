//# sourceURL=sortingCriteria.js

'use strict';

window.smartRApp.directive('sortingCriteria', [
    function() {
        return {
            restrict: 'E',
            scope: {
                criteria : '=',
                samples: '=',
                subsets: '='
            },
            template:
            '<h2>Ranking Criteria:</h2>' +
            '<div class="heim-input-field-sub" id="sr-non-multi-subset">' +
            '    <fieldset class="heim-radiogroup" id="sr-variability-group" ng-disabled="samples < 2">' +
            '        <h3>Expression variability</h3>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"   value="coef"> Coefficient of variation' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="variance"> Variance' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="range"> Range between max and min after' +
            '            excluding outliers' +
            '        </label></div>' +
            '    </fieldset>' +
            '    <fieldset class="heim-radiogroup" id="sr-expression-level-group">' +
            '        <h3>Expression level</h3>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="mean"> Mean' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="median"> Median' +
            '        </label></div>' +
            '    </fieldset>' +
            '</div>' +
            '<div class="heim-input-field-sub" id="sr-multi-subset" >' +
            '    <fieldset class="heim-radiogroup" id="sr-differential-exp-group" ng-disabled="subsets < 2">' +
            '        <h3>Differential expression</h3>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="bval"> B-value/log odds ratio' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="pval"> p-value' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="adjpval"> Adjusted p-value (method “fdr”)' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="logfold"> log fold-change' +
            '        </label></div>' +
            '        <div><label>' +
            '            <input type="radio" ng-model="criteria"  value="ttest"> t-statistic' +
            '        </label></div>' +
            '    </fieldset>' +
            '</div>'
        };
    }
]);
