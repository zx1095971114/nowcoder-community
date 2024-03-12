$(function(){
    $("#uploadForm").submit(upload);
});

function upload(event){
    //阻止原有的表单提交行为
    event.preventDefault();

    let head = $("#head-image")[0].files[0];
    if(head == null){
        alert("请上传头像文件！");
        return false;
    }
    let suffix = head.name.split(".").pop().toLowerCase();
    if(suffix != "jpg"){
        alert("请上传jpg格式的图片!");
    }

    let filename = $("#filename").val();

    let address = $("#uploadAddress").val();
    $.ajax({
        url: address,
        method: "post",
        //不要把表单的内容转字符串
        processData: false,
        //让浏览器来自动设置类型，因为是图片，要由浏览器来设置边界
        contentType: false,
        data: new FormData(document.getElementById("uploadForm")),
        success: function (data){
            // data = JSON.parse(data)
            if(data != null || data.code == 200){
                $.post(
                    CONTEXT_PATH + "/user/saveHeader",
                    {filename: filename},
                    function (data){
                        data = JSON.parse(data);
                        if(data.code == 200){
                            alert("修改头像成功！");
                            location.reload();
                        }else{
                            alert(data.code + ": " + data.msg);
                        }
                    }
                )
            }else {
                alert("上传失败：" + data.error);
            }
        },
        error: function (jqXHR, textStatus, errorThrown){
            alert("上传失败!");
        }
    });
}