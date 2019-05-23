const express = require('express');
const router = express.Router();
const models = require('../models/');

/* GET users listing. */
router.get('/', async function(req, res, next) {
  let users = await models.user.findAll();
  console.log(users);
  res.send('respond with a resource');
});

router.post('/', async (req, res, next) => {
  //models.user.create();
  let data = {
    username: req.body.username,
    password: req.body.password,
    nickname: req.body.nickname,
    address: req.body.address,
    score: 0,
    interest: req.body.interest
  };
  try {
    let query_res = await models.user.create(data);
    console.log(query_res);
    res.send(query_res);
  } catch (err) {
    res.status(500).send({msg: 'DB Validation on user query.'});
  }  
  
});


module.exports = router;
