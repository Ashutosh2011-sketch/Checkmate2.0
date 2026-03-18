function outest(){
    var c =20;
    function outer(b){
        let a = 10;
        function inner(){
            console.log(a,b,c);
        }
        a = 30;
        return inner;
    }
    return outer;
}
var c = 100;
let a = 200;
outest()("Hello")();