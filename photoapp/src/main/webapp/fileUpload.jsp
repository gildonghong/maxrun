<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>




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
	</form>
	
</body>
</html>