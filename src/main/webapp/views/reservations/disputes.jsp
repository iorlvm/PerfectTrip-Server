<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="d-flex flex-wrap justify-content-between mb-3">
    <!-- 搜尋和過濾 -->
    <div class="d-flex align-items-center me-3 mb-2">
        <input type="text" id="search" class="form-control me-2" placeholder="搜尋爭議訂單或訂單編號"
               style="max-width: 250px;">
        <button class="btn btn-primary" style="white-space: nowrap;">搜尋</button>
    </div>

    <!-- 狀態過濾 -->
    <div class="d-flex align-items-center mb-2">
        <label for="statusFilter" class="form-label me-2 mb-0" style="white-space: nowrap;">狀態:</label>
        <select id="statusFilter" class="form-select" style="max-width: 150px;" onchange="filterTable()">
            <option value="">全部</option>
            <option value="in-progress">處理中</option>
            <option value="unresolved">未處理</option>
            <option value="resolved">已完成</option>
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
                <a href="#" class="sort" data-sort="dispute-title">爭議內容 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="appeal-date">申訴日期 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="status">狀態 <i class="bi bi-sort"></i></a>
            </th>
        </tr>
        </thead>
        <tbody id="disputeTableBody"></tbody>
    </table>
</div>

<!-- Modal -->
<div class="modal fade" id="disputeDetailModal" tabindex="-1" aria-labelledby="disputeDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="disputeDetailModalLabel">爭議訂單詳細資訊</h5>
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
                        <strong>爭議內容:</strong>
                        <p id="modalDisputeTitle" class="text-muted"></p>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-4">
                        <strong>申訴日期:</strong>
                        <p id="modalAppealDate" class="text-muted"></p>
                    </div>
                    <div class="col-md-4">
                        <strong>狀態:</strong>
                        <p id="modalStatus" class="text-muted"></p>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-12">
                        <strong>詳細說明:</strong>
                        <p id="modalDetails" class="text-muted"></p>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-12">
                        <strong>處理意見:</strong>
                        <p id="modalResolutionText" class="text-muted d-none"></p>
                        <textarea id="modalResolutionInput" class="form-control d-none" rows="3"  placeholder="請輸入處理意見" style="resize: none; overflow: auto"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer justify-content-between" id="modalFooter">
                <button type="button" class="btn btn-danger" id="rejectButton">拒絕</button>
                <button type="button" class="btn btn-success" id="approveButton">批准</button>
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

        const tbody = document.querySelector('#disputeTableBody');
        const rows = Array.from(tbody.querySelectorAll('tr'));


    };

    const sortTable = (sortBy, sortOrder) => {
        const tbody = document.querySelector('#disputeTableBody');
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
            case 'dispute-title':
                return 3;
            case 'appeal-date':
                return 4;
            case 'status':
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

        const rows = document.querySelectorAll('#disputeTableBody tr');
        rows.forEach(row => {
            row.addEventListener('click', (event) => {
                showModal(row);
            });
        });
    };

    const showModal = async (row) => {
        const orderId = row.children[0].innerText;
        const disputeDetails = await getDisputeDetailsAPI(orderId);

        document.getElementById('modalOrderId').innerText = disputeDetails.orderId;
        document.getElementById('modalCustomerName').innerText = disputeDetails.customerName;
        document.getElementById('modalDisputeTitle').innerText = disputeDetails.disputeTitle;
        document.getElementById('modalAppealDate').innerText = disputeDetails.appealDate;
        document.getElementById('modalStatus').innerText = disputeDetails.status;
        document.getElementById('modalDetails').innerText = disputeDetails.details;

        const status = disputeDetails.status;
        const footer = document.getElementById('modalFooter');
        const resolutionText = document.getElementById('modalResolutionText');
        const resolutionInput = document.getElementById('modalResolutionInput');

        if (status === '已完成') {
            footer.style.display = 'none'; // Hide action buttons
            resolutionText.innerText = disputeDetails.resolution;
            resolutionText.classList.remove('d-none');
            resolutionInput.classList.add('d-none');
            resolutionInput.value = '';
        } else {
            footer.style.display = 'flex'; // Show action buttons
            resolutionInput.value = '';
            resolutionText.classList.add('d-none');
            resolutionInput.classList.remove('d-none');
        }

        const modal = new bootstrap.Modal(document.getElementById('disputeDetailModal'));
        modal.show();
    }


    const getDisputeDetailsAPI = (orderId) => {
        // Simulated data for demonstration purposes
        const mockData = {
            'O001': { orderId: 'O001', customerName: '張三', disputeTitle: '預訂問題', appealDate: '2024-07-01', status: '處理中', details: '詳細說明1', resolution: '處理意見1' },
            'O002': { orderId: 'O002', customerName: '李四', disputeTitle: '付款糾紛', appealDate: '2024-07-15', status: '未處理', details: '詳細說明2', resolution: '處理意見2' },
            'O003': { orderId: 'O003', customerName: '王五', disputeTitle: '服務質量', appealDate: '2024-08-05', status: '已完成', details: '詳細說明3', resolution: '處理意見3' }
        };

        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(mockData[orderId] || {});
            }, 500); // Simulate network delay
        });
    };


    const renderDisputes = (disputes) => {
        const tableBody = document.getElementById('disputeTableBody');
        tableBody.innerHTML = ''; // 清空表格內容

        disputes.forEach((dispute) => {
            const row = `
            <tr>
                <th scope="row">\${dispute.orderId}</th>
                <td>\${dispute.customerName}</td>
                <td>\${dispute.disputeTitle}</td>
                <td>\${dispute.appealDate}</td>
                <td>\${dispute.status}</td>
            </tr>
        `;
            tableBody.insertAdjacentHTML('beforeend', row);
        });
    };

    const getDisputesListAPI = (offset = 0) => {
        // 範例靜態數據
        const mockData = {
            data: {
                result: [
                    { orderId: 'O001', customerName: '張三', disputeTitle: '預訂問題', appealDate: '2024-07-01', status: '處理中' },
                    { orderId: 'O002', customerName: '李四', disputeTitle: '付款糾紛', appealDate: '2024-07-15', status: '未處理' },
                    { orderId: 'O003', customerName: '王五', disputeTitle: '服務質量', appealDate: '2024-08-05', status: '已完成' }
                    // 可以添加更多範例數據
                ],
                total: 3,
                limit: 10,
                offset: offset
            }
        };

        // 返回模擬數據的 Promise
        return new Promise((resolve) => {
            setTimeout(() => resolve(mockData), 500); // 模擬網絡延遲
        });
    };

    const loadDisputes = async (offset = 0) => {
        try {
            const res = await getDisputesListAPI(offset);
            const data = res.data;
            console.log(data);
            renderDisputes(data.result);
            renderPagination(data.total, data.limit, data.offset);
        } catch (error) {
            console.error('加載爭議訂單時出錯:', error);
        }
    };

    document.addEventListener('DOMContentLoaded', async () => {
        await loadDisputes();
        initTable();
    });
</script>
