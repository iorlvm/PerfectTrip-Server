<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<h2 class="my-4">系統設定</h2>
<form>
    <div class="mb-3">
        <label for="siteName" class="form-label">網站名稱</label>
        <input type="text" class="form-control" id="siteName" placeholder="輸入網站名稱">
    </div>
    <div class="mb-3">
        <label for="adminEmail" class="form-label">管理員電子郵件</label>
        <input type="email" class="form-control" id="adminEmail" placeholder="輸入管理員電子郵件">
    </div>
    <button type="submit" class="btn btn-primary">保存設定</button>
</form>
