import io from 'socket.io';
import { createServer } from 'http';
import connect, { getCatergories } from './redis';

let socket = null;

const init = function (app) {
    var server = createServer(app);
    io(server).on('connection', (client) => {
        socket = client;
        socket.on('subscribeToCategories', (interval) => {
            console.log('client is subscribing to categories with interval ', interval);
            setInterval(() => {
                console.log("calling db");
                getCatergories().then((cats) =>
                    client.emit('categories', cats)
                );
            }, interval);
        });
    });
    return server;
};

export const ws = () => socket;

export default init;