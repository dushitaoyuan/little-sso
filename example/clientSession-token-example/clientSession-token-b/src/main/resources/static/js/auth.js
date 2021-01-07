var webCache = new WebCache();
var tokenRefreshUrl = 'http://localhost:8082/appB/refresh';


/**
 * client 保存 session
 */
function storageClientSession(sessionToken, refreshToken, expire) {
    webCache.set('sessionToken', sessionToken, expire);
    webCache.set('refreshToken', refreshToken, expire + 10);
}

function clearClientSession() {
    webCache.remove('sessionToken');
    webCache.remove('refreshToken');
}

/**
 * 每次ajax 请求携带  sessionToken
 */
$.ajaxSetup({
    beforeSend: function (callbackContext, jqXHR) {
        /**
         * add header failed abort
         * authHeader ==true add sessionToken
         */
        try {
            if (jqXHR.authHeader === false) {
                return;
            }
            if (!addSessionTokenHeader(callbackContext)) {
                jqXHR.abort();
            }
        } catch (e) {
            console.error("before send error");
        }
    }
});

function addSessionTokenHeader(xhr) {
    var sessionToken = webCache.get('sessionToken');
    if (isNotNull(sessionToken)) {
        xhr.setRequestHeader("sessionToken", sessionToken);
        return true;
    }
    var refreshToken = webCache.get('refreshToken');
    refresh();
    var sessionToken = webCache.get('sessionToken');
    if (isNotNull(sessionToken)) {
        xhr.setRequestHeader("sessionToken", sessionToken);
        return true;
    }
    return false;

}

function refresh() {
    var refreshToken = webCache.get('refreshToken');
    if (isNull(refreshToken)) {
        alert('会话过期,请重新登录');
        window.location.href = "/appB";
        return;
    }
    $.ajax({
        method: "get",
        url: "/appB/refresh",
        headers: {'refreshToken': refreshToken},
        async: false,
        dataType: 'json',
        authHeader: false,
        success: function (result) {
            if (result.code == 1) {
                var refreshResult = JSON.parse(result.data);
                storageClientSession(refreshResult.sessionToken,
                    refreshResult.refreshToken,
                    refreshResult.expire);
                return;
            }
            alert('会话过期,请重新登录');
            window.location.href = "/appB";

        },
        error: function (xhr) {
            alert('会话过期,请重新登录');
            window.location.href = "/appB";
        }
    })
}

function isNotNull(value) {
    return !isNull(value);
}

function isNull(value) {
    return value == '' || value == undefined;
}
