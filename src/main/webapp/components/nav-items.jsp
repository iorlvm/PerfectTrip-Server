<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Dashboard -->
<li class="nav-item">
    <a class="nav-link" href="dashboard">
        總覽
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
                <a class="nav-link" href="reservations/current">
                    當前訂單
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="reservations/history">
                    訂單歷史
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
                <a class="nav-link" href="customers/list">
                    客戶列表
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="customers/feedback">
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
                <a class="nav-link" href="merchants/list">
                    商家列表
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="merchants/verification">
                    商家驗證
                </a>
            </li>
        </ul>
    </div>
</li>

<!-- Reports -->
<li class="nav-item">
    <a class="nav-link collapsed" data-bs-toggle="collapse" href="#reportsMenu" role="button" aria-expanded="false"
       aria-controls="reportsMenu">
        報告圖表
    </a>
    <div class="collapse" id="reportsMenu">
        <ul class="nav flex-column ms-3">
            <li class="nav-item">
                <a class="nav-link" href="reports/overview">
                    總覽報告
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="reports/statistics">
                    統計報告
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
                <a class="nav-link" href="settings">
                    系統設定
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="admin/logout">
                    登出
                </a>
            </li>
        </ul>
    </div>
</li>