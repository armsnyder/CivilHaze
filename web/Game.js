var mongoose = require('mongoose');

var gameSchema = new mongoose.Schema({
    public_ip_min: {type: Number, required: true},
    public_ip_max: {type: Number, required: true},
    private_ip: {type: Number, required: true},
    last_updated: Date
});

gameSchema.pre('save', function(next) {
    this.last_updated = new Date();
    next();
});

var Game = mongoose.model('Game', gameSchema);

module.exports = Game;