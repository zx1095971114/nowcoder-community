
function getVerify() {
    let email = document.getElementById("your-email").value;

    $.post(
        CONTEXT_PATH + "/verify",
        {email: email},
        function (data) {
            data = JSON.parse(data);
            if(data.code == 200){
                alert(data.msg);
                hideError();
                // 禁用按钮
                $("#getVerifycode").removeClass("btn-info").addClass("btn-secondary").addClass("disabled");

                let seconds = 60; // 设定倒计时秒数

                // 每秒更新一次
                timer = setInterval(function() {
                    seconds = updateButton(seconds, timer);
                }, 1000);
            }else if(data.code == 400){
                hideError();
                $("#emailError").text(data.emailMsg);
                $("#your-email").addClass("is-invalid");
            }else {
                alert(data.code + ": " + data.msg);
            }
        }
    );


}


// timer = setInterval(function() {
//     seconds = updateButton(seconds, timer);
// }, 1000);
// 更新按钮文本和倒计时
function updateButton(seconds, timer) {
    $("#getVerifycode").text("已获取(" + seconds + ")");
    seconds--;

    if (seconds < 0) {
        // 恢复按钮状态
        $("#getVerifycode").removeClass("btn-secondary").addClass("btn-info").removeClass("disabled");
        $("#getVerifycode").text("获取验证码");
        clearInterval(timer);
        return;
    }

    return seconds;
}

// function showError() {
//     $("#your-email").addClass("is-invalid");
//     $("#verifycode").addClass("is-invalid");
//     $("#your-password").addClass("is-invalid");
// }

function hideError(){
    $("#your-email").removeClass("is-invalid");
    $("#verifycode").removeClass("is-invalid");
    $("#your-password").removeClass("is-invalid");

}

function resetPassword(){
    $.post(
        CONTEXT_PATH + "/forget",
        {
            email: document.getElementById("your-email").value,
            code: $("#verifycode").val(),
            password: $("#your-password").val()
        },

        function (data){
            data = JSON.parse(data);
            hideError();
            if(data.code == 400){
                if(data.emailMsg != null){
                    $("#your-email").addClass("is-invalid");
                    $("#emailError").text(data.emailMsg);
                }

                if(data.codeMsg != null){
                    $("#verifycode").addClass("is-invalid");
                    $("#codeError").text(data.codeMsg);
                }

                if(data.passwordMsg != null){
                    $("#your-password").addClass("is-invalid");
                    $("#passwordError").text(data.passwordMsg);
                }
            }else if(data.code == 200){
                let message = data.message;
                let href = data.href;
                window.location = CONTEXT_PATH + "/operate-result?message=" + encodeURIComponent(message) + "&href=" + encodeURIComponent(href);
            }else {
                alert(data.code + ": " + data.msg);
            }
        }
    );
}

