//提交回复
function post() {

    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {

    if (!content) {
        alert("内容为空~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
                //$("#comment_section").hide();
            } else {
                if (response.code == 2003) {

                    var isAccept = confirm(response.message);
                    if (isAccept) {
                        window.open("https://github.com/login/oauth/authorize?client_id=40cca80d10082a99e683&redirect_uri=http://localhost:8080/callback&scope=user&state=1")
                        window.localStorage.setItem("isClose", true);
                    }
                } else {
                    alert(response.message)
                }
            }

        },
        dataType: "json"
    });
}

//二级回复
function comment(e) {
    let id = e.getAttribute("data-id");
    let content = $("#sub-comment-" + id).val();
    comment2target(id, 2, content);
}

//展开二级评论
function collapseComments(e) {

    let id = e.getAttribute("data-id");
    let comments = $("#comment-" + id);


    //获取二级评论展开状态
    let collapse = e.getAttribute("data-collapse");
    if (collapse) {
        //关闭二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        $.getJSON("/comment/" + id, function (data) {
            console.log(data);
            let commentBody = $("comment-body-" + id);
            let items = [];


            //拼接二级评论页面
            $.each(data.data, function (comment) {
                var c=$("<div/>", {
                    "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    html: comment.content
                });
                items.push(c);
            });

            $("<div/>", {
                "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 collapse sub-comments", "id": "comment-id" + id,
                html: items.join("")
            }).appendTo(commentBody);


            //展开二级评论
            comments.addClass("in");
            //标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        });

    }

}