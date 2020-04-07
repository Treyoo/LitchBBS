$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var is_follow = $(btn).hasClass("btn-info");
	var request_path = is_follow?'/followUser':'/unFollowUser';
    //发送异步请求前把CSRF令牌以kv形式设置到请求消息头中
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options){
        xhr.setRequestHeader(header, token);
    });
	$.post(request_path,{'userId':$(btn).val()},
    function(data){
        data = $.parseJSON(data);
        console.log(data)
        if(data.code == 0) {
        if(is_follow){
            $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
        }else{
            $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
        }
        } else {
            alert(data.msg);
        }
    });
}