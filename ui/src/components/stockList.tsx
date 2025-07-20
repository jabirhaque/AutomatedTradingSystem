import { Box, List, ListItem, ListItemText } from "@mui/material";

export default function StockList() {
    const stocks = [
        { symbol: "AAPL", name: "Apple Inc.", price: 175.64 },
        { symbol: "GOOGL", name: "Alphabet Inc.", price: 2801.12 },
        { symbol: "AMZN", name: "Amazon.com Inc.", price: 3450.96 },
        { symbol: "MSFT", name: "Microsoft Corp.", price: 299.35 },
        { symbol: "TSLA", name: "Tesla Inc.", price: 850.12 },
        { symbol: "NFLX", name: "Netflix Inc.", price: 645.23 },
        { symbol: "FB", name: "Meta Platforms Inc.", price: 325.45 },
        { symbol: "NVDA", name: "NVIDIA Corp.", price: 220.67 },
        { symbol: "UBER", name: "Uber Technologies", price: 45.67 },
        { symbol: "LYFT", name: "Lyft Inc.", price: 35.12 }
    ];

    return (
        <Box
            sx={{
                width: "15%",
                height: "97%", // Fixed height
                padding: 2,
                overflowY: "auto", // Enable vertical scrolling

                direction: "rtl", // Move scrollbar to the left
                textAlign: "left", // Ensure text alignment remains correct
            }}
        >
            <List>
                {stocks.map((stock) => (
                    <ListItem key={stock.symbol}>
                        <ListItemText
                            primary={`${stock.symbol} - ${stock.name}`}
                            secondary={`Price: $${stock.price.toFixed(2)}`}
                        />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
}