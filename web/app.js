/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var stylus = require('stylus');
var nib = require('nib');
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
        src: path.join(__dirname, 'public', 'styl'),
        dest: path.join(__dirname, 'public', 'css'),
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

// Routes

app.use(express.static(path.join(__dirname, 'public')));

app.use('/lib', express.static(path.join(__dirname, 'node_modules')));

// API

app.get('/api/ip/private', function(req, res) {
    var publicIP = ip.toLong(getIP(req));
    connectionPool.getConnection(function(err, connection) {
        if (err) {
            console.error('CONNECTION error: ', err);
            res.statusCode = 503;
            res.json({
                result: 'error',
                error: err.code
            });
        } else {
            connection.query("SELECT * FROM games WHERE public_ip_min<"+publicIP+" AND public_ip_max>"+publicIP+
                " ORDER BY last_updated DESC LIMIT 1", function(err, rows) {
                if (err) {
                    console.error(err);
                    res.statusCode = 500;
                    res.json({
                        result: 'error',
                        error: err.code
                    })
                } else {
                    if (rows.length == 0) {
                        err = 'Could not find requested IP';
                        console.error(err);
                        res.statusCode = 409;
                        res.json({
                            result: 'error',
                            error: err
                        })
                    } else {
                        //TODO: Account for multiple valid games on a single subnet
                        res.json({
                            result: ip.fromLong(rows[0]['private_ip']),
                            error: ''
                        })
                    }
                }
                connection.release();
            });
        }
    });
});

app.post('/api/ip/private/:privateIP', function(req, res) {
    var publicIP = getIP(req);
    var mask = req.body.hasOwnProperty('mask') ? ip.fromPrefixLen(req.body.mask) : "255.255.255.0";
    var subnetInfo = ip.subnet(publicIP, mask);
    var minIP = ip.toLong(subnetInfo.firstAddress);
    var maxIP = ip.toLong(subnetInfo.lastAddress);
    var privateIP = ip.toLong(req.params['privateIP']);
    if (privateIP) {
        connectionPool.getConnection(function(err, connection) {
            if (err) {
                console.error('CONNECTION error: ', err);
                res.statusCode = 503;
                res.json({
                    result: 'error',
                    error: err.code
                });
            } else {
                connection.execute("INSERT INTO games (public_ip_min, public_ip_max, private_ip) VALUES (?, ?, ?)",
                    [minIP, maxIP, privateIP], function(err) {
                        if (err) {
                            console.error(err);
                            res.statusCode = 500;
                            res.json({
                                result: 'error',
                                error: err.code
                            })
                        } else {
                            res.json({
                                result: 'success',
                                error: ''
                            })
                        }
                        connection.release();
                    });
            }
        });
    } else {
        res.statusCode = 404;
        res.json({
            result: 'error',
            error: 'Invalid request'
        })
    }
});

// App partials

app.get('/partials/:name', function(req, res) {
    var name = req.params.name;
    res.render(path.join('partials', name));
});

// Redirect all others to main app

app.get('*', function(req, res) {
    res.render('index');
});

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

// Start server

if (!module.parent) {
    app.listen(80, function() {
        console.log('Civil Haze backend server listening on port 80.');
    });
}

module.exports = app;
