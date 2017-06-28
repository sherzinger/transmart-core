//= require_self

//= require resource/jquery-2.1.4.min.js
//= require resource/jquery-ui-1.11.4.min.js
//= require resource/d3.min.js
//= require resource/d3-tip.js
//= require resource/crossfilter.js
//= require resource/plotly-latest.min.js
//= require resource/jsrender.js
//= require resource/angular.js
//= require resource/angular-route.js
//= require resource/angular-css.js

//= require smartRApp/angular/smartRApp.js
//= require_tree /templates
//= require_tree smartRApp
//# sourceURL=smartR.js

// Avoid `console` errors in browsers that lack a console.
(function() {
    var method;
    var noop = function () {};
    var methods = [
        'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
        'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
        'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
        'timeline', 'timelineEnd', 'timeStamp', 'trace', 'warn'
    ];
    var length = methods.length;
    console = (window.console = window.console || {}); // jshint ignore:line

    while (length--) {
        method = methods[length];

        // Only stub undefined methods.
        if (!console[method]) {
            console[method] = noop;
        }
    }
}());

window.addSmartRPanel = function addSmartRPanel(parentPanel) {
    parentPanel.insert(4, window.smartRPanel);
};

function cleanUpSmartR() {
    var d3tips = document.getElementsByClassName('d3-tip');
    Array.prototype.forEach.call(d3tips, function(el) { // d3tips is array-like object
        el.parentNode.removeChild(el);
    });
}
cleanUpSmartR();

