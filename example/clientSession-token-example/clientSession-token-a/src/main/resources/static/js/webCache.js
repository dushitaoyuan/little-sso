var CacheItem = function (value, expireSeconds) {
    var now = new Date().getTime();
    this.createTime = now;
    this.endTime = now + expireSeconds * 1000;
    this.value = value;
}

var WebCache = function (storageType) {
    if (storageType == undefined) {
        this.storageType = 'localStorage';
    }
}

WebCache.prototype.remove = function (cacheKey) {
    if (this.storageType == 'localStorage') {
        localStorage.removeItem(cacheKey);
    } else if (this.storageType == 'sessionStorage') {
        sessionStorage.removeItem(cacheKey);
    }
}

WebCache.prototype.storageGet = function (cacheKey) {
    if (this.storageType == 'localStorage') {
        return localStorage.getItem(cacheKey);
    } else if (this.storageType == 'sessionStorage') {
        return sessionStorage.getItem(cacheKey);
    }
}
WebCache.prototype.storageSet = function (cacheKey, cacheValue) {
    if (cacheKey == '' || cacheKey == undefined || cacheValue == '' || cacheValue == undefined) {
        return;
    }
    if (this.storageType == 'localStorage') {
        localStorage.setItem(cacheKey, cacheValue);
    } else if (this.storageType == 'sessionStorage') {
        sessionStorage.setItem(cacheKey, cacheValue);
    }
}
WebCache.prototype.get = function (cacheKey) {
    var cacheItem = this.getCacheItem(cacheKey);
    if (cacheItem === '') {
        return cacheItem;
    }
    return cacheItem.value;

}

WebCache.prototype.getCacheItem = function (cacheKey) {
    var value = this.storageGet(cacheKey);
    try {
        if (value == '' || value == undefined) {
            return '';
        }
        var cacheItem = JSON.parse(value);
        var now = new Date().getTime();
        if (cacheItem.endTime > now) {
            return cacheItem;
        }
        this.remove(cacheKey);
        return '';
    } catch (e) {
        console.error("cache error" + e);
        return '';
    }
}

WebCache.prototype.set = function (cacheKey, cacheValue, expireSeconds) {
    if (cacheKey == '' || cacheKey == undefined ||
        cacheValue == '' || cacheValue == undefined ||
        expireSeconds == undefined) {
        return;
    }
    var cacheItem = new CacheItem(cacheValue, expireSeconds);
    this.storageSet(cacheKey, JSON.stringify(cacheItem));
}


WebCache.prototype.getExpire = function (cacheKey) {
    var cacheItem = this.getCacheItem(cacheKey);
    if (cacheItem === '') {
        return 0;
    }
    var now = new Date().getTime();
    var expireSeconds = (cacheItem.endTime - now) / 1000;
    if (expireSeconds <= 0) {
        return 0;
    }
    return parseInt(expireSeconds,10);
}