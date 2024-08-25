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

<!-- Customer Detail Modal -->
<div class="modal fade" id="customerDetailModal" tabindex="-1" aria-labelledby="customerDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="customerDetailModalLabel">客戶詳細資訊</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label for="modalCustomerId" class="form-label">客戶編號</label>
                    <input type="text" class="form-control" id="modalCustomerId" readonly>
                </div>
                <div class="mb-3">
                    <label for="modalCustomerName" class="form-label">客戶名稱</label>
                    <input type="text" class="form-control" id="modalCustomerName">
                </div>
                <div class="mb-3">
                    <label for="modalCustomerEmail" class="form-label">電子郵件</label>
                    <input type="email" class="form-control" id="modalCustomerEmail">
                </div>
                <div class="mb-3">
                    <label for="modalCustomerPhone" class="form-label">電話</label>
                    <input type="text" class="form-control" id="modalCustomerPhone">
                </div>
                <div class="mb-3">
                    <label for="modalCustomerDate" class="form-label">註冊日期</label>
                    <input type="date" class="form-control" id="modalCustomerDate">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="saveCustomerButton">儲存變更</button>
            </div>
        </div>
    </div>
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
            </tr>`;
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

    const getCustomerDetailsAPI = (customerId) => {
        // Simulated data for demonstration purposes
        const mockData = {
            'C001': { userId: 'C001', firstName: '張', lastName: '三', username: 'zhangsan@example.com', phoneNumber: '0987654321', createdDate: '2024-07-01' },
            'C002': { userId: 'C002', firstName: '李', lastName: '四', username: 'lisi@example.com', phoneNumber: '0912345678', createdDate: '2024-07-15' },
            'C003': { userId: 'C003', firstName: '王', lastName: '五', username: 'wangwu@example.com', phoneNumber: '0922333444', createdDate: '2024-08-05' }
        };

        return mockData[customerId] || {};
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

        // Event delegation for dynamically added rows
        document.getElementById('customerTableBody').addEventListener('click', (event) => {
            if (event.target.classList.contains('btn-warning')) {
                const row = event.target.closest('tr');
                showCustomerModal(row);
            }
        });

        document.getElementById('saveCustomerButton').addEventListener('click', saveCustomerDetails);
    });

    const showCustomerModal = (row) => {
        const customerId = row.children[0].innerText; // Retrieve customer ID from the row

        // Fetch customer details using customerId (for now, we use mock data)
        const customerDetails = getCustomerDetailsAPI(customerId);

        // Populate the modal with customer details
        document.getElementById('modalCustomerId').value = customerDetails.userId;
        document.getElementById('modalCustomerName').value = customerDetails.firstName + customerDetails.lastName;
        document.getElementById('modalCustomerEmail').value = customerDetails.username;
        document.getElementById('modalCustomerPhone').value = customerDetails.phoneNumber;
        document.getElementById('modalCustomerDate').value = customerDetails.createdDate;

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('customerDetailModal'));
        modal.show();
    };

    const saveCustomerDetails = () => {
        const customerId = document.getElementById('modalCustomerId').value;
        const customerName = document.getElementById('modalCustomerName').value;
        const customerEmail = document.getElementById('modalCustomerEmail').value;
        const customerPhone = document.getElementById('modalCustomerPhone').value;
        const customerDate = document.getElementById('modalCustomerDate').value;

        // Implement logic to save updated customer details (send data to backend, etc.)
        console.log(`Saving changes for Customer ID: \${customerId}, Name: \${customerName}, Email: \${customerEmail}, Phone: \${customerPhone}, Date: \${customerDate}`);

        // Close the modal after saving changes
        const modal = bootstrap.Modal.getInstance(document.getElementById('customerDetailModal'));
        modal.hide();
    };
</script>