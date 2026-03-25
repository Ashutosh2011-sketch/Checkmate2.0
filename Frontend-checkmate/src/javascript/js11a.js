// print 1 ,2 ,3 ,4,5 after n second each

//using var
function x(){
    for(var i = 1; i<=5; i++){
        function close(x) {
            setTimeout ( function(){
                console.log(x);
            },x*1000);
        }
        close(i);
    }
}
// x();

// using let we can do it direct without a need of closure
function y(){
    for (let i =1;i<=5; i++){
        setTimeout( function(){
            console.log(i);
        },i*1000);
    }
}
y();