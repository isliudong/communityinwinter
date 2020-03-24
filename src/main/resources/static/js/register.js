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
function verifyPwd() {
    let pwd = $("input[name='password']").val();
    let pwd2 = $("input[name='password2']").val();
    if (pwd!==pwd2){
        $('#pwdMsg').removeClass('display');
        $('#pwdMsg').show();
        $('#pwdMsg').text("两次输入的密码不相同");
    }
    if (pwd===pwd2){
        $('#pwdMsg').hide();
    }
}