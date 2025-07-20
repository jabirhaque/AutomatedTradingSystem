import { Box } from "@mui/material";
import Chart from "./components/chart";
import StockList from "./components/stockList";
import ArticleList from "./components/articleList.tsx";

export default function App() {
    return (
        <Box
            sx={{
                display: "flex",
                flexDirection: "row",
                height: "97vh",
            }}
        >
            <StockList />
            <Box sx={{ flex: 1 }}>
                <Chart />
            </Box>
            <ArticleList />
        </Box>
    );
}