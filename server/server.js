var express = require('express');
var server = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
var connection = mysql.createConnection({
    // host : '54.183.138.146',
    // user : 'ec2-user',
    // password : '1234',
    // database : 'voice',
    user     : 'root',
    password : '1234',
    database : 'voice'

});
connection.connect(function(error){
    if(error){
        console.log(error);
    }
    else{
        console.log('Connected');
    }
});
server.use(bodyParser.urlencoded({ extended: false }));
server.use(bodyParser.json());

server.get('/voice/', function (req, res, next) {
    //res.writeHead(200, {'Content-Type': 'text/json;charset=utf-8'});
    connection.query("SELECT * FROM family", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log('Access');
            console.log(result);
            res.json(result);
        }
    });
});

server.get('/test/', function (req, res, next) {
    connection.query("SELECT * FROM family", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log('Access');
            console.log(result);
            res.json(result);
        }
    });    
});

server.post('/test/', function (req, res, next) {
    var body = req.body;
    console.log(body)
    console.log('123123');
    if(parseInt(body.message.indexOf('관리자')) !== -1){
        res.json({
                isOk : true, 
                message : body.message + '사용자가 등록되었습니다.'
        
        });
    }
    else{
        res.json({
            isOk : false, 
            message : body.message + '사용자가 등록이 실패했습니다.'

        });
    }
    console.log('444');
});

server.get('/state/:device_id', function (req, res, next) {
    connection.query("SELECT * FROM object WHERE device_id = ?", req.params.device_id, function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log('Access');
            console.log(result);
            res.json(result.state);
        }
    });
});

server.get('/password/:user_id', function (req, res, next) {
    connection.query("SELECT * FROM user WHERE user_id = ?", req.params.user_id, function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log('Access');
            console.log(result);
            res.json(result.password);
        }
    });
});

server.post('/user/', function (req, res, next) {
    var body = req.body;
    connection.query("INSERT INTO user SET ?", body, function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
            res.json({
                result : {
                        isOk : false, 
                        error : "쿼리문에 오류가 있습니다."
                }
            });
        }
        else{
            console.log('Access');
            console.log(result);
            res.json({
                result : {
                        isOk : true, 
                        message : '사용자가 등록되었습니다.'
                }
            });
        }
    });
}); 
var kk;
server.post('/device/', function (req, res, next) {
    var j;
    var body = req.body;
    
    var post_id;
    console.log(body)
    var sum = 0;    
    connection.query("SELECT * FROM object", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log('Access');
            console.log(result);
            for(j = 0; j < result.length; j++){
                console.log(result[j].object_id);
                sum = sum + result[j].object_id;
            }
            console.log('sum' + sum);
            kk = sum;            
        }
        post_id = 3 - sum;
        let sql = "INSERT INTO object (object_id, object_state, object_name) VALUES (?, ?, ?)";
        connection.query(sql, [ post_id,0,body.message ], function(err, rows) {
            if(err){
                console.log(err);
                console.log("쿼리문에 오류가 있습니다.");
            }
            else{
                console.log('응답성공');
                res.json({
                        isOk : true, 
                        message : '디바이스가 등록되었습니다.'
                });
            }
        });
        console.log('final' + kk);
    });
    console.log('fuck' + sum)
    
    
});

server.delete('/user/:user_name', function (req, res, next) {
    var i = 0;
    var user_name;
    var body = req.body;
    connection.query("SELECT * FROM user", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            console.log('Access');
            console.log(result);
            for(i = 0; i < result.length; i++){
                user_name = body.name.indexOf(result[i].user_name);
                if(parseInt(name) !== -1){
                    if(parseInt(body.name.indexOf('삭제')) !== -1){
                        connection.query("DELETE * FROM user WHERE user_name = ?", user_name, function(error, rows){ 
                            if(error){ 
                                throw error;
                            }	
                            else{ 
                                console.log(rows); 
                            } 
                        });
                        res.json({
                            isOk : true,
                            message : result[i].user_name + '가 삭제되었습니다.'
                        });
                    }                  
                    else {
                        res.json({
                            isOk : false, 
                            error : '명령을 확인해주세요.'
                        });
                    }
                }
            }
            res.json({
                isOk : false, 
                error : 'user가 존재하지 않습니다.'
            });
        }
    });
    // connection.query("DELETE * FROM user WHERE user_id = ?", req.params.user_id, function(err, result, fields){
    //     if(err){
    //         console.log(err);
    //         console.log("쿼리문에 오류가 있습니다.");
    //         res.json({
    //             result : {
    //                     isOk : false, 
    //                     error : "쿼리문에 오류가 있습니다."
    //             }
    //         });
    //     }
    //     else{
    //         console.log('Access');
    //         console.log(result);
    //         res.json({
    //             result : {
    //                     isOk : true, 
    //                     message : req.params.user_id + '가 삭제되었습니다.'
    //             }
    //         });
    //     }
        
    // });
}); 

server.put('/device/', function (req, res, next) {
    var i = 0;
    var name;
    var body = req.body;
    
    connection.query("SELECT * FROM object", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else{
            //eeee
            console.log('Access');
            console.log(result);
            if(parseInt(body.message.indexOf('관리자')) !== -1){
            
                for(i = 0; i < result.length; i++){
                    name = body.message.indexOf(result[i].object_name);
                    if(parseInt(name) !== -1){
                        if(parseInt(body.message.indexOf('삭제')) !== -1){
                            connection.query("DELETE FROM object WHERE object_name = ?", result[i].object_name, function(error, rows){ 
                                if(error){ 
                                    throw error;
                                }	
                                else{ 
                                    console.log(rows); 
                                } 
                            });
                            res.json({
                                isOk : true,
                                message : result[i].object_name + '이 삭제되었습니다.'
                            });
                        }                  
                        else {
                            res.json({
                                isOk : false, 
                                message : '명령을 다시 확인해주세요.'
                            });
                        }
                    }
                }
            
            }
            else if(i === result.length){
                res.json({
                                
                        isOk : false, 
                        message : '디바이스가 없습니다. 다시한번 확인해주세요.'
                
                });
                
            } 
            else {
                res.json({
            
                    isOk : false, 
                    message : '관리자가 아닙니다.'
            
                });
            }
        }
    });
});    
//     connection.query("DELETE * FROM object WHERE object_name = ?", req.params.object_name, function(err, result, fields){
//         if(err){
//             console.log(err);
//             console.log("쿼리문에 오류가 있습니다.");
//             res.json({
//                 isOk : false, 
//                 error : "쿼리문에 오류가 있습니다."
//             });
//         }
//         else{
//             console.log('Access');
//             console.log(result);
//             res.json({
//                 isOk : true, 
//                 message : req.params.object_name + '가 삭제되었습니다.'
//             });
//         }
//     });
// }); 
//test
//teees
server.put('/state/', function (req, res, next) {
    var i = 0;
    var name;
    var body = req.body;
    //body.message = "관리자인데 전등좀 켜줘";
    connection.query("SELECT * FROM object", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else {
            console.log('Access');
            console.log(result);
            if(parseInt(body.message.indexOf('관리자')) !== -1){
                
                for(i = 0; i < result.length; i++){
                    console.log(1);
                    if(parseInt(body.message.indexOf('가스')) !== -1){
                        res.json({
                            isOk : true,
                            message :  '가스레인지가 꺼졌습니다.'
                            //state : result[i].object_state
                        });
                    }
    
                    if(parseInt(body.message.indexOf('문')) !== -1){
                        if(parseInt(body.message.indexOf('닫아')) !== -1){
                            res.json({
                                isOk : true,
                                message :  '현관문이 닫혔습니다.'
                                //state : result[i].object_state
                            });
                        }
                        if(parseInt(body.message.indexOf('열')) !== -1){
                            res.json({
                                isOk : true,
                                message :  '현관문이 열렸습니다.'
                                //state : result[i].object_state
                            });
                        }
                    }
                    name = body.message.indexOf(result[i].object_name);
                    if(parseInt(name) !== -1){
                        console.log(2);
                        if(parseInt(body.message.indexOf('켜')) !== -1){
                            console.log(3);
                            console.log(result[i].object_name);
                            connection.query("UPDATE object SET object_state = 1 WHERE object_name = ?", result[i].object_name, function(error, rows){ 
                                if(error){ 
                                    throw error;
                                }	
                                else{ 
                                    console.log(rows); 
                                } 
                            });
                            console.log(4);
                            res.json({
                                        isOk : true,
                                        message :  result[i].object_name + '이 켜졌습니다.',
                                        device_id : result[i].object_id
                                        //state : result[i].object_state
                                
                            });
                            console.log(44);
                        }
                        else if(parseInt(body.message.indexOf('꺼')) !== -1){
                            connection.query("UPDATE object SET object_state = 0 WHERE object_name = ?", result[i].object_name, function(error, rows){ 
                                if(error){ 
                                    throw error;
                                }	 
                                else{ 
                                    console.log(rows); 
                                } 
                            });
                            res.json({
                                
                                        isOk : false,
                                        message :  result[i].object_name + '이 꺼졌습니다.',
                                        device_id : result[i].object_id
                                        //state : result[i].object_state
                                
                            });
                        }
                        else {
                            res.json({
                                
                                        isOk : false, 
                                        message : '다시 명령을 해주세요.'
                                
                            });
                        }
                    }                                        
                }
                if(i == result.length){
                    res.json({
                                
                        isOk : false, 
                        message : '등록되어있지 않은 디바이스입니다. 확인해주세요.'
                
                    });
                    console.log(res.body);
                    console.log('tteetsetest');
                }                                
            }
            // else if(i == result.length){
            //     res.json({
                                
            //             isOk : false, 
            //             message : '등록되어있지 않은 디바이스입니다. 확인해주세요.'
                
            //     });
            //     console.log(res.body);
            //     console.log('tteetsetest');
                
            // } 
            else {
                res.json({
            
                    isOk : false, 
                    message : '관리자가 아닙니다.'
            
                });
            }
        }
    });
}); 

server.get('/state/', function (req, res, next) {
    var i = 0;
    var buffer;
    var body = req.body;
    connection.query("SELECT object_name FROM object WHERE object_state = 1", function(err, result, fields){
        if(err){
            console.log(err);
            console.log("쿼리문에 오류가 있습니다.");
        }
        else {
            console.log('Access');
            console.log(result);
            if(parseInt(body.message.indexOf('관리자')) !== -1){
                for(i = 0; i < result.length; i++){
                    console.log(1);
                    if(parseInt(body.message.indexOf('상태')) !== -1){
                        buffer = buffer + ' ' + result[i].object_name;
                        console.log(buffer);
                    }
                    else {
                        res.json({
                            
                                    isOk : false, 
                                    message : '다시 명령을 해주세요.'
                            
                        });
                    }
                }
                res.json({
                    isOk : true,
                    message :  buffer + '이 켜있습니다.'
                    //state : result[i].object_state
                        
                });
            }
            else {
                res.json({
            
                    isOk : false, 
                    message : '관리자가 아닙니다.'
            
                });
            }
        }
    });
}); 

server.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});

module.exports = server;


