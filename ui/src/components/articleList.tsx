import { Box, List, ListItem, ListItemText, Typography, CircularProgress } from "@mui/material";
import { useGetArticlesQuery } from "../features/api";
import type { Article } from "../types/article";

export default function ArticleList() {
    const { data: articles, isLoading, isError } = useGetArticlesQuery();

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
                <Typography color="error">Failed to load articles.</Typography>
            </Box>
        );
    }

    const truncateText = (text: string, maxLength: number) =>
        text.length > maxLength ? `${text.slice(0, maxLength)}...` : text;

    return (
        <Box
            sx={{
                width: "15%",
                height: "97%",
                padding: 2,
                overflowY: "auto",
                textAlign: "left",
            }}
        >
            <Typography variant="h6" sx={{ marginBottom: 2 }}>
                Recent Articles
            </Typography>
            <List>
                {articles?.map((article: Article, index: number) => (
                    <ListItem key={index}>
                        <ListItemText
                            primary={truncateText(article.article, 27)}
                            secondary={`Publisher: ${article.publisher}, Score: ${article.score}, Symbol: ${article.symbol}, Created: ${new Date(article.created).toLocaleString()}`}
                            sx={{
                                backgroundColor: article.score >= 0 ? "lightgreen" : "lightcoral",
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