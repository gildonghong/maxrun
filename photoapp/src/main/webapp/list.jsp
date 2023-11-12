<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>접속 정비소 리스트</title>

<script>
	var exampleSocket = new WebSocket("ws://localhost/socket?loginId=maxrun&passwd=maxrun");
	
	/*연결수립시*/
	exampleSocket.onopen = function (event) {
		  alert("HI");
	};
	
	/*현재 소켓 서버에서 물고 있는 데이터 목록*/
	exampleSocket.onmessage = function (event) {
		var msg = JSON.parse(event.data);
		alert(msg);
	};
	
	let repeatWork = setInterval(getSituations, 10000);	//10초 간격으로 넷트워크 파일 문제 현황 조회
	
	
	function getSituations(){
		
		
	}
</script>
</head>
<body>

</body>
</html>