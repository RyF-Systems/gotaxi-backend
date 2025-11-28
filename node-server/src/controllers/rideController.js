const rides = require('../data/rides');

const getRiderHistory = (req, res) => {
    const { riderId } = req.params;
    const riderRides = rides.filter(r => r.riderId == riderId);
    res.status(200).json(riderRides);
};

module.exports = {
    getRiderHistory
};
