function r(){
    $.ajax({
        url : '/verify',
        contentType: "application/json",
        dataType:"json",
        data : JSON.stringify({
            'username' : $("input[name='name']").val(),
            'password' : $("input[name='password']").val(),
            'email':$("input[name='email']").val()
        }),
        type : 'POST',
        success: function(data){
            console.log(data);
            $('#msg').removeClass('display');
            $('#msg').text(data.message);
        }
    })
}