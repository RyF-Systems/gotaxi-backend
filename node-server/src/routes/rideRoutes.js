const express = require('express');
const router = express.Router();
const rideController = require('../controllers/rideController');

router.get('/rider/:riderId', rideController.getRiderHistory);

module.exports = router;
