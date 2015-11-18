var express = require('express');
var router = express.Router();
var MobileDetect = require('mobile-detect');
var fs = require('fs'), json;
var secret = JSON.parse(getConfig('../secret.json'));

/* GET home page. */
router.get('/', function(req, res, next) {
    var md = new MobileDetect(req.headers['user-agent']);
    if (md.mobile()) {
        res.render('mobileIndex', { title: 'Controller' });
    } else {
        res.render('desktopIndex', { title: secret.database.foo });
    }
});

router.get('/api/gameID');

module.exports = router;

// Utility functions:
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
