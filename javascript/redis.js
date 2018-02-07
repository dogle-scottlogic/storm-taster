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

export function getKSData() {
    const ks_data = [new Promise((res, rej) => {
        client.hgetall('category', (err, obj) => {
            if (err) {
                return (rej(err));
            }
            return res(obj);
        });
    }), new Promise((res, rej) => {
            client.hgetall('backers', (err, obj) => {
                if (err) {
                    return (rej(err));
                }
                return res(obj);
            });    
    })];
    return ks_data;
}

export default connect;