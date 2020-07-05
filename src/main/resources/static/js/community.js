//提交问题一级回复
function post() {

    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

//回复接口
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
                        window.open("https://github.com/login/oauth/authorize?client_id=5a0ae794703f8d9490fd&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
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
        var subCommentContainer = $("#comment-" + id);
        //判断是否加载过，已经加载过则不再刷新
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            //标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            //获取评论并显示
            $.getJSON("/comment/" + id, function (data) {

                //拼接二级评论页面(非常麻烦，所以有vue等框架)
                $.each(data.data.reverse(), function (index, comment) {

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('HH: mm DD/MM/YYYY')
                    })));
                    var avatarElement = $("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    });
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    });
                    mediaLeftElement.append(avatarElement);
                    var mediaElement = $("<div/>", {
                        "class": "media"
                    });
                    mediaElement.append(mediaLeftElement).append(mediaBodyElement);
                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    });
                    commentElement.append(mediaElement);
                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                //标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }

    }

}

//发布问题选择问题标签
function selectTag(e) {
    let value = e.getAttribute("data-tagName");
    let previous = $("#tag").val();
    if (previous.split(',').indexOf(value) == -1) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }
}

//展示标签库
function showSelectTag() {
    $("#selectTag").show();

}