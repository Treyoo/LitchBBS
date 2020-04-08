$(function(){
    $("#topBtn").click(setTop);
    $("#highlightBtn").click(setHighlight);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId) {
	//发送异步请求前把CSRF令牌以kv形式设置到请求消息头中
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options){
	    xhr.setRequestHeader(header, token);
	});
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId},
        function(data) {
            data = $.parseJSON(data);
            console.log(data)
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 置顶
function setTop() {
	//发送异步请求前把CSRF令牌以kv形式设置到请求消息头中
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options){
	    xhr.setRequestHeader(header, token);
	});
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                var btnText = $("#topBtn").text();
                $("#topBtn").text(btnText == '置顶'?'取消置顶':'置顶');
            } else {
                alert(data.msg);
            }
        }
    );
}

// 加精
function setHighlight() {
	//发送异步请求前把CSRF令牌以kv形式设置到请求消息头中
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options){
	    xhr.setRequestHeader(header, token);
	});
    $.post(
        CONTEXT_PATH + "/discuss/highlight",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                var btnText = $("#highlightBtn").text();
                $("#highlightBtn").text(btnText == '加精'?'取消加精':'加精');
            } else {
                alert(data.msg);
            }
        }
    );
}

// 删除
function setDelete() {
	//发送异步请求前把CSRF令牌以kv形式设置到请求消息头中
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options){
	    xhr.setRequestHeader(header, token);
	});
    $.ajax({
        url:"/discuss/delete",
        type:"delete",
        dataType:"json",
        data:{"id":$("#postId").val()},
        success:function(data){
            alert(data)
            if(data.code == 0) {
                alert("删除成功！")
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        },
        error:function(xhr,textStatus,thrown){
          console.log(textStatus);
        }
    });
}