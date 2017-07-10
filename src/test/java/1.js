(function () {
    var html = "";
    if (document.getElementsByClassName('image')[0]) {
        html = document.getElementsByClassName('image')[0].innerHTML + ' ' + document.getElementsByClassName('text')[0].innerHTML;
    } else {
        html = document.getElementsByClassName('text')[0].innerHTML;
    }
    html = html.replace(/<li>/g, '<p>');
    html = html.replace(/<\/li>/g, '</p>');
    html = html.replace(/<div>/g, '<p>');
    html = html.replace(/<\/div>/g, '</p>');
    html = html.replace(/&nbsp;/g, '');
    html = html.replace(/ /g, '　');
    return html;
})()


(function () {
    if (document.getElementsByClassName('laiyuan') && document.getElementsByClassName('laiyuan')[0].getElementsByTagName('font')) {

    }
    document.getElementsByClassName('laiyuan')[0].getElementsByTagName('font')[1].textContent
})()

(function () {
    var ele = document.getElementById('news_more_page_div_id').getElementsByTagName('a')[1];
    var parEle = ele.previousSibling;
    while (parEle && parEle.nodeType !== 1) {
        parEle = parEle.previousSibling;
    }
    return parEle.innerHTML.slice(1, 2)
})()

(function () {
    var strCon = document.getElementById('cont_1_1_2').getElementsByTagName('img');
    for (var i = 0; i < strCon.length; i++) {
        var img = strCon[i].src;
        strCon[i].src = img;
        strCon[i].alt = "";
        strCon[i].title = "";
    }
    if (document.getElementsByClassName('left_ph') [0]) {
        if (document.getElementsByClassName('left_pt') [0]) {
            var a1 = document.getElementsByClassName('left_zw') [0].getElementsByTagName('table') [0].innerHTML;
            var a2 = document.getElementById('tupian_div').innerHTML.replace(/<!--Yc94EUEtAn4YSUKCaSOM --><br><div style.*?>/, '').replace('<!--视频搜索s--><!--视频搜索e-->', '').replace('<!--Yc94EUEtAn4YSUKCaSOM -->', '').replace(/&nbsp;/g, '').replace(/&amp;/g, '') + ' ' + document.getElementsByClassName('left_ph') [0].innerHTML + ' ' + document.getElementsByClassName('left_pt') [0].innerHTML + ' ' + document.getElementsByClassName('left_zw') [0].innerHTML.replace(a1, '');
        } else {
            var a1 = document.getElementsByClassName('left_zw') [0].getElementsByTagName('table') [0].innerHTML;
            var a2 = document.getElementsByClassName('left_ph') [0].innerHTML + ' ' + document.getElementsByClassName('left_zw') [0].innerHTML.replace(a1, '');
        }
    } else {
        var a1 = document.getElementsByClassName('left_zw') [0].getElementsByTagName('table') [0].innerHTML;
        var a2 = document.getElementById('tupian_div').innerHTML.replace(/<!--Yc94EUEtAn4YSUKCaSOM --><br><div style.*?>/, '').replace('<!--视频搜索s--><!--视频搜索e-->', '').replace('<!--Yc94EUEtAn4YSUKCaSOM -->', '').replace(/&nbsp;/g, '').replace(/&amp;/g, '') + ' ' + document.getElementsByClassName('left_zw') [0].innerHTML.replace(a1, '');
    }
    return a2;
})()


(function () {
    var jquery = document.createElement('script');
    jquery.type = 'text/javascript';
    jquery.src = "http://libs.baidu.com/jquery/1.11.1/jquery.min.js";
    document.getElementsByTagName('head')[0].appendChild(jquery);
    var body = '';
    for (var i = 0; i <= 2; i = i + 1) {
        var url = 't20170321_9329472(_${index}).htm';
        if (i == 0) {
            url = url.replace(/\(.*\)/, '');
        } else {
            url = url.replace(/\(/, '').replace(/\)/, '').replace('${index}', i);
        }
        for (var j = 0; j <= 5; j = j + 1) {
            var success = false;
            $.ajax({
                url: url,
                async: false,
                success: function (data) {
                    var tagTpl = document.createElement('div');
                    tagTpl.innerHTML = data;
                    var content = (function () {
                        var strCon = tagTpl.getElementsByClassName('TRS_Editor') [0].getElementsByTagName('img');
                        for (var i = 0; i < strCon.length; i++) {
                            var img = strCon[i].src;
                            strCon[i].src = img;
                        }
                        var a1 = tagTpl.getElementsByClassName('TRS_Editor') [0].getElementsByTagName('p');
                        var a2 = '';
                        for (var i = 0; i < a1.length; i++) {
                            a2 += a1[i].outerHTML.replace(/&nbsp;/g, '').replace(/&amp;/g, '');
                        }
                        return a2;
                    })();
                    body += content;
                    success = true;
                }
            });
            if (success) {
                break;
            }
        }
    }
    return body;
}())


(function () {
    var strCon = document.getElementById('cont_1_1_2').getElementsByTagName('img');
    for (var i = 0; i < strCon.length; i++) {
        var img = strCon[i].src;
        strCon[i].src = img;
        strCon[i].alt = "";
        strCon[i].title = "";
    }
    if (document.getElementsByClassName('left_ph') [0]) {
        if (document.getElementsByClassName('left_pt') [0]) {
            var a1 = document.getElementsByClassName('left_zw') [0].getElementsByTagName('table') [0].innerHTML;
            var a2 = document.getElementById('tupian_div').innerHTML.replace(/<!--Yc94EUEtAn4YSUKCaSOM --><br><div style.*?>/, '').replace('<!--视频搜索s--><!--视频搜索e-->', '').replace('<!--Yc94EUEtAn4YSUKCaSOM -->', '').replace(/&nbsp;/g, '').replace(/&amp;/g, '') + ' ' + document.getElementsByClassName('left_ph') [0].innerHTML + ' ' + document.getElementsByClassName('left_pt') [0].innerHTML + ' ' + document.getElementsByClassName('left_zw') [0].innerHTML.replace(a1, '');
        } else {
            var a1 = document.getElementsByClassName('left_zw') [0].getElementsByTagName('table') [0].innerHTML;
            var a2 = document.getElementsByClassName('left_ph') [0].innerHTML + ' ' + document.getElementsByClassName('left_zw') [0].innerHTML.replace(a1, '');
        }
    } else {
        var a1 = document.getElementsByClassName('left_zw') [0].getElementsByTagName('table') [0].innerHTML;
        var a2 = document.getElementById('tupian_div').innerHTML.replace(/<!--Yc94EUEtAn4YSUKCaSOM --><br><div style.*?>/, '').replace('<!--视频搜索s--><!--视频搜索e-->', '').replace('<!--Yc94EUEtAn4YSUKCaSOM -->', '').replace(/&nbsp;/g, '').replace(/&amp;/g, '') + ' ' + document.getElementsByClassName('left_zw') [0].innerHTML.replace(a1, '');
    }
    return a2;
})()


(function () {
    var pageHref = document.getElementById('page').getElementsByTagName('a') [0].href;
    var nextPage = document.getElementById('page').getElementsByTagName('a') [0].getAttribute('pageno');
    var nextPageHref = 'http://news.ibsonet.com/industry_opinion/index.html?pageNo=' + nextPage;
    document.getElementById('page').getElementsByTagName('a') [0].href = nextPageHref;
    return document.getElementsByClassName('leftSide') [0].innerHTML;
})()

(function () {
    if (document.getElementsByClassName('ly')[0].textContent.trim().indexOf('脢卤录盲拢潞') > 0) {
        return '';
    } else {
        return document.getElementsByTagName('h1')[1].textContent;
    }
})()
var ele = document.getElementsByClassName('ina_page')[0].getElementsByTagName('a');
ele[ele.length - 3].innerHTML

(function () {
    try {
        if (url.match(/_1\.html/)) {
            return '';
        }
    } catch (e) {

    }
    var strCon = document.getElementsByClassName('ina_content') [0].getElementsByTagName('img');
    var img = '';
    for (var i = 0; i < strCon.length; i++) {
        if (strCon[i].getAttribute('data-original')) {
            img = strCon[i].getAttribute('data-original');
        } else {
            img = strCon[i].src;
        }
        strCon[i].src = img;
    }
    var str = document.getElementsByClassName('ina_content') [0].innerHTML;
    return str;
})()