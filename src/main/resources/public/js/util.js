var state = {
    tokenInvalid: true
};
var isLocalStore = !!window.localStorage;
var localDB = {
    set: function (key, value) {
        if (isLocalStore) {
            window.localStorage.setItem(key, value);
        }
    },
    setObj: function (key, obj) {
        if (isLocalStore) {
            window.localStorage.setItem(key, JSON.stringify(obj));
        }
    },
    get: function (key) {
        if (isLocalStore) {
            return window.localStorage.getItem(key);
        }
    },
    getObj: function (key) {
        var val = localDB.get(key);
        if (val) {
            return JSON.parse(val);
        }
    },
    remove: function (key) {
        if (isLocalStore) {
            return window.localStorage.removeItem(key);
        }
    }
}
var tokenInvalid = function (resp) {
    if (!state.tokenInvalid) {
        return;
    }
    state.tokenInvalid = false;
    localDB.remove('accessToken');
    localDB.remove('account');
    openLogin(function () {

    })
}
var http = {
    post: function (url, data, succCallBack, errCallBack) {
        if (typeof data != 'string') {
            data = JSON.stringify(data)
        }
        $.ajax({
            type: 'POST',
            url: url,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            data: data,
            success: function (resp) {
                succCallBack(resp);
            },
            error: function (err) {
                if (errCallBack) {
                    errCallBack(err.responseJSON);
                }
            }
        })
    },
    get: function (url, data, succCallBack, errCallBack) {
        $.ajax({
            type: 'GET',
            url: url,
            data: data,
            dataType: 'json',
            success: function (resp) {
                if (succCallBack) {
                    succCallBack(resp);
                }
            },
            error: function (error) {
                if (errCallBack) {
                    errCallBack(error.responseJSON);
                }
            }
        })
    }
}

Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

var getUrlParameter = function (sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)), sURLVariables = sPageURL
        .split('&'), sParameterName, i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

var accountSet = function () {
    var accountJson = localDB.get('account');
    if (accountJson) {
        var account = JSON.parse(accountJson);
        var user = account.user;
        $("#account").attr("title", user.userId);
        $("#account").html(user.userId);
        $("#loginAccount").hide();
        $("#loginedAccount").show();
    } else {
        $("#loginAccount").show();
        $("#loginedAccount").hide();
    }
}

var toManager = function () {
    if (localDB.get('accessToken')) {
        window.location.href = "/html/manager"
    } else {
        openLogin(function () {
            window.location.href = "/html/manager"
        })
    }
}

var logout = function () {
    http.get("/api/oauth2-service/logout", null, function () {
        localDB.remove('accessToken');
        localDB.remove('account');
        state.tokenInvalid = true;
        accountSet();
        if (window.location.pathname.indexOf('/html/manager') != -1) {
            window.location.href = "/"
        }
    })
}

var loginDialog;
var loginCallBack;

function openLogin(cb) {
    loginDialog = dialog({
        title: '登录',
        width: 460,
        content: document.getElementById('loginDialog')
    }).showModal();
    loginCallBack = cb;
}

var linkList = [{
    name: '配置中心',
    url: '/index.html'
}, {
    name: '服务注册中心',
    url: '/zkservice/index.html'
}, {
    name: '日志查询',
    url: '/log/index.html'
}, {
    name: '项目生成',
    url: '/project/index.html'
}, {
    name: '数据生成',
    url: '/dataGen/index.html'
}, {
    name: '部署',
    url: '/deploy/index.html'
}, {
    name: '短信验证码',
    url: '/sms/index.html'
}]