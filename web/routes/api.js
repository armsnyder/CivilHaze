/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version. Any redistribution must give proper attribution to the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

'use strict';

var fs = require('fs');
var mysql = require('mysql');
var ip = require('ip');
var secret = getConfig('../secret.json');

// Database setup

var connectionPool = mysql.createPool(secret.mysqlConnection);

// GET

exports.getPrivate = function(req, res) {
    var publicIP = getIP(req);
    connectionPool.getConnection(function(err, connection) {
        if (err) {
            console.error('CONNECTION error: ', err);
            res.statusCode = 503;
            res.json({
                result: 'error',
                error: err.code
            });
        } else {
            connection.query("SELECT * FROM games WHERE public_ip='"+publicIP+"' ORDER BY last_updated DESC LIMIT 1",
                function(err, rows) {
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
                            res.statusCode = 409;
                            res.json({
                                result: 'error',
                                error: err
                            })
                        } else {
                            //TODO: Account for multiple valid games on a single subnet
                            res.json({
                                result: rows[0]['private_ip'],
                                error: ''
                            })
                        }
                    }
                    connection.release();
                });
        }
    });
};

// POST

exports.postPrivate = function(req, res) {
    var publicIP = getIP(req);
    var privateIP = req.params['privateIP'];
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
                connection.query("INSERT INTO games (public_ip, private_ip) VALUES ('"+publicIP+"', '"+privateIP+"')",
                    function(err) {
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
};

// Utility functions

function readJsonFileSync(filepath, encoding) {

    if (typeof (encoding) == 'undefined'){
        encoding = 'utf8';
    }
    var file = fs.readFileSync(filepath, encoding);
    return JSON.parse(file);
}

function getConfig(file) {

    var filepath = __dirname + '/' + file;
    return readJsonFileSync(filepath);
}

function getIP(req) {
    var ips_str = req.headers['x-forwarded-for'];
    if (ips_str) {
        return ips_str.split(',')[0].trim();
    } else {
        //return req.connection.remoteAddress;
        return 'default';
    }
}