<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<style>
    .sort {
        text-decoration: none; /* 去除底線 */
        color: inherit; /* 繼承父元素的顏色 */
        cursor: pointer; /* 鼠標懸停時顯示為手型 */
        opacity: 0.8;
    }

    .sort:hover {
        color: #007bff;
    }

    .sort.asc ,.sort.desc {
        opacity: 1;
    }
</style>

<div class="d-flex flex-wrap justify-content-between mb-3">
    <!-- 狀態過濾 -->
    <div class="d-flex align-items-center mb-2">
        <label for="statusFilter" class="form-label me-2 mb-0" style="white-space: nowrap;">狀態:</label>
        <select id="statusFilter" class="form-select" style="max-width: 150px;" onchange="filterTable()">
            <option value="">全部</option>
            <option value="confirmed">已確認</option>
            <option value="pending">待確認</option>
            <option value="cancelled">已取消</option>
        </select>
    </div>

    <!-- 搜索和過濾 -->
    <div class="d-flex align-items-center me-3 mb-2">
        <input type="text" id="search" class="form-control me-2" placeholder="搜尋客戶或訂單編號"
               style="max-width: 250px;">
        <button class="btn btn-primary" style="white-space: nowrap;">搜尋</button>
    </div>
</div>

<div class="table-responsive">
    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th scope="col">
                <a href="#" class="sort" data-sort="order-number">訂單編號 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="customer-name">客戶名稱 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="hotel-name">旅館名稱 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="date">日期 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="amount">金額 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="status">狀態 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">操作</th>
        </tr>
        </thead>
        <tbody id="orderTableBody">
        <!-- Example row -->
        <tr>
            <th scope="row">#12345</th>
            <td>王小明</td>
            <td>星空旅館</td>
            <td>2024-08-22</td>
            <td>NT$ 5,000</td>
            <td><span class="badge bg-success">已確認</span></td>
            <td>
                <button class="btn btn-sm btn-primary">查看</button>
                <button class="btn btn-sm btn-danger">取消</button>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<%@ include file="/components/pagination.jsp" %>


<script>
    const filterTable = () => {
        // 簡單的過濾邏輯示例，實際應用可能需要根據需求進行調整
        const searchValue = document.getElementById('search').value.toLowerCase();
        const statusValue = document.getElementById('statusFilter').value.toLowerCase();
        const rows = document.querySelectorAll('#orderTableBody tr');

        rows.forEach(row => {
            const customerName = row.cells[1].textContent.toLowerCase();
            const orderId = row.cells[0].textContent.toLowerCase();
            const status = row.cells[5].textContent.toLowerCase();

            const matchSearch = customerName.includes(searchValue) || orderId.includes(searchValue);
            const matchStatus = statusValue === '' || status.includes(statusValue);

            row.style.display = (matchSearch && matchStatus) ? '' : 'none';
        });
    }

    const sortTable = (sortBy, sortOrder) => {
        const tbody = document.querySelector('#orderTableBody');
        const rows = Array.from(tbody.querySelectorAll('tr'));

        rows.sort((a, b) => {
            const aText = a.querySelector(`:nth-child(\${getSortColumnIndex(sortBy)})`).innerText;
            const bText = b.querySelector(`:nth-child(\${getSortColumnIndex(sortBy)})`).innerText;

            return (sortOrder === 'asc' ? aText.localeCompare(bText) : bText.localeCompare(aText));
        });

        rows.forEach(row => tbody.appendChild(row));
    };

    const getSortColumnIndex = (sortBy) => {
        switch (sortBy) {
            case 'order-number':
                return 1;
            case 'customer-name':
                return 2;
            case 'hotel-name':
                return 3;
            case 'date':
                return 4;
            case 'amount':
                return 5;
            case 'status':
                return 6;
            default:
                return 1;
        }
    };

    const updateSortIcons = (activeLink, sortOrder) => {
        document.querySelectorAll('.sort').forEach(link => {
            link.classList.remove('asc', 'desc');
            link.querySelector('i').classList.remove('bi-sort-up', 'bi-sort-down');
        });

        activeLink.classList.add(sortOrder);
        const icon = activeLink.querySelector('i');
        icon.classList.add(sortOrder === 'asc' ? 'bi-sort-up' : 'bi-sort-down');
    };

    const initTable = () => {
        // 設置初始排序
        const defaultSortBy = 'date'; // 默認排序列
        const defaultSortOrder = 'desc'; // 默認排序方向

        // 初始排序
        sortTable(defaultSortBy, defaultSortOrder);
        const defaultLink = document.querySelector(`.sort[data-sort="\${defaultSortBy}"]`);
        if (defaultLink) {
            defaultLink.classList.add(defaultSortOrder);
            updateSortIcons(defaultLink, defaultSortOrder);
        }

        const sortLinks = document.querySelectorAll('.sort');

        sortLinks.forEach(link => {
            link.addEventListener('click', (event) => {
                event.preventDefault();
                const sortBy = link.getAttribute('data-sort');
                const sortOrder = link.classList.contains('desc') ? 'asc' : 'desc';
                sortTable(sortBy, sortOrder);
                updateSortIcons(link, sortOrder);
            });
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        initTable();
    });
</script>
