<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="d-flex flex-wrap justify-content-between mb-3">
    <!-- 搜尋和過濾 -->
    <div class="d-flex align-items-center me-3 mb-2">
        <input type="text" id="search" class="form-control me-2" placeholder="搜尋訂單或訂單編號"
               style="max-width: 250px;">
        <button class="btn btn-primary" style="white-space: nowrap;">搜尋</button>
    </div>

    <!-- 狀態過濾 -->
    <div class="d-flex align-items-center mb-2">
        <label for="statusFilter" class="form-label me-2 mb-0" style="white-space: nowrap;">狀態:</label>
        <select id="statusFilter" class="form-select" style="max-width: 150px;" onchange="filterTable()">
            <option value="">全部</option>
            <option value="completed">已完成</option>
            <option value="pending">未完成</option>
            <option value="disputed">爭議處理</option>
        </select>
    </div>
</div>


<div class="table-responsive">
    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th scope="col">
                <a href="#" class="sort" data-sort="order-id">訂單編號 <i class="bi bi-sort"></i></a>
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
        <tbody id="orderTableBody"></tbody>
    </table>
</div>

<!-- Order Detail Modal -->
<div class="modal fade" id="orderDetailModal" tabindex="-1" aria-labelledby="orderDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="orderDetailModalLabel">訂單詳細資訊</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="row mb-3">
                    <div class="col-md-4">
                        <strong>訂單編號:</strong>
                        <p id="modalOrderId" class="text-muted"></p>
                    </div>
                    <div class="col-md-4">
                        <strong>客戶名稱:</strong>
                        <p id="modalCustomerName" class="text-muted"></p>
                    </div>
                    <div class="col-md-4">
                        <strong>旅館名稱:</strong>
                        <p id="modalHotelName" class="text-muted"></p>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-4">
                        <strong>日期:</strong>
                        <p id="modalDate" class="text-muted"></p>
                    </div>
                    <div class="col-md-4">
                        <strong>金額:</strong>
                        <p id="modalAmount" class="text-muted"></p>
                    </div>
                    <div class="col-md-4">
                        <strong>狀態:</strong>
                        <p id="modalStatus" class="text-muted"></p>
                    </div>
                </div>
            </div>
            <div class="modal-footer justify-content-between">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="saveButton">退款</button>
            </div>
        </div>
    </div>
</div>


<%@ include file="/components/pagination.jsp" %>
<%@ include file="/components/alert.jsp" %>

<script>
    const pageSize = 20;

    const filterTable = () => {
        const searchQuery = document.getElementById('search').value.toLowerCase();
        const statusFilter = document.getElementById('statusFilter').value;

        const tbody = document.querySelector('#orderTableBody');
        const rows = Array.from(tbody.querySelectorAll('tr'));

        // TODO: 去資料庫撈資料
    };


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
            case 'order-id':
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
        const defaultSortBy = ''; // 預設排序列
        const defaultSortOrder = ''; // 預設排序方向

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
    };

    const renderOrders = (orders) => {
        const tableBody = document.getElementById('orderTableBody');
        tableBody.innerHTML = ''; // 清空表格內容

        orders.forEach((order) => {
            const row = `
            <tr>
                <th scope="row">\${order.orderId}</th>
                <td>\${order.subscriber}</td>
                <td>\${order.hotelName}</td>
                <td>\${order.createdDate}</td>
                <td>\${order.actualPrice}</td>
                <td>\${order.payStatus}</td>
                <td>
                    <button class="btn btn-sm btn-warning">處理退款</button>
                </td>
            </tr>`;
            tableBody.insertAdjacentHTML('beforeend', row);
        });
    };

    const getOrdersListAPI = (offset = 0) => {
        const page = Math.floor(offset / pageSize);

        let url = '/api/orders?size=' + pageSize + '&page=' + page;
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
            showAlert('取得訂單列表失敗', 'warning');
            throw error;
        });
    };

    const getOrderDetailsAPI = (orderId) => {
        let url = '/api/orders/' + orderId;
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
            showAlert('取得訂單資料失敗', 'warning');
            throw error;
        });
    };

    const loadOrders = async (offset = 0) => {
        try {
            const res = await getOrdersListAPI(offset);
            const data = res.data;
            renderOrders(data);
            renderPagination(res.total, pageSize, res.page * pageSize, loadOrders);
        } catch (error) {
            console.error('加載訂單時出錯:', error);
        }
    };

    document.addEventListener('DOMContentLoaded', () => {
        loadOrders();
        initTable();

        // Event delegation for dynamically added rows
        document.getElementById('orderTableBody').addEventListener('click', (event) => {
            if (event.target.classList.contains('btn-warning')) {
                const row = event.target.closest('tr');
                showModal(row);
            }
        });

        document.getElementById('saveButton').addEventListener('click', () => {
            const orderId = document.getElementById('modalOrderId').innerText;

            // Implement logic to save updated details (send data to backend, etc.)
            console.log(`Saving changes for Order ID: \${orderId}`);

            // Close the modal after saving changes
            const modal = bootstrap.Modal.getInstance(document.getElementById('orderDetailModal'));
            modal.hide();
        });
    });

    const showModal = async (row) => {
        const orderId = row.children[0].innerText;

        const res = await getOrderDetailsAPI(orderId);
        console.log(res)

        document.getElementById('modalOrderId').innerText = res.data.orderId;
        document.getElementById('modalCustomerName').innerText = res.data.subscriber;
        document.getElementById('modalHotelName').innerText = res.data.hotelName;
        document.getElementById('modalDate').innerText = res.data.createdDate;
        document.getElementById('modalAmount').innerText = res.data.actualPrice;
        document.getElementById('modalStatus').innerText = res.data.payStatus;

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
        modal.show();
    };
</script>