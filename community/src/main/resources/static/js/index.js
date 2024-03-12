$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//发异步请求前，将csrf的参数放到请求头中
	// let csrfContent = $("meta[name = '_csrf']").attr("content");
	// let headerName = $("meta[name = '_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e, xhr, options){
	// 	xhr.setRequestHeader(headerName, csrfContent);
	// });

	let discussPost = {
		title: $("#recipient-name").val(),
		content: $("#message-text").val()
	};

	console.log(JSON.stringify(discussPost));
	console.log(discussPost);

	$.post(
		CONTEXT_PATH + "/discussPost/publish",
		discussPost,
		function (data){
			data = JSON.parse(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code == 200){
					location.href = CONTEXT_PATH + "/index";
				}else if(data.code == 302){
					location.href = data.location;
				}else {
					alert(data.msg);
				}
			}, 2000);
		}
	);


}