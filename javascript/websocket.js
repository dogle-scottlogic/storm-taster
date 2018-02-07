import io from 'socket.io';
import { createServer } from 'http';
import connect, { getKSData } from './redis';

let socket = null;

const init = function (app) {
    var server = createServer(app);
    io(server).on('connection', (client) => {
        socket = client;
        socket.on('subscribeToKickStarterData', (interval) => {
            console.log('client is subscribing to Kick Starter Data with interval ', interval);
            setInterval(() => {
                Promise.all(getKSData()).then((data) =>
                    client.emit('ks_data', data)
                );
            }, interval);
        });
    });
    return server;
};

export const ws = () => socket;

export default init;