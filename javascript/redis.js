import redis from 'redis';

let client = null;

function connect(host, port) {
    const connection = new Promise((res, rej) => {
        client = redis.createClient({host, port});

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