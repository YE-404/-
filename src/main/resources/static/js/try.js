const generate = require('./generate');
const fs = require('fs');
generate().then(buffer => {
    fs.writeFileSync(`/home/sy/${+new Date()}.png`, buffer);
}).catch(err => {
    console.log(err);
})