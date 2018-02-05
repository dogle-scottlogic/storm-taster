import Express from 'express'
import connect, { getCatergories } from './redis';

const app = Express();
app.use("/", Express.static(__dirname + "/client/public/"));
const port = 3000;
const redis_host = '192.168.99.100';
const redis_port = 32768;

app.get("/caterory", async function (req, res) {
    connect(redis_host, redis_port)
        .then((conn, err) => {
            if (err) { console.log(err);}
            getCatergories().then((cats) => 
                 res.send(cats)
            );
        })
        .catch((err) => console.log(err));
});

app.listen(port)