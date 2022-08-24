
async function deleteRequest(url, data = {}) {
    return makeRequest('DELETE', url, data);
}

async function postData(url, data = {}) {
    return makeRequest('POST', url, data);
}

async function putFormData(url, data) {
    const response = await fetch(url, {
        method: 'PUT',
        body: data
    });

    return response.json();
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

        sum += (productPrice * productQuantity);
    }

    let sumString = String(sum.toFixed(2)) + "";

    if (sumString.lastIndexOf('.') == -1) {
        sum += ".00";
    }
    else if (sumString.lastIndexOf('.') == sumString.length - 2) {
        sum += "0";
    }

    $('#total-sum').text(sumString + ' $');
}


/**
 * method for enabling button in pagination divs
 */
function initPageChangeDiv() {
    let pageChangeDiv = $('#page-change-div')[0];
    let pageChangeInput = pageChangeDiv.children[1];

    let url = new URL(location.href);
    let pageNr = parseInt(url.searchParams.get('pageNr')) || 1;
    let sortOption = parseInt(url.searchParams.get('sortOption'));
    let newUrl = url.origin + url.pathname + (!isNaN(sortOption) ? ('?sortOption=' + sortOption + '&') : '?') + 'pageNr=';
    let lastPage = parseInt(pageChangeDiv.children[2].innerText.substring(2));

    if (pageChangeInput.value != '1') {
        pageChangeDiv.children[0].onclick = function() {
            window.location.href = newUrl + (pageNr - 1);
        }

        pageChangeDiv.children[0].style.cursor = 'pointer';
    }

    pageChangeDiv.children[1].onchange = function() {
        let page = parseInt(pageChangeDiv.children[1].value);
        if (page > 0 && page <= lastPage) {
            window.location.href = newUrl + page;
        }
    }

    if (lastPage != pageNr) {
        pageChangeDiv.children[3].onclick = function() {
            window.location.href = newUrl + String(pageNr + 1);
        }
        pageChangeDiv.children[3].style.cursor = 'pointer';
    }
}