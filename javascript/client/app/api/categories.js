import openSocket from 'socket.io-client';

const url = 'http://localhost:3000/caterory';
const socket = openSocket('http://localhost:3000/');

export async function getCategories() {
    return await fetch(url)
        .then((res) => {
            return res.json();
        })
        .catch((err) => console.log(err));
}

export function subscribeToKSData(cb) {
    try {
        socket.on('ks_data', ks_data => cb(null, ks_data));
        socket.emit('subscribeToKickStarterData', 1000);
    } catch (e) {
        console.log(e);
    }
}