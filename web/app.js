var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var stylus = require('stylus');
var nib = require('nib');

var routes = require('./routes');
var api = require('./routes/api');

var app = express();

// Configuration

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.set('view options', {
    layout: false
});

// Middleware

var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');

    // intercept OPTIONS method
    if ('OPTIONS' == req.method) {
        res.send(200);
    }
    else {
        next();
    }
};

app.use(allowCrossDomain);
app.use(favicon(path.join(__dirname, 'public', 'images', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(stylus.middleware({
        src: __dirname + '/public/styl',
        dest: __dirname + '/public/css',
        compress: true,
        force: true,
        compile: function(str, path) {
            return stylus(str)
                .set('filename', path)
                .set('compress', true)
                .use(nib())
        }
    }
));
app.use(express.static(path.join(__dirname, 'public')));


// Routes

app.get('/', routes.index);
app.get('/partials/:name', routes.partials);
app.use('/lib', express.static(path.join(__dirname, 'node_modules')));

// API

app.get('/api/ip/private', api.getPrivate);
app.post('/api/ip/private/:privateIP', api.postPrivate);

// Redirect all others

app.get('*', routes.index);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;
