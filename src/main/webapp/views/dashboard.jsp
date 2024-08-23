<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<script src="https://code.highcharts.com/highcharts.js"></script>

<div class="row" style="--bs-gutter-x: 0.75rem;">
    <!-- 今日訂單卡片 -->
    <div class="col-md-4">
        <a href="todayOrders.jsp" class="text-decoration-none">
            <div class="card custom-card bg-primary-light mb-3">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-cart-check"></i>今日訂單</h5>
                    <p class="card-text">5</p>
                </div>
            </div>
        </a>
    </div>

    <!-- 本月收入卡片 -->
    <div class="col-md-4">
        <a href="monthlyRevenue.jsp" class="text-decoration-none">
            <div class="card custom-card bg-success-light mb-3">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-currency-dollar"></i>本月收入</h5>
                    <p class="card-text">NT$ 50,000</p>
                </div>
            </div>
        </a>
    </div>

    <!-- 新增客戶卡片 -->
    <div class="col-md-4">
        <a href="newCustomers.jsp" class="text-decoration-none">
            <div class="card custom-card bg-info-light mb-3">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-people"></i>新增客戶</h5>
                    <p class="card-text">10</p>
                </div>
            </div>
        </a>
    </div>

    <!-- 商家待審核通知卡片 -->
    <div class="col-md-4">
        <a href="pendingMerchants.jsp" class="text-decoration-none">
            <div class="card custom-card bg-warning-light mb-3">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-file-earmark-person"></i>商家待審核</h5>
                    <p class="card-text">3</p>
                </div>
            </div>
        </a>
    </div>

    <!-- 未讀訊息卡片 -->
    <div class="col-md-4">
        <a href="unreadMessages.jsp" class="text-decoration-none">
            <div class="card custom-card bg-secondary-light mb-3">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-envelope"></i>未讀訊息</h5>
                    <p class="card-text">8</p>
                </div>
            </div>
        </a>
    </div>

    <!-- 訂單爭議卡片 -->
    <div class="col-md-4">
        <a href="orderDisputes.jsp" class="text-decoration-none">
            <div class="card custom-card bg-danger-light mb-3">
                <div class="card-body text-center">
                    <h5 class="card-title"><i class="bi bi-exclamation-triangle"></i>訂單爭議</h5>
                    <p class="card-text">2</p>
                </div>
            </div>
        </a>
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
    const monthlyOrdersChart = () => {
        Highcharts.chart('monthlyOrdersChart', {
            chart: {
                type: 'line'
            },
            title: {
                text: '月度訂單量和收入趨勢'
            },
            xAxis: {
                categories: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
            },
            yAxis: [{
                title: {
                    text: '訂單量'
                }
            }, {
                title: {
                    text: '收入 (NT$)',
                    opposite: true
                }
            }],
            series: [{
                name: '訂單量',
                data: [50, 75, 120, 200, 180, 220, 250, 300, 280, 320, 350, 400]
            }, {
                name: '收入',
                data: [50000, 60000, 80000, 120000, 150000, 170000, 200000, 220000, 250000, 270000, 300000, 320000],
                yAxis: 1
            }]
        });

    }

    const customerSourceChart = () => {
        Highcharts.chart('customerSourceChart', {
            chart: {
                type: 'pie'
            },
            title: {
                text: '客戶來源比例'
            },
            series: [{
                name: '客戶',
                colorByPoint: true,
                data: [{
                    name: 'Google廣告',
                    y: 45
                }, {
                    name: '社交媒體',
                    y: 30
                }, {
                    name: '推薦',
                    y: 15
                }, {
                    name: '直接訪問',
                    y: 10
                }]
            }]
        });
    }

    const roomTypeSalesChart = () => {
        Highcharts.chart('roomTypeSalesChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: '房型銷售情況'
            },
            xAxis: {
                categories: ['單人房', '雙人房', '豪華房', '家庭房']
            },
            yAxis: {
                title: {
                    text: '銷售量'
                }
            },
            series: [{
                name: '銷售量',
                data: [120, 180, 240, 90]
            }]
        });
    }



    document.addEventListener('DOMContentLoaded', function () {
        monthlyOrdersChart();
        customerSourceChart();
        roomTypeSalesChart();
    });
</script>
