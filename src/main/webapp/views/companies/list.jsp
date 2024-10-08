<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="d-flex flex-wrap justify-content-between mb-3">
    <!-- Search -->
    <div class="d-flex align-items-center me-3 mb-2">
        <input type="text" id="search" class="form-control me-2" placeholder="搜尋商家名稱或是編號"
               style="max-width: 250px;">
        <button class="btn btn-primary" style="white-space: nowrap;">搜尋</button>
    </div>
</div>

<div class="table-responsive">
    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th scope="col">
                <a href="#" class="sort" data-sort="company-id">商家編號 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="company-name">商家名稱 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="manager">負責人 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="telephone">電話 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="username">電子郵件 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">
                <a href="#" class="sort" data-sort="date">註冊日期 <i class="bi bi-sort"></i></a>
            </th>
            <th scope="col">操作</th>
        </tr>
        </thead>
        <tbody id="companyTableBody"></tbody>
    </table>
</div>

<!-- Company Detail Modal -->
<div class="modal fade" id="companyDetailModal" tabindex="-1" aria-labelledby="companyDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="companyDetailModalLabel">公司詳細資訊</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label for="modalCompanyId" class="form-label">公司編號</label>
                    <input type="text" class="form-control" id="modalCompanyId" readonly>
                </div>
                <div class="mb-3">
                    <label for="modalCompanyName" class="form-label">公司名稱</label>
                    <input type="text" class="form-control" id="modalCompanyName">
                </div>
                <div class="mb-3">
                    <label for="modalManager" class="form-label">負責人</label>
                    <input type="text" class="form-control" id="modalManager">
                </div>
                <div class="mb-3">
                    <label for="modalTelephone" class="form-label">電話</label>
                    <input type="text" class="form-control" id="modalTelephone">
                </div>
                <div class="mb-3">
                    <label for="modalUsername" class="form-label">電子郵件</label>
                    <input type="email" class="form-control" id="modalUsername">
                </div>
                <div class="mb-3">
                    <label for="modalCreatedDate" class="form-label">註冊日期</label>
                    <input type="date" class="form-control" id="modalCreatedDate">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" id="saveCompanyButton">儲存變更</button>
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

        const tbody = document.querySelector('#companyTableBody');
        const rows = Array.from(tbody.querySelectorAll('tr'));

        // TODO: 去資料庫撈資料
    };

    const sortTable = (sortBy, sortOrder) => {
        const tbody = document.querySelector('#companyTableBody');
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
            case 'company-id':
                return 1;
            case 'company-name':
                return 2;
            case 'manager':
                return 3;
            case 'telephone':
                return 4;
            case 'username':
                return 5;
            case 'date':
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
        const defaultSortBy = ''; // Default sort column
        const defaultSortOrder = ''; // Default sort order

        // Initial sort
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

    const renderCompanies = (companies) => {
        const tableBody = document.getElementById('companyTableBody');
        tableBody.innerHTML = ''; // Clear table content

        companies.forEach((company) => {
            const row = `
            <tr>
                <th scope="row">\${company.companyId}</th>
                <td>\${company.companyName}</td>
                <td>\${company.manager}</td>
                <td>\${company.telephone}</td>
                <td>\${company.username}</td>
                <td>\${company.createdDate}</td>
                <td>
                    <button class="btn btn-sm btn-warning">修改</button>
<!--                    <button class="btn btn-sm btn-danger">刪除</button>-->
                </td>
            </tr>
        `;
            tableBody.insertAdjacentHTML('beforeend', row);
        });
    };


    // const getCompaniesListAPI = (offset = 0) => {
    //     // Example static data
    //     const mockData = {
    //         data: {
    //             result: [
    //                 { companyId: 'C001', companyName: 'Company A', manager: 'Alice Smith', telephone: '123-456-7890', username: 'alice@example.com', createdDate: '2024-01-15' },
    //                 { companyId: 'C002', companyName: 'Company B', manager: 'Bob Jones', telephone: '987-654-3210', username: 'bob@example.com', createdDate: '2024-02-20' },
    //                 { companyId: 'C003', companyName: 'Company C', manager: 'Charlie Brown', telephone: '555-555-5555', username: 'charlie@example.com', createdDate: '2024-03-30' }
    //                 // Add more mock data as needed
    //             ],
    //             total: 3,
    //             limit: 10,
    //             offset: offset
    //         }
    //     };
    //
    //     // Return a promise that resolves with the mock data
    //     return new Promise((resolve) => {
    //         setTimeout(() => resolve(mockData), 100); // Simulate network delay
    //     });
    // };

    const getCompaniesListAPI = (offset = 0) => {

        let url = '/api/store/companies?orderBy=companyId&sort=asc&limit=' + 10 + '&offset=' + offset;
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
            showAlert('Failed to retrieve company list', 'warning');
            throw error;
        });
    };


    const getCompanyDetailsAPI = (companyId) => {
        // Simulated data for demonstration purposes
        const mockData = {
            'C001': { companyId: 'C001', companyName: 'Company A', manager: 'Alice Smith', telephone: '123-456-7890', username: 'alice@example.com', createdDate: '2024-01-15' },
            'C002': { companyId: 'C002', companyName: 'Company B', manager: 'Bob Jones', telephone: '987-654-3210', username: 'bob@example.com', createdDate: '2024-02-20' },
            'C003': { companyId: 'C003', companyName: 'Company C', manager: 'Charlie Brown', telephone: '555-555-5555', username: 'charlie@example.com', createdDate: '2024-03-30' }
        };

        return mockData[companyId] || {};
    };



    const loadCompanies = async (offset = 0) => {
        try {
            const res = await getCompaniesListAPI(offset);
            const data = res.data;
            console.log(data);
            renderCompanies(data.result);
            renderPagination(data.total, data.limit, data.offset, loadCompanies);
        } catch (error) {
            console.error('Error loading companies:', error);
        }
    };

    document.addEventListener('DOMContentLoaded', () => {
        loadCompanies();
        initTable();

        // Event delegation for dynamically added rows
        document.getElementById('companyTableBody').addEventListener('click', (event) => {
            if (event.target.classList.contains('btn-warning')) {
                const row = event.target.closest('tr');
                showCompanyModal(row);
            }
        });

        document.getElementById('saveCompanyButton').addEventListener('click', saveCompanyDetails)
    });

    const showCompanyModal = (row) => {
        const companyId = row.children[0].innerText; // Retrieve company ID from the row

        // Fetch company details using companyId (for now, we use mock data)
        const companyDetails = getCompanyDetailsAPI(companyId);

        // Populate the modal with company details
        document.getElementById('modalCompanyId').value = companyDetails.companyId;
        document.getElementById('modalCompanyName').value = companyDetails.companyName;
        document.getElementById('modalManager').value = companyDetails.manager;
        document.getElementById('modalTelephone').value = companyDetails.telephone;
        document.getElementById('modalUsername').value = companyDetails.username;
        document.getElementById('modalCreatedDate').value = companyDetails.createdDate;

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('companyDetailModal'));
        modal.show();
    };

    const saveCompanyDetails = () => {
        const companyId = document.getElementById('modalCompanyId').value;
        const companyName = document.getElementById('modalCompanyName').value;
        const manager = document.getElementById('modalManager').value;
        const telephone = document.getElementById('modalTelephone').value;
        const username = document.getElementById('modalUsername').value;
        const createdDate = document.getElementById('modalCreatedDate').value;

        // Implement logic to save updated company details (send data to backend, etc.)
        console.log(`Saving changes for Company ID: \${companyId}, Name: \${companyName}, Manager: \${manager}, Telephone: \${telephone}, Username: \${username}, Date: \${createdDate}`);

        // Close the modal after saving changes
        const modal = bootstrap.Modal.getInstance(document.getElementById('companyDetailModal'));
        modal.hide();
    };
</script>
