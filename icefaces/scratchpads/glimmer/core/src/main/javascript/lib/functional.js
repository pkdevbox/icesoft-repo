function apply(fun, arguments) {
    return fun.apply(fun, arguments);
}

function withArguments() {
    var args = arguments;
    return function(fun) {
        apply(fun, args);
    };
}

function let(definition) {
    return function() {
        return apply(definition, arguments);
    };
}

function curry() {
    var args = arguments;
    return function() {
        var curriedArguments = [];
        var fun = args[0];
        for (var i = 1; i < args.length; i++) curriedArguments.push(args[i]);
        for (var j = 0; j < arguments.length; j++) curriedArguments.push(arguments[j]);
        return apply(fun, curriedArguments);
    };
}

function $witch(tests, defaultRun) {
    return function(val) {
        var args = arguments;
        var conditions = [];
        var runs = [];
        tests(function(condition, run) {
            conditions.push(condition);
            runs.push(run);
        });
        var size = conditions.length;
        for (var i = 0; i < size; i++) {
            if (apply(conditions[i], args)) {
                return apply(runs[i], args);
            }
        }
        if (defaultRun) apply(defaultRun, args);
    };
}

function identity(arg) {
    return arg;
}

function negate(b) {
    return !b;
}

function greater(a, b) {
    return a > b;
}

function less(a, b) {
    return a < b;
}

function not(a) {
    return !a;
}

function multiply(a, b) {
    return a * b;
}

function plus(a, b) {
    return a + b;
}

function max(a, b) {
    return a > b ? a : b;
}

function increment(value, step) {
    return value + (step ? step : 1);
}

function decrement(value, step) {
    return value - (step ? step : 1);
}

function any() {
    return true;
}

function none() {
    return false;
}

function noop() {
}