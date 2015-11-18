var fs = require('fs');
var mysql = require('mysql');
var secret = getConfig('../secret.json');

// Database setup

var connectionPool = mysql.createPool(secret.mysqlConnection);

// GET

exports.getPrivate = function(req, res) {
    var publicIP = req.params.publicIP;
    connectionPool.getConnection(function(err, connection) {
        if (err) {
            console.error('CONNECTION error: ', err);
            res.statusCode = 503;
            res.json({
                result: 'error',
                err: err.code
            });
        } else {
            connection.query('SELECT * FROM games WHERE public_ip='+publicIP, function(err, rows, fields) {
                if (err) {
                    console.error(err);
                    res.statusCode = 500;
                    res.json({
                        result: 'error',
                        err: err.code
                    })
                } else {
                    if (rows.length==1) {
                        res.json({
                            result: rows[0],
                            err: ''
                        })
                    } else if (rows.length < 1) {
                        err = 'Could not find requested IP';
                        console.error(err);
                        res.statusCode = 404;
                        res.json({
                            result: 'error',
                            err: err
                        })
                    } else {
                        //TODO: Account for this possibility
                        err = 'Found more than one game active on subnet';
                        console.error(err);
                        res.statusCode = 500;
                        res.json({
                            result: 'error',
                            err: err
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
    console.log(filepath);
    return readJsonFileSync(filepath);
}