# Automated Trading System

This project is an **Automated Trading System** designed to analyze market news and execute trades based on sentiment analysis.

## Features

- **Market News Analysis**: Periodically fetches articles from a market news API (Polygon API).
- **Sentiment Scoring**: Uses **Stanford Core NLP** to assign a semantic score to each article.
- **Automated Trading**:
    - Positive sentiment → **Buy transaction**.
    - Negative sentiment → **Short transaction**.
- **Trade Scheduling**: Trades are exited after a fixed period of time.
- **Reporter Reliance Score**:
    - Profitable trades increase the reporter's reliance score.
    - Reliance scores influence trade pricing for future articles, creating a continuous feedback loop.

## Technology Stack

- **Backend**: Java Spring Framework
- **Database**: MongoDB
- **Frontend**: ReactJS
- **Market News API**: Polygon API
- **Trading API**: Alpaca API
- **Natural Language Processing**: Stanford Core NLP

This system integrates advanced sentiment analysis and trading strategies to automate decision-making in financial markets.