var express = require('express');
var router = express.Router();
const models = require('../models');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/login', (req, res, next) => {
  res.send('login api');
});

router.post('/logout', (req, res, next) => {
  res.send('logout api');
});

module.exports = router;
