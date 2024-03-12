$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	let userEntityId = $(btn).prev().val();
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
			CONTEXT_PATH + "/follow",
			{entityType: 3, entityId: userEntityId},
			function (data) {
				data = JSON.parse(data);
				if(data.code == 200){
					$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");

					if($("#followerCount") != null){
						let followerCount = parseInt($("#followerCount").text());
						$("#followerCount").text(followerCount + 1);
					}
				}else {
					alert(data.msg);
				}
			}
		);
	} else {
		// 取消关注
		$.post(
			CONTEXT_PATH + "/unfollow",
			{entityType: 3, entityId: userEntityId},
			function (data) {
				data = JSON.parse(data);
				if(data.code == 200){
					$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");

					if($("#followerCount") != null){
						let followerCount = parseInt($("#followerCount").text());
						$("#followerCount").text(followerCount - 1);
					}
				}else {
					alert(data.msg);
				}
			}
		);
	}
}