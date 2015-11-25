
/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

/* GET home page. */
exports.index = function(req, res) {
    res.render('index');
};

exports.partials = function(req, res) {
    var name = req.params.name;
    res.render('partials/' + name);
};
