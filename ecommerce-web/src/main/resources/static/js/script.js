
async function deleteRequest(url, data = {}) {
    return makeRequest('DELETE', url, data);
}

async function postData(url, data = {}) {
    return makeRequest('POST', url, data);
}

async function makeRequest(method, url, data) {
    const response = await fetch(url, {
        method: method,
        body: JSON.stringify(data)
    });

    return response.json();
}
