toastr.options = {
    closeButton: true,
    debug: false,
    newestOnTop: true,
    progressBar: true,
    positionClass: "toast-top-right",
    preventDuplicates: false,
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "5000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut"
};

function parseMsg(msg) {
    return msg.split(";ttl=");
}

function toastWarning(msg) {
    const [_msg, ttl] = parseMsg(msg);

    if (ttl && parseInt(ttl) < new Date().getTime()) return;

    toastr["warning"](_msg, "경고");
}

function toastNotice(msg) {
    const [_msg, ttl] = parseMsg(msg);

    if (ttl && parseInt(ttl) < new Date().getTime()) return;

    toastr["success"](_msg, "성공");
}

function getQueryParams() {
    const params = new URLSearchParams(window.location.search);
    const paramsObj = {};

    for (const [key, value] of params) {
        paramsObj[key] = value;
    }

    return paramsObj;
}

/* 로그아웃 버튼의 기능을 위한 스크립트 (POST 요청, csrf 토큰) */
$(function () {
    $('a[method="post"], a[method="POST"]').click(function (e) {
        const action = $(this).attr('href');
        const csfTokenValue = $("meta[name='_csrf']").attr("content");

        // 동적으로 폼을 만든다.
        const $form = $(`<form action="${action}" method="POST"><input type="hidden" name="_csrf" value="${csfTokenValue}"></form>`);
        $('body').append($form);

        $form.submit();

        return false;
    });
});