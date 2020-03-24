

window.onload=function eyeball() {
    document.querySelector('body').addEventListener('mousemove',eyeball);
    let eye =document.querySelectorAll('.eye');
    let face =document.querySelectorAll('.eyes');
    eye.forEach(function (eye) {

        let x=eye.getBoundingClientRect().left+eye.clientWidth/2;
        let y=eye.getBoundingClientRect().top+eye.clientWidth/2;
        let radian=Math.atan2(event.pageX-x,event.pageY-y);
        let rot=(radian*(180/Math.PI)*-1)+270;
        eye.style.transform="rotate("+rot+"deg)";
    })

}


