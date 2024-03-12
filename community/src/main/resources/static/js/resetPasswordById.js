document.getElementById("save").addEventListener("click", submitReset);
function submitReset(event){
    event.preventDefault();

    let oldPassword = document.getElementById("old-password");
    let newPassword = document.getElementById("new-password");
    let confirmPassword = document.getElementById("confirm-password");

    //判空和小于8位
    if(isBlankOrLess8(oldPassword, document.getElementById("old-password-msg"))
        || isBlankOrLess8(newPassword, document.getElementById("new-password-msg"))
        || isBlankOrLess8(confirmPassword, document.getElementById("confirm-password-msg"))
    ){
        return;
    }

    //判断密码是否相同
    if(newPassword.value !== confirmPassword.value){
        resetAllMsg(document.getElementById("old-password"),
            document.getElementById("new-password"),
            document.getElementById("confirm-password")
        );
        confirmPassword.className = "form-control is-invalid";
        document.getElementById("confirm-password-msg").innerHTML = "两次密码输入不一致";
        return;
    }

    document.getElementById("reset-form").submit();
}

function isBlankOrLess8(element, suggestElement){
    if(element.value == ""){
        resetAllMsg(document.getElementById("old-password"),
            document.getElementById("new-password"),
            document.getElementById("confirm-password")
        );
        element.className = "form-control is-invalid";
        suggestElement.innerHTML = "请输入以上字段";
        return true;
    }

    if(element.value.length < 8){
        resetAllMsg(document.getElementById("old-password"),
            document.getElementById("new-password"),
            document.getElementById("confirm-password")
        );
        element.className = "form-control is-invalid";
        suggestElement.innerHTML = "密码不能小于8位";
        return true;
    }
    return false;
}

//重置提示信息
function resetAllMsg(){
    for(let i = 0; i < arguments.length; i++){
        arguments[i].className = "form-control";
    }
}