const express = require('express');
const router = express.Router();
const models = require('../models/');

/* GET users listing. */
router.get('/', async function(req, res, next) {
  let users = await models.user.findAll();
  console.log(users);
  res.send('respond with a resource');
});

router.post('/', function(req, res, next) {

});

module.exports = router;
