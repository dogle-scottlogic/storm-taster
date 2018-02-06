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

export function subscribeToCategories(cb) {
    try {
        socket.on('categories', cats => cb(null, cats));
        socket.emit('subscribeToCategories', 1000);
    } catch (e) {
        console.log(e);
    }
}