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
                <div class="d-flex">
                    <label for="modalCustomerFirstName" class="form-label flex-grow-1">姓</label>
                    <label for="modalCustomerLastName" class="form-label flex-grow-1">名</label>
                </div>
                <div class="mb-3 d-flex">
                    <input type="text" class="form-control flex-grow-1" id="modalCustomerFirstName">
                    <input type="text" class="form-control flex-grow-1" id="modalCustomerLastName">
                </div>
                <div class="mb-3">
                    <label for="modalCustomerEmail" class="form-label">電子郵件</label>
                    <input type="email" class="form-control" id="modalCustomerEmail">
                </div>
                <div class="mb-3">
                    <label for="modalCustomerPhone" class="form-label">電話</label>
                    <input type="text" class="form-control" id="modalCustomerPhone">
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
            <tr data-user-id="\${customer.userId}">
                <th scope="row">\${customer.userId}</th>
                <td>\${customer.firstName+customer.lastName}</td>
                <td>\${customer.username}</td>
                <td>\${customer.phoneNumber}</td>
                <td>\${customer.createdDate}</td>
                <td>
                    <button class="btn btn-sm btn-warning">編輯</button>
                </td>
            </tr>`;
            tableBody.insertAdjacentHTML('beforeend', row);
        });
    };

    const getCustomersListAPI = (offset = 0) => {
        let url = '/api/users?orderBy=userId&sort=asc&limit=' + 20 + '&offset=' + offset;
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
        let url = '/api/users/'+ customerId;
        return fetch(url, {
            method: 'GET'
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(res => {
            console.log(res.data)
            return res.data;
        }).catch(error => {
            showAlert('取得使用者失敗', 'warning');
            throw error;
        });
    };

    const updateCustomerAPI = (customerId, userUpdateRequest) => {
        let url = '/api/users/'+ customerId;
        return fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userUpdateRequest)
        }).then(response => {
            if (!response.ok) {
                return Promise.reject(new Error(`HTTP error! Status: \${response.status}`));
            }
            return response.json();
        }).then(res => {
            return res;
        }).catch(error => {
            showAlert('更新使用者失敗', 'warning');
            throw error;
        });
    };

    const loadCustomers = async (offset = 0) => {
        try {
            const res = await getCustomersListAPI(offset);
            const data = res.data;
            renderCustomers(data.result);
            renderPagination(data.total, data.limit, data.offset, loadCustomers);
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

    const showCustomerModal = async (row) => {
        const customerId = row.children[0].innerText; // Retrieve customer ID from the row

        // Fetch customer details using customerId (for now, we use mock data)
        const customerDetails = await getCustomerDetailsAPI(customerId);

        // Populate the modal with customer details
        document.getElementById('modalCustomerId').value = customerDetails.userId;
        document.getElementById('modalCustomerFirstName').value = customerDetails.firstName;
        document.getElementById('modalCustomerLastName').value = customerDetails.lastName;
        document.getElementById('modalCustomerEmail').value = customerDetails.username;
        document.getElementById('modalCustomerPhone').value = customerDetails.phoneNumber;

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('customerDetailModal'));
        modal.show();
    };

    const saveCustomerDetails = async () => {
        const userId = document.getElementById('modalCustomerId').value;
        const firstName = document.getElementById('modalCustomerFirstName').value;
        const lastName = document.getElementById('modalCustomerLastName').value;
        const username = document.getElementById('modalCustomerEmail').value;
        const phoneNumber = document.getElementById('modalCustomerPhone').value;

        const res = await updateCustomerAPI(userId, {
            firstName,
            lastName,
            username,
            phoneNumber
        });

        if (res.success) {
            const row = document.querySelector(`tr[data-user-id="\${userId}"]`);
            if (row) {
                row.cells[1].textContent = firstName + lastName;
                row.cells[2].textContent = username;
                row.cells[3].textContent = phoneNumber;
            }
            showAlert('更新成功');
        } else {
            showAlert('更新成功','error');
        }

        // Close the modal after saving changes
        const modal = bootstrap.Modal.getInstance(document.getElementById('customerDetailModal'));
        modal.hide();
    };
</script>
