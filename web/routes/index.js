var express = require('express');
var router = express.Router();
var MobileDetect = require('mobile-detect');

/* GET home page. */
router.get('/', function(req, res, next) {
    var md = new MobileDetect(req.headers['user-agent']);
    if (md.mobile()) {
        res.render('mobileIndex', { title: 'Controller' });
    } else {
        res.render('desktopIndex', { title: 'Come Again' });
    }
});

router.get('/api/gameID');

module.exports = router;
