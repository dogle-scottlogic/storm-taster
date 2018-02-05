const url = 'http://localhost:3000/caterory'

export async function getCategories() {
    return await fetch(url)
        .then((res) => {
            return res.json();
        })
        .catch((err) => console.log(err));
}