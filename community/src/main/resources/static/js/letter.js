$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	//隐藏发送框
	$("#sendModal").modal("hide");

	//获取要发送的数据
	let message = {
		toName: $("#recipient-name").val(),
		content: $("#message-text").val()
	}

	//发异步请求
	$.post(
		CONTEXT_PATH + "/message/send",
		message,
		function (data){
			data = JSON.parse(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");

			//2s后自动隐藏并刷新
			setTimeout(function (){
					$("#hintModal").modal("hide");
					if(data.code == 200){
						let origin = location.href;
						let newLocation = origin.split("?")[0];
						//重定向到去掉参数的地址
						location.href = newLocation;
					}else if(data.code == 302){
						location.href = data.location;
					}
				}, 2000);
		}
	)
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}

// 点进去没有未读提示，不应该让用户主动点进去才算已读
// function readSystemMessage(element){
// 	let path = CONTEXT_PATH + element.getAttribute("data-path");
// 	let status = element.getAttribute("data-status");
// 	let id = element.getAttribute("data-id");
//
// 	if(status != 1){
// 		$.ajax({
// 			type: 'PUT', // 规定请求的类型（GET 或 POST）
// 			url: CONTEXT_PATH + "/message/read-system-message" , // 请求的url地址
// 			dataType: 'json', //预期的服务器响应的数据类型
// 			data: {messageId: id}, //规定要发送到服务器的数据
// 			success: function(result){
// 				// 当请求成功时运行的函数,code为3xx,2xx
// 			},
// 			error:function(result){
// 				//失败的函数
// 			}
// 		});
// 	}
//
// 	location.href = path;
// }