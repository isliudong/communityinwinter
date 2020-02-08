function post() {

    let questionId = $("#question_id").val();
    let content = $("#comment_content").val();

    if (!content){
        alert("内容为空~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": questionId,
            "content": content,
            "type": 1
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
                //$("#comment_section").hide();
            } else {
                if (response.code == 2003) {

                    var isAccept = confirm(response.message);
                    if (isAccept){
                        window.open("https://github.com/login/oauth/authorize?client_id=40cca80d10082a99e683&redirect_uri=http://localhost:8080/callback&scope=user&state=1")
                        window.localStorage.setItem("isClose",true);
                    }
                } else {
                    alert(response.message)
                }
            }

        },
        dataType: "json"
    });
}