import { Box, List, ListItem, ListItemText } from "@mui/material";

export default function ArticleList() {
    const articles = [
        { title: "Tesla unveils new electric truck", author: "Elon Musk", date: "2023-06-25" },
        { title: "Meta announces AI advancements", author: "Mark Zuckerberg", date: "2023-05-30" },
        { title: "Netflix expands into gaming", author: "Sarah Connor", date: "2023-04-15" },
        { title: "NVIDIA reveals next-gen GPUs", author: "Jensen Huang", date: "2023-03-10" },
        { title: "Twitter introduces subscription model", author: "Jack Dorsey", date: "2023-02-20" },
        { title: "SpaceX launches new satellite", author: "Chris Hadfield", date: "2023-01-05" },
        { title: "Apple announces AR glasses", author: "Tim Cook", date: "2022-12-15" },
        { title: "Google AI beats human in chess", author: "Sundar Pichai", date: "2022-11-10" },
        { title: "Amazon opens cashier-less stores", author: "Andy Jassy", date: "2022-10-05" },
    ];

    return (
        <Box
            sx={{
                width: "15%",
                height: "97%", // Fixed height
                padding: 2,
                overflowY: "auto", // Enable vertical scrolling
                textAlign: "left", // Ensure text alignment remains correct
            }}
        >
            <List>
                {articles.map((article, index) => (
                    <ListItem key={index}>
                        <ListItemText
                            primary={article.title}
                            secondary={`By ${article.author} - ${article.date}`}
                        />
                    </ListItem>
                ))}
            </List>
        </Box>
    );
}