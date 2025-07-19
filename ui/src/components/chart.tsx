import { useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { useGetPortfolioDataWeekQuery, useGetPortfolioDataMonthQuery, useGetPortfolioDataYearQuery } from "../features/api.ts";
import { CircularProgress, Button, Box, Stack } from "@mui/material";

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
    const portfolioData = timeRange === '1W' ? weekData : timeRange === '1M' ? monthData : timeRange === '1Y' ? yearData : null;

    if (isLoading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <CircularProgress size={100} />
            </div>
        );
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
            <ReactECharts
                option={option}
                notMerge={true}
                lazyUpdate={true}
                theme={"theme_name"}
                style={{ width: '100%', height: 'calc(100vh - 100px)' }}
            />
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    marginTop: 3
                }}
            >
                <Stack spacing={2} direction="row">
                    <Button
                        variant="outlined"
                        onClick={() => setTimeRange('1W')}
                        color={timeRange === '1W' ? 'primary' : 'inherit'}
                    >
                        1W
                    </Button>
                    <Button
                        variant="outlined"
                        onClick={() => setTimeRange('1M')}
                        color={timeRange === '1M' ? 'primary' : 'inherit'}
                    >
                        1M
                    </Button>
                    <Button
                        variant="outlined"
                        onClick={() => setTimeRange('1Y')}
                        color={timeRange === '1Y' ? 'primary' : 'inherit'}
                    >
                        1Y
                    </Button>
                </Stack>
            </Box>
        </div>
    );
}