
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
const updateCartQuantity = function() {
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


const calculateTotalSum = function() {
    let products = $('.product-in-cart');
    let sum = 0;

    for (let product of products) {
        let productPrice = parseFloat(product.getElementsByClassName('product-in-cart-price')[0].innerText).toFixed(2);
        // ||, to make it work both with cart and checkout
        let productQuantity = product.getElementsByClassName('product-in-cart-quantity')[0].value ||
            parseInt(product.getElementsByClassName('product-in-cart-quantity')[0].innerText);

        if (productPrice < 0 || productQuantity < 1) {
            sum = '???';
            break;
        }

        sum += productPrice * productQuantity;
    }

    let sumString = sum + "";
    if (sumString.lastIndexOf('.') == -1) {
        sum += ".00";
    }
    else if (sumString.lastIndexOf('.') == sumString.length - 2) {
        sum += "0";
    }

    $('#total-sum').text(sum + ' $');
}