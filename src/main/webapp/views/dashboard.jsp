<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<script src="https://code.highcharts.com/highcharts.js"></script>

<div class="row g-3">
    <!-- 今日訂單卡片 -->
    <div class="col-md-4">
        <div class="text-decoration-none">
            <div class="card custom-card bg-primary-light text-dark mb-3 shadow-sm rounded">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-cart-check"></i> 今日訂單</h5>
                    <p class="h3 fw-bold mb-1" id="todayOrders">0</p>
                    <p class="card-text"><small id="growthPercentage">比昨日增長 0%</small></p>
                </div>
            </div>
        </div>
    </div>

    <!-- 本月收入卡片 -->
    <div class="col-md-4">
        <div class="text-decoration-none">
            <div class="card custom-card bg-success-light text-dark mb-3 shadow-sm rounded">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-currency-dollar"></i>本月收入</h5>
                    <p class="h3 fw-bold mb-1"><span id="currentRevenue">NT$ 0</span></p>
                    <p class="card-text"><small>對比上月 <span id="revenueDifference">0</span></small></p>
                </div>
            </div>
        </div>
    </div>

    <!-- 新增客戶卡片 -->
    <div class="col-md-4">
        <div class="text-decoration-none">
            <div class="card custom-card bg-info-light text-dark mb-3 shadow-sm rounded">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-people"></i> 本月客戶</h5>
                    <p class="h3 fw-bold mb-1" id="newCustomersCount">0</p>
                    <p class="card-text"><small>今日新增 <span id="newCustomersToday">0</span> 位</small></p>
                </div>
            </div>
        </div>
    </div>

</div>

<h4 class="my-1">統計圖表</h4>
<div class="row">
    <div class="chart-container col-md-6" style="position: relative;">
        <div id="roomTypeSalesChart" style="height: 400px;"></div>
    </div>
    <div class="chart-container col-md-6" style="position: relative;">
        <div id="customerSourceChart" style="height: 350px;"></div>
    </div>
    <div class="chart-container col-md-12" style="position: relative;">
        <div id="monthlyOrdersChart" style="height: 400px;"></div>
    </div>
</div>


<script>
    // 計算最近幾天的標籤
    const getLastNDaysLabels = (days) => {
        const labels = [];
        const today = new Date();
        const dayNames = ['今天', '昨天', '前天'];

        for (let i = 0; i < days; i++) {
            if (i < 3) {
                labels.push(dayNames[i]);  // 今天, 昨天, 前天
            } else {
                labels.push(`\${i}天前`);  // 三天前, 四天前...
            }
        }
        return labels.reverse();
    };

    const fetchData = async (url) => {
        const response = await fetch(url);
        return response.json();
    };

    document.addEventListener('DOMContentLoaded', async function () {
        // 假設有一個 API 端點用來獲取會員數據
        const customerDataRes = await fetchData('/api/statistics/new-customers');
        const customerData = customerDataRes.data;

        document.getElementById('newCustomersCount').textContent = customerData.totalNewCustomers;
        document.getElementById('newCustomersToday').textContent = customerData.newCustomersToday;
    });

    document.addEventListener('DOMContentLoaded', async function () {
        // Fetch 過去三十天的收入
        const revenueRes = await fetchData('/api/statistics/revenue');
        const revenueData = revenueRes.data;

        // 更新卡片顯示
        document.getElementById('currentRevenue').textContent = `NT$ \${revenueData.currentRevenue}`;

        const revenueDifference = revenueData.revenueDifference;
        const revenueDiffSpan = document.getElementById('revenueDifference');

        // 根據差額的值更新文字和樣式
        if (revenueDifference > 0) {
            revenueDiffSpan.textContent = `增加 \${revenueDifference}`;
            revenueDiffSpan.style.color = 'green'; // 增加的情況設置為綠色
        } else if (revenueDifference < 0) {
            revenueDiffSpan.textContent = `減少 \${Math.abs(revenueDifference)}`; // 顯示為正數
            revenueDiffSpan.style.color = 'red'; // 減少的情況設置為紅色
        } else {
            revenueDiffSpan.textContent = '不變';
            revenueDiffSpan.style.color = 'gray'; // 未變的情況設置為灰色
        }
    });

    document.addEventListener('DOMContentLoaded', async function () {
        // Fetch 每日訂單數量和收入趨勢
        const  dailyOrdersRes = await fetchData('/api/statistics/daily-orders');

        const dailyOrdersData =  dailyOrdersRes.data;
        const orders = dailyOrdersData.map(day => day.orderCount);
        const revenue = dailyOrdersData.map(day => day.totalRevenue);

        if (orders) {
            const today = orders[6];
            const yesterday = orders[5];

            document.getElementById('todayOrders').textContent = today;

            // 計算增長百分比
            let growth = 0;
            let growthText = ''; // 儲存增長或減少的文本
            if (yesterday > 0) {
                growth = ((today - yesterday) / yesterday) * 100; // 計算增長百分比
                if (growth < 0) {
                    growthText = `比昨日減少 \${Math.abs(growth).toFixed(0)}%`; // 減少
                } else {
                    growthText = `比昨日增長 \${growth.toFixed(0)}%`; // 增加
                }
            } else {
                growthText = '昨日無訂單數據'; // 昨日無訂單的情況
            }
            document.getElementById('growthPercentage').textContent = growthText;
        }

        // 使用自定義的標籤生成方法
        const daysLabels = getLastNDaysLabels(7);  // 生成「今天」到「6天前」的標籤

        Highcharts.chart('monthlyOrdersChart', {
            chart: { type: 'line' },
            title: { text: '每日訂單量和收入趨勢' },
            xAxis: { categories: daysLabels },  // 使用「今天」到「6天前」的標籤
            yAxis: [{ title: { text: '訂單量' } }, { title: { text: '收入 (NT$)', opposite: true } }],
            series: [{
                name: '訂單量',
                data: orders
            }, {
                name: '收入',
                data: revenue,
                yAxis: 1
            }]
        });

        // Fetch 客戶性別數據
        const customerSourceData = await fetchData('/api/statistics/customer-source');
        Highcharts.chart('customerSourceChart', {
            chart: { type: 'pie' },
            title: { text: '客戶性別比例' },
            series: [{
                name: '客戶',
                colorByPoint: true,
                data: customerSourceData.map(source => ({
                    name: source.name,
                    y: source.percentage
                }))
            }]
        });

        // Fetch 房型銷售數據
        const roomTypeSalesDataRes = await fetchData('/api/statistics/room-type-sales');
        const roomTypeSalesData = roomTypeSalesDataRes.data;
        Highcharts.chart('roomTypeSalesChart', {
            chart: { type: 'column' },
            title: { text: '房型銷售情況' },
            xAxis: { categories: roomTypeSalesData.map(room => room.roomType) },
            yAxis: { title: { text: '銷售量' } },
            series: [{
                name: '銷售量',
                data: roomTypeSalesData.map(room => room.sales)
            }]
        });
    });
</script>
