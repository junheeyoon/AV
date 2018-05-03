var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

var app = express();
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
// port setup
app.set('port', process.env.PORT || 9000);
app.get('/',function(req,res){
 res.send('Hello World!')
})

//test
var http = require('http');

// 1. 요청한 url을 객체로 만들기 위해 url 모듈사용
var url = require('url');
// 2. 요청한 url 중에 Query String 을 객체로 만들기 위해 querystring 모듈 사용
var querystring = require('querystring'); 

var server = http.createServer(function(request,response){
    // 3. 콘솔화면에 로그 시작 부분을 출력
    console.log('--- log start ---');
    // 4. 브라우저에서 요청한 주소를 parsing 하여 객체화 후 출력
    var parsedUrl = url.parse(request.url);
    console.log(parsedUrl);
    // 5. 객체화된 url 중에 Query String 부분만 따로 객체화 후 출력
    var parsedQuery = querystring.parse(parsedUrl.query,'&','=');
    console.log(parsedQuery);
    // 6. 콘솔화면에 로그 종료 부분을 출력
    console.log('--- log end ---');

    response.writeHead(200, {'Content-Type':'text/html'});
    response.end('Hello node.js!!');
});

server.listen(8080, function(){

    console.log('Server is running...');
});
//
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});
//////////////////////////////////////////////////////
// ------- creates Server -------
module.exports = app;

var server = app.listen(app.get('port'), function() {
console.log('Express server listening on port ' + server.address().port);
});
module.exports = app;

var http = require('http');
var url = require('url');
var fs = require('fs');

// 1. mime 모듈 추가. 서비스하려는 파일의 타입을 알아내기 위해서 필요
var mime = require('mime');

var server = http.createServer(function(request,response){

  var parsedUrl = url.parse(request.url);
  var resource = parsedUrl.pathname;

  // 2. 요청한 자원의 주소가 '/images/' 문자열로 시작하면
  if(resource.indexOf('/images/') == 0){
    // 3. 첫글자인 '/' 를 제외하고 경로를 imgPath 변수에 저장
    var imgPath = resource.substring(1);
    console.log('imgPath='+imgPath);
    // 4. 서비스 하려는 파일의 mime type
    var imgMime = mime.getType(imgPath); // lookup -> getType으로 변경됨
    console.log('mime='+imgMime);

    // 5. 해당 파일을 읽어 오는데 두번째 인자인 인코딩(utf-8) 값 없음
    fs.readFile(imgPath, function(error, data) {
      if(error){
        response.writeHead(500, {'Content-Type':'text/html'});
        response.end('500 Internal Server '+error);
      }else{
        // 6. Content-Type 에 4번에서 추출한 mime type 을 입력
        response.writeHead(200, {'Content-Type':imgMime});
        response.end(data);
      }
    });
  }else{
    response.writeHead(404, {'Content-Type':'text/html'});
    response.end('404 Page Not Found');
  }
});

server.listen(80, function(){
    console.log('Server is running...');
});

