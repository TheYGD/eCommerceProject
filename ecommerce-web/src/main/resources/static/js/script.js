
async function deleteRequest(url, data = {}) {
    return makeRequest('DELETE', url, data);
}

async function postData(url, data = {}) {
    return makeRequest('POST', url, data);
}

async function getData(url) {
    const response = await fetch(url);

    return response.json();
}

async function makeRequest(method, url, data) {
    const response = await fetch(url, {
        method: method,
        body: JSON.stringify(data)
    });

    console.log(document.cookie);

    return response.json();
}

/**
 *  Methods for fragments/layout.html
 */
const updateCartAmount = function() {
    let cartBadge = $('#cart-badge');
    let urlWithAdditionalSlash = $('#nav-search-form').attr('action');
    let url = urlWithAdditionalSlash.substring(0, urlWithAdditionalSlash.lastIndexOf('/')) + '/cart/size';

    getData(url)
        .then( res => {
            if (res.status != null && res.status !== 200) {
                cartBadge.text(0);
                return;
            }

            cartBadge.text(res.response);
        } )
}
