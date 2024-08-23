<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="d-flex flex-wrap justify-content-between mb-3">
    <!-- 搜索 -->
    <div class="d-flex align-items-center me-3 mb-2">
        <input type="text" id="search" class="form-control me-2" placeholder="搜尋客戶姓名名或編號"
               style="max-width: 250px;">
        <button class="btn btn-primary" style="white-space: nowrap;">搜尋</button>
    </div>
</div>

<div class="table-responsive">
    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th scope="col">
                <a href="#" class="sort" data-sort="customer-id">客戶編號 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="customer-name">客戶名稱 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="customer-email">電子郵件 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="phone">電話 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="date">註冊日期 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">操作</th>
        </tr>
        </thead>
        <tbody id="customerTableBody"></tbody>
    </table>
</div>

<%@ include file="/components/pagination.jsp" %>
<%@ include file="/components/alert.jsp" %>

<script>
    const filterTable = () => {
        const searchQuery = document.getElementById('search').value.toLowerCase();
        const statusFilter = document.getElementById('statusFilter').value;

        const tbody = document.querySelector('#customerTableBody');
        const rows = Array.from(tbody.querySelectorAll('tr'));

        // TODO: 去資料庫撈資料
    };

    const sortTable = (sortBy, sortOrder) => {
        const tbody = document.querySelector('#customerTableBody');
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
            case 'customer-id':
                return 1;
            case 'customer-name':
                return 2;
            case 'customer-email':
                return 3;
            case 'phone':
                return 4;
            case 'date':
                return 5;
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
        const defaultSortBy = ''; // 默認排序列
        const defaultSortOrder = ''; // 默認排序方向

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


    const renderCustomers = (customers) => {
        const tableBody = document.getElementById('customerTableBody');
        tableBody.innerHTML = ''; // 清空表格内容

        customers.forEach((customer, index) => {
            const row = `
            <tr>
                <th scope="row">\${customer.userId}</th>
                <td>\${customer.firstName+customer.lastName}</td>
                <td>\${customer.username}</td>
                <td>\${customer.phoneNumber}</td>
                <td>\${customer.createdDate}</td>
                <td>
                    <button class="btn btn-sm btn-warning">編輯</button>
                    <button class="btn btn-sm btn-danger">刪除</button>
                </td>
            </tr>
        `;
            tableBody.insertAdjacentHTML('beforeend', row);
        });
    };

    const getCustomersListAPI = (offset = 0) => {
        let url = '/users?limit=' + 10 + '&offset=' + offset;
        return fetch(url, {
            method: 'GET'
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(data => {
            return data;
        }).catch(error => {
            showAlert('取得使用者列表失敗', 'warning');
            throw error;
        });
    };

    const loadCustomers = async (offset = 0) => {
        try {
            const res = await getCustomersListAPI(offset);
            const data = res.data;
            console.log(data)
            renderCustomers(data.result);
            renderPagination(data.total, data.limit, data.offset);
        } catch (error) {
            console.error('Error loading customers:', error);
        }
    };

    document.addEventListener('DOMContentLoaded', () => {
        loadCustomers();
        initTable();
    });
</script>
