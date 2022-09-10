
async function deleteRequest(url, data = {}) {
    return makeRequest('DELETE', url, data);
}

async function postData(url, data = {}) {
    return makeRequest('POST', url, data);
}

async function putFormData(url, data) {
    return makeRequest('PUT', url, data);
}

async function requestAndToastForm(method, url, date) {
    return requestAndToast(method, url, date, makeRequestForm);
}

async function requestAndToast(method, url, data = {}, formRequest) {
    let status;
    let result;
    let message;

    let makeRequestFunction = formRequest || makeRequest;

    await makeRequestFunction(method, url, data)
        .then( response => {
            status = response.status;
            return response.json();
        })
        .then( body => {
            if (status == 200) {
                showToastFromResponse(status, body.response);
                result = true;
            }
            else {
                showToastFromResponse(status, body.message);
                result = false;
                message = body.message;
            }
        });

    return {
        success: result,
        message: message
    };
}

async function getData(url) {
    const response = await fetch(url);

    return response.json();
}

async function makeRequestForm(method, url, data) {
    const response = await fetch(url, {
        method: method,
        body: data
    });

    return response;
}

async function makeRequest(method, url, data) {
    const response = await fetch(url, {
        method: method,
        body: JSON.stringify(data)
    });

    return response;
}



/**
 *  Methods for fragments/layout.html
 */
function updateCartQuantity() {
    let cartBadge = $('#cart-badge');
    let url = location.origin + '/cart/size';

    getData(url)
        .then( res => {
            if (res.status != null && res.status !== 200) {
                cartBadge.text(0);
                return;
            }

            cartBadge.text(res.response);
        } )
}

let toastTimeout;
function showToast(text, type) {
    function close(){
        content.parentElement.hidden = true;
    }

    let content = $('#toast div')[0];
    content.className = `toast-body alert alert-${type} m-0 p-3`;
    content.innerText = text;

    clearTimeout(toastTimeout);
    toastTimeout = setTimeout(close, 3500);
    content.parentElement.hidden = false;
}

function showToastFromResponse(status, body) {
    let type = status == 200 ? 'success' : 'danger';
    showToast(body, type);
}



/**
 *  Method for cart & checkout
 */
function calculateTotalSum() {
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
        sumString += ".00";
    }
    else if (sumString.lastIndexOf('.') == sumString.length - 2) {
        sumString += "0";
    }

    $('#total-sum').text(sumString + ' $');
}



/**
 * method for enabling button in pagination divs
 */
function initPageChangeDiv() {
    let pageChangeDiv = $('#page-change-div')[0];
    if (pageChangeDiv == null) {
        return;
    }

    let pageChangeInput = pageChangeDiv.children[1];

    let url = new URL(location.href);

    let pageNr = parseInt(url.searchParams.get('pageNr')) || 1;

    let lastPage = parseInt(pageChangeDiv.children[2].innerText.substring(2));

    if (pageChangeInput.value != '1') {
        pageChangeDiv.children[0].onclick = function() {
            url.searchParams.set("pageNr", pageNr - 1)
            window.location.href = url.href;
        }

        pageChangeDiv.children[0].style.cursor = 'pointer';
    }

    pageChangeDiv.children[1].onchange = function() {
        let page = parseInt(pageChangeDiv.children[1].value);
        if (page >= 1 && page <= lastPage) {
            url.searchParams.set("pageNr", page)
            window.location.href = url.href;
        }
    }

    if (lastPage != pageNr) {
        pageChangeDiv.children[3].onclick = function() {
            url.searchParams.set("pageNr", pageNr + 1)
            window.location.href = url.href;
        }
        pageChangeDiv.children[3].style.cursor = 'pointer';
    }
}