$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	//发送异步请求前把CSRF令牌以kv形式设置到请求消息头中
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options){
	    xhr.setRequestHeader(header, token);
	});
	//获取标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
//  Ajax异步post
    $.post(
        "/discuss/add",
        {"title":title, "content":content},
        function(data){
            data = $.parseJSON(data)
            $("#hintBody").text(data.msg)
            $("#hintModal").modal("show");
            setTimeout(function(){
                $("#hintModal").modal("hide");
//            		刷新页面
                if(data.code == 0){
                    window.location.reload();
                }
            }, 2000);
        }
    );
}