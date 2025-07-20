import { Box, List, ListItem, ListItemText, CircularProgress, Typography } from "@mui/material";
import { useGetPositionsQuery } from "../features/api";

export default function StockList() {
    const { data: positions, isLoading, isError } = useGetPositionsQuery();

    if (isLoading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                <CircularProgress />
            </Box>
        );
    }

    if (isError) {
        return (
            <Box sx={{ padding: 2 }}>
                <Typography color="error">Failed to load positions.</Typography>
            </Box>
        );
    }

    return (
        <Box
            sx={{
                width: "15%",
                height: "97%",
                padding: 2,
                overflowY: "auto",
                direction: "rtl",
                textAlign: "left",
            }}
        >
            <Typography variant="h6" sx={{ marginBottom: 2 }}>
                Current Positions
            </Typography>
            <List>
                {positions?.map((position) => (
                    <ListItem key={position.symbol}>
                        <ListItemText
                            primary={`${position.symbol} (${position.exchange})`}
                            secondary={`Qty: ${Number(position.qty).toFixed(2)}, Market Value: $${Number(position.marketValue).toFixed(2)}, Unrealized P/L: $${Number(position.unrealizedPl).toFixed(2)}`}
                            sx={{
                                backgroundColor: Number(position.unrealizedPl) >= 0 ? "lightgreen" : "lightcoral",
                                borderRadius: "4px",
                                padding: "8px",
                            }}
                        />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
}