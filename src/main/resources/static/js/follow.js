$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var is_follow = $(btn).hasClass("btn-info");
	var request_path = is_follow?'/followUser':'/unFollowUser';
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