var express = require('express');
var server = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
var connection = mysql.createConnection({
    user : 'root',
    password : '1234',
    database : 'voice',
    host : '54.183.138.146',
    timeout: 60000    
});
connection.connect();
server.use(bodyParser.urlencoded({ extended: false }));
server.use(bodyParser.json());

/*server.get('/', (req, res) => {
    res.sendFile('C:\Users\XNOTE\grap-bot-rps\11.html');
});
*/
server.get('/voice/', function (req, res, next) {
    console.log(1);
    connection.query('SELECT * FROM family;', function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log(result);
            res.json(result);
        }
    });
}); 

server.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});

module.exports = server;
// server.run = function () {
//     server.listen(3000, function () {
//         console.log('Example app listening on port 3000!');
//     });
// };
// module.exports = server;
// server.get('/user/:id', function (req, res) {

//     for (var i = 0; i < db1.userlist.length; i++){
//         if(db1.userlist[i].id === parseInt(req.params.id)){
//             res.send(db1.userlist[i]);
//             return;
//         }
//     }
//     res.send({ 
//         error : 'ID가 존재하지 않습니다.'
//     });
// });

// var ids = 0;

// server.post('/user/', function (req, res) {
//     var body = req.body;
//     var id = 0; 
//     var age = parseInt(body.age);

//     for (var i = 0; i < db1.userlist.length; i++){
//         if(id < db1.userlist[i].id){
//         id = db1.userlist[i].id;                     
//         }
//     }
//     body['age'] = age; 
//     //console.log(body.name)
//     body['id'] = ++length;
//     db1.userlist.push(body);
//     /*var result = {
//                     result : {
//                         isOk : true, 
//                         user : body
//                     }
//     }       
//     res.send(result)*/
//     res.send({   
//         result : {
//             isOk : true, 
//             user : body
//         }
//     })
//     //console.log(result.result.isOk)
//     //console.log(result.isOk)
// });

// server.delete('/user/:id', function (req, res) {
//     var index = 9999;
//     for (var i = 0; i < db1.userlist.length; i++){
//         if(db1.userlist[i].id === parseInt(req.params.id)) {
//             index = i;
//             break;
//         }
//     }    
//     if(index === 9999){
//         res.send({
//             result : {
//                 isOk : false , error : 'ID가 존재하지 않습니다.'
//             }
//         });
//     } 
//     else { 
//         res.send({
//             result : {
//                 isOk : true , user : db1.userlist[index]
//             }
//         });
//         db1.userlist.splice(index,1);  
//     }  
// });

// server.put('/user/:id', function (req, res) {
//     var body = req.body;
//     var id = 0;
//     var index;

//     for (var i = 0; i < db.length; i++){
//         if(db1.userlist[i].id === parseInt(req.params.id)){
//             index = i;
//             db1.userlist.splice(index,1);
//             body['id'] = parseInt(req.params.id);
//             db1.userlist.splice(index,0,body);
//             res.send({   
//                 result : {
//                     isOk : true, 
//                     user : body
//                 }
//             })
//         return;
//         }
//     }    
//     res.send({   
//         result : {
//             isOk : false, 
//             error : 'ID가 존재하지 않습니다.'
//         }
//     })
//     //db.push(body);
// });
//서버 실행 함수

/*function custonSort(a, b) {
  if(a.id == b.id){ return 0} return  a.id > b.id ? 1 : -1;
}
db.sort(custonSort);
*/
