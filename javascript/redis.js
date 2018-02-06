import redis from 'redis';

let client = null;
const redis_host = '192.168.99.100';
const redis_port = 32768;

function connect() {
    const connection = new Promise((res, rej) => {
        client = redis.createClient({ host: redis_host, port: redis_port});

        client.on("error", function (err) {
            return rej(err);
        });

        client.on("connect", function () {
            return res(client);
        });
    });
    return connection;
}

export function getCatergories() {
    const categories = new Promise((res, rej) => {
        client.hgetall('category', (err, obj) => {
            if (err) {
                return (rej(err));
            }
            return res(obj);
        });
    });
    return categories;
}

export default connect;