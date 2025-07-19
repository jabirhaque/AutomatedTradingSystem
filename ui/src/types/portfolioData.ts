export type PortfolioData = {
    timestamp: string; // ISO string representation of LocalDateTime
    open: number;
    high: number;
    low: number;
    close: number;
    volume: number;
    sign: number;
};