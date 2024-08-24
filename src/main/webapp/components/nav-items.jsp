<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Messages -->
<li class="nav-item">
    <a class="nav-link" href="/dashboard">
        報告總覽
    </a>
</li>

<!-- Messages -->
<li class="nav-item">
    <a class="nav-link" href="/messages">
        訊息中心
    </a>
</li>

<!-- Reservations -->
<li class="nav-item">
    <a class="nav-link collapsed" data-bs-toggle="collapse" href="#reservationsMenu" role="button" aria-expanded="false"
       aria-controls="reservationsMenu">
        訂單管理
    </a>
    <div class="collapse" id="reservationsMenu">
        <ul class="nav flex-column ms-3">
            <li class="nav-item">
                <a class="nav-link" href="/reservations/list">
                    訂單列表
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/reservations/disputes">
                    爭議處理
                </a>
            </li>
        </ul>
    </div>
</li>

<!-- Customers -->
<li class="nav-item">
    <a class="nav-link collapsed" data-bs-toggle="collapse" href="#customersMenu" role="button" aria-expanded="false"
       aria-controls="customersMenu">
        客戶管理
    </a>
    <div class="collapse" id="customersMenu">
        <ul class="nav flex-column ms-3">
            <li class="nav-item">
                <a class="nav-link" href="/customers/list">
                    客戶列表
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/customers/feedback">
                    客戶反饋
                </a>
            </li>
        </ul>
    </div>
</li>

<!-- Merchants -->
<li class="nav-item">
    <a class="nav-link collapsed" data-bs-toggle="collapse" href="#merchantsMenu" role="button" aria-expanded="false"
       aria-controls="merchantsMenu">
        商家管理
    </a>
    <div class="collapse" id="merchantsMenu">
        <ul class="nav flex-column ms-3">
            <li class="nav-item">
                <a class="nav-link" href="/companies/list">
                    商家列表
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/companies/verification">
                    商家驗證
                </a>
            </li>
        </ul>
    </div>
</li>

<!-- Settings -->
<li class="nav-item">
    <a class="nav-link collapsed" data-bs-toggle="collapse" href="#settingsMenu" role="button" aria-expanded="false"
       aria-controls="settingsMenu">
        設定
    </a>
    <div class="collapse" id="settingsMenu">
        <ul class="nav flex-column ms-3">
            <li class="nav-item">
                <a class="nav-link" href="/settings">
                    系統設定
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/admin/logout">
                    登出
                </a>
            </li>
        </ul>
    </div>
</li>