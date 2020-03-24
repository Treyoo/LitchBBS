$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	var is_follow = $(btn).hasClass("btn-info");
	var request_path = is_follow?'/followUser':'/unFollowUser';
	$.post(request_path,{'userId':$("#userId").val()},
    function(data){
        data = $.parseJSON(data);
        console.log(data)
        if(data.code == 0) {
        if(is_follow){
            $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
            $("#followerCount").text(parseInt($("#followerCount").text())+1);
        }else{
            $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
            $("#followerCount").text(parseInt($("#followerCount").text())-1);
        }
        } else {
            alert(data.msg);
        }
    });
}