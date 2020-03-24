function a() {
    $.ajax({
        url: "/ajax",
        method:"post",
        contentType:"application/json",
        data: JSON.stringify({
            "name": $("#textInput").val()
        }),

        success: function (data, status) {
            console.log(data);
            console.log(status);
        }
    });


    console.log($("#textInput").val())
}