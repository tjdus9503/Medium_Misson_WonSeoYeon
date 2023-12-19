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

/* a 태그를 통해 [POST, PUT, DELETE] 메서드로 요청을 보내기 위한 스크립트 */
$(function () {

    // (1) a 태그의 onclick에 지정된 동작을 onclick-before 속성으로 이동
    $('a[method="POST"][onclick], a[method="PUT"][onclick], a[method="DELETE"][onclick]').each(function (index, el) {
        const onclick = $(el).attr('onclick');

        $(el).removeAttr('onclick');

        $(el).attr('onclick-before', onclick);
    });

    // (2) a 태그의 click 이벤트로 수행할 동작을 지정 : form을 동적으로 생성한 후 submit
    $('a[method="POST"], a[method="PUT"], a[method="DELETE"]').click(function (e) {

        // onclick-before 이벤트로 지정된 동작이 있는 경우에는, 그걸 먼저 실행하고 결과가 false 이면 동작 중단 (form 생성/제출을 하지 않음)
        if ($(this).attr('onclick-before')) {
            let onclickBefore = null;

            eval("onclickBefore = function() { " + $(this).attr('onclick-before') + "}");

            if (!onclickBefore()) return false;
        }

        // 동적으로 form 요소를 만든다.
        const action = $(this).attr('href');
        const csfTokenValue = $("meta[name='_csrf']").attr("content");

        const formHtml = `
        <form action="${action}" method="POST">
            <input type="hidden" name="_csrf" value="${csfTokenValue}">
            <input type="hidden" name="_method" value="${$(this).attr('method')}">
        </form>
        `;

        // 웹 브라우저가 동적 생성한 form 요소를 인식하도록 DOM 트리에 추가한 후, form을 제출
        const $form = $(formHtml);
        $('body').append($form);
        $form.submit();

        return false; // a 태그의 기본 동작을 하지 않도록 한다. (href 에 지정된 주소로 GET 요청)
    });
});