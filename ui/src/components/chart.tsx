import { useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { useGetPortfolioDataWeekQuery, useGetPortfolioDataMonthQuery, useGetPortfolioDataYearQuery } from "../features/api.ts";

const upColor = '#00da3c';
const upBorderColor = '#008F28';
const downColor = '#ec0000';
const downBorderColor = '#8A0000';

export default function Chart() {
    const [timeRange, setTimeRange] = useState<'1W' | '1M' | '1Y'>('1W');

    const { data: weekData, isLoading: isWeekLoading, error: weekError } = useGetPortfolioDataWeekQuery();
    const { data: monthData, isLoading: isMonthLoading, error: monthError } = useGetPortfolioDataMonthQuery();
    const { data: yearData, isLoading: isYearLoading, error: yearError } = useGetPortfolioDataYearQuery();

    const isLoading = timeRange === '1W' ? isWeekLoading : timeRange === '1M' ? isMonthLoading : isYearLoading;
    const error = timeRange === '1W' ? weekError : timeRange === '1M' ? monthError : yearError;
    const portfolioData = timeRange === '1W' ? weekData : timeRange === '1M' ? monthData : yearData;

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (error) {
        console.error('Error fetching portfolio data:', error);
        return <div>Error loading data</div>;
    }

    if (!portfolioData || portfolioData.length === 0) {
        return <div>No data available</div>;
    }

    const data = portfolioData.map(item => [
        item.timestamp,
        item.open,
        item.close,
        item.low,
        item.high,
        item.volume,
        item.sign
    ]);

    const option = {
        dataset: {
            source: data
        },
        title: {
            text: 'Automated Trading System Portfolio Performance'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            },
            formatter: function (params: any) {
                const [data] = params;
                return `
                ${data.axisValue}<br />
                Open: ${data.data[1]}<br />
                Close: ${data.data[2]}
            `;
            }
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: false
                }
            }
        },
        grid: [
            {
                left: '10%',
                right: '10%',
                bottom: 200
            },
            {
                left: '10%',
                right: '10%',
                height: 80,
                bottom: 80
            }
        ],
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                axisLine: { onZero: false },
                splitLine: { show: false },
                min: 'dataMin',
                max: 'dataMax'
            },
            {
                type: 'category',
                gridIndex: 1,
                boundaryGap: false,
                axisLine: { onZero: false },
                axisTick: { show: false },
                splitLine: { show: false },
                axisLabel: { show: false },
                min: 'dataMin',
                max: 'dataMax'
            }
        ],
        yAxis: [
            {
                scale: true,
                splitArea: {
                    show: true
                }
            },
            {
                scale: true,
                gridIndex: 1,
                splitNumber: 2,
                axisLabel: { show: false },
                axisLine: { show: false },
                axisTick: { show: false },
                splitLine: { show: false }
            }
        ],
        dataZoom: [
            {
                type: 'inside',
                xAxisIndex: [0, 1],
                start: 10,
                end: 100
            },
            {
                show: true,
                xAxisIndex: [0, 1],
                type: 'slider',
                bottom: 10,
                start: 10,
                end: 100
            }
        ],
        visualMap: {
            show: false,
            seriesIndex: 1,
            dimension: 6,
            pieces: [
                {
                    value: 1,
                    color: upColor
                },
                {
                    value: -1,
                    color: downColor
                }
            ]
        },
        series: [
            {
                type: 'candlestick',
                itemStyle: {
                    color: upColor,
                    color0: downColor,
                    borderColor: upBorderColor,
                    borderColor0: downBorderColor
                },
                encode: {
                    x: 0,
                    y: [1, 4, 3, 2]
                }
            },
            {
                name: 'Volume',
                type: 'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                itemStyle: {
                    color: '#7fbe9e'
                },
                large: true,
                encode: {
                    x: 0,
                    y: 5
                }
            }
        ]
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '20px' }}>
                <button
                    onClick={() => setTimeRange('1W')}
                    style={{
                        padding: '10px 20px',
                        fontSize: '16px',
                        margin: '0 10px',
                        borderRadius: '8px',
                        border: '1px solid #ccc',
                        backgroundColor: timeRange === '1W' ? '#007BFF' : '#f8f9fa',
                        color: timeRange === '1W' ? '#fff' : '#000',
                        cursor: 'pointer',
                        transition: 'all 0.3s ease',
                    }}
                    onMouseOver={(e) => (e.currentTarget.style.backgroundColor = '#0056b3')}
                    onMouseOut={(e) =>
                        (e.currentTarget.style.backgroundColor = timeRange === '1W' ? '#007BFF' : '#f8f9fa')
                    }
                >
                    1W
                </button>
                <button
                    onClick={() => setTimeRange('1M')}
                    style={{
                        padding: '10px 20px',
                        fontSize: '16px',
                        margin: '0 10px',
                        borderRadius: '8px',
                        border: '1px solid #ccc',
                        backgroundColor: timeRange === '1M' ? '#007BFF' : '#f8f9fa',
                        color: timeRange === '1M' ? '#fff' : '#000',
                        cursor: 'pointer',
                        transition: 'all 0.3s ease',
                    }}
                    onMouseOver={(e) => (e.currentTarget.style.backgroundColor = '#0056b3')}
                    onMouseOut={(e) =>
                        (e.currentTarget.style.backgroundColor = timeRange === '1M' ? '#007BFF' : '#f8f9fa')
                    }
                >
                    1M
                </button>
                <button
                    onClick={() => setTimeRange('1Y')}
                    style={{
                        padding: '10px 20px',
                        fontSize: '16px',
                        margin: '0 10px',
                        borderRadius: '8px',
                        border: '1px solid #ccc',
                        backgroundColor: timeRange === '1Y' ? '#007BFF' : '#f8f9fa',
                        color: timeRange === '1Y' ? '#fff' : '#000',
                        cursor: 'pointer',
                        transition: 'all 0.3s ease',
                    }}
                    onMouseOver={(e) => (e.currentTarget.style.backgroundColor = '#0056b3')}
                    onMouseOut={(e) =>
                        (e.currentTarget.style.backgroundColor = timeRange === '1Y' ? '#007BFF' : '#f8f9fa')
                    }
                >
                    1Y
                </button>
            </div>
            <ReactECharts
                option={option}
                notMerge={true}
                lazyUpdate={true}
                theme={"theme_name"}
                style={{ width: '100%', height: 'calc(100vh - 100px)' }}
            />
        </div>
    );
}