var request = require('request');

var fs = require('fs');
var obj = JSON.parse(fs.readFileSync('location.json', 'utf8'));

async function doRequests(){

  let rest = await obj.map(async (item) => {
    return await request.post('http://localhost:9000/api/location',
    { json: item }).body
  })

  console.log(rest)
}

(async function () {
  await doRequests()
})()






