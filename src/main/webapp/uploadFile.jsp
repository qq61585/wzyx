<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>文件上传</title>
</head>
<body>
<h2>文件上传</h2>
<form action="/mobile/seller/user/submit_audit_material" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td>phoneNumber</td>
            <td><input type="text" name="phoneNumber"></td>
        </tr>
        <tr>
            <td>password:</td>
            <td><input type="text" name="password"></td>
        </tr>
        <tr>
            <td>realName:</td>
            <td><input type="text" name="realName"></td>
        </tr>
        <tr>
            <td>identifyNumber:</td>
            <td><input type="text" name="identifyNumber"></td>
        </tr>
        <tr>
            <td>businessLicenseNumber:</td>
            <td><input type="text" name="businessLicenseNumber"></td>
        </tr>

        <tr>
            <td>identifyCardFrontPhoto:</td>
            <td><input type="file" name="files"></td>
        </tr>
        <tr>
            <td>identifyCardBackPhoto:</td>
            <td><input type="file" name="files"></td>
        </tr>
        <tr>
            <td>businessLicensePhot:</td>
            <td><input type="file" name="files"></td>
        </tr>
        <tr>
            <td><input type="submit" value="上传"></td>
        </tr>
    </table>
</form>
</body>
</html>
