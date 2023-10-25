<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<script>

$(document).ready(function(){
	
	
	//'request'라는 id를 가진 버튼 클릭 시 실행.
	$("#ddd").click(function(){

	        // json 형식으로 데이터 set
	        var params = {
	                  "reqNo": "1",
	                  "carLicenseNo": "11가1111"}
	        
	        
			var memo = [{"memoNo":"", "regDate":"2023-10-25", "memo":"dfdsfadsfaadfs"}];
	        
	        params.memo=memo;
	        
	        // ajax 통신
	        $.ajax({
	            type : "POST",            // HTTP method type(GET, POST) 형식이다.
	            contentType:"application/json; charset=utf-8",
	            url : "/repairshop/carcare/enterin",      // 컨트롤러에서 대기중인 URL 주소이다.
	            data : JSON.stringify(params),            // Json 형식의 데이터이다.
	            success : function(res){ // 비동기통신의 성공일경우 success콜백으로 들어옵니다. 'res'는 응답받은 데이터이다.
	                // 응답코드 > 0000
	                alert(res.code);
	            },
	            error : function(XMLHttpRequest, textStatus, errorThrown){ // 비동기 통신이 실패할경우 error 콜백으로 들어옵니다.
	                alert("통신 실패.")
	            }
	        });
	    });
	
})


</script>
</head>
<body>

	<h3>File Upload:</h3>
      Select a file to upload: <br />
	<form action = "/repairshop/carcare/repair" method = "post" enctype = "multipart/form-data">
	   <input type="hidden" name="departmentNo" value="2"/>
	   <input type="hidden" name="reqNo" value="19"/>
	   
	   <input type = "file" name = "photo" size = "50" />
	   <input type = "file" name = "photo" size = "50" />
	   <input type = "file" name = "photo" size = "50" />
	   <br />
	   <input type = "button" value = "Upload File" />
	   
	   
	   <input type="submit" value="test" />
	   
	   <input type="button" id="ddd" value="차량등록"/>
	</form>
	
</body>
</html>