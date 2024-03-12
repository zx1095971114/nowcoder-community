$(function (){
    $("#topBtn").click(setTop);
    $("#refinementBtn").click(setRefinement);
    $("#deleteBtn").click(setDelete);
});

function like(element, entityType, entityId, entityUserId, postId){
    let a = 1;
    $.post(
        CONTEXT_PATH + "/like",
        {entityType: entityType, entityId: entityId, entityUserId: entityUserId, postId: postId},
        function (data){
            data = JSON.parse(data);
            if(data.code == 200){
                $(element).children("b").text((data.hostLikeStatus == LIKE) ? "已赞" : "赞");
                $(element).children("i").text(data.likeCount);
            }else {
                alert(data.msg);
                if(data.code == 302){
                    location.href = data.location;
                }
            }
        }
    );
}

function setTop(){
    let btn = this;
    let id = $("#discussPostId").val();
    if($(btn).hasClass("btn-danger")){
        $.post(
            CONTEXT_PATH + "/discussPost/top",
            {id: id},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    $(btn).text("已置顶").removeClass("btn-danger").addClass("btn-secondary");
                }else {
                    alert(data.code + ": " + data.msg);
                }
            }
        );
    }else {
        $.post(
            CONTEXT_PATH + "/discussPost/untop",
            {id: id},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    $(btn).text("置顶").removeClass("btn-secondary").addClass("btn-danger");
                }else {
                    alert(data.code + ": " + data.msg);
                }
            }
        );
    }
}

function setRefinement(){
    let btn = this;
    let id = $("#discussPostId").val();
    if($(btn).hasClass("btn-danger")){
        $.post(
            CONTEXT_PATH + "/discussPost/refinement",
            {id: id},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    $(btn).text("已加精").removeClass("btn-danger").addClass("btn-secondary");
                }else {
                    alert(data.code + ": " + data.msg);
                }
            }
        );
    }else {
        $.post(
            CONTEXT_PATH + "/discussPost/unrefinement",
            {id: id},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    $(btn).text("加精").removeClass("btn-secondary").addClass("btn-danger");
                }else {
                    alert(data.code + ": " + data.msg);
                }
            }
        );
    }
}

function setDelete(){
    let result = confirm("确定要删除这个帖子吗？");

    if(result){
        let btn = this;
        let id = $("#discussPostId").val();
        $.post(
            CONTEXT_PATH + "/discussPost/delete",
            {id: id},
            function (data){
                data = JSON.parse(data);
                if(data.code == 200){
                    alert("帖子已删除！");
                    window.location.href = CONTEXT_PATH + "/index";
                }else {
                    alert(data.code + ": " + data.msg);
                }
            }
        );
    }

}