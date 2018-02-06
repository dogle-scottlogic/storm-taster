import Express from 'express'
import connect, { getCatergories } from './redis';
import websocket, { ws } from './websocket';
import { setTimeout } from 'timers';

const app = Express();
app.use("/", Express.static(__dirname + "/client/public/"));
const port = 3000;

app.get("/caterory", async function (req, res) {
    getCatergories().then((cats) => 
            res.send(cats)
    );
});

connect()
    .then((conn, err) => {
        if (err) { console.log(err); }
        const server = websocket(app);
        server.listen(port);
    })
    .catch((err) => console.log(err));