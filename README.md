# Automated Trading System

This project is an **Automated Trading System** designed to analyse market news and execute trades based on sentiment analysis

## Features
- **Market News Analysis**: Periodically fetches relevant market news from [**Polygon API**](https://polygon.io/)
- **Sentiment Scoring**: Uses [**Stanford CoreNLP**](https://stanfordnlp.github.io/CoreNLP/) to assign a semantic score to each article
- **Automated Trading**:
    - Trades are executed through the [**Alpaca API**](https://alpaca.markets/)
    - Positive sentiment → **Buy transaction**
    - Negative sentiment → **Short transaction**
- **Trade Scheduling**: Trades are scheduled to be exited after a fixed duration of market exposure

## Planned Features

- **User Interface**: A web-based dashboard to visualize trades, performance and market news
- **Support for multiple news sources**: Integrate additional news APIs for broader market coverage
- **Reporter Reliance**: Implement trade performance tracking for each reporter to weight sources and dynamically size positions with a self
  adjusting feedback loop

## Technology Stack
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MongoDB](https://img.shields.io/badge/-MongoDB-13aa52?style=for-the-badge&logo=mongodb&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![ReactJS](https://img.shields.io/badge/-ReactJs-61DAFB?logo=react&logoColor=white&style=for-the-badge)
![Docker](https://img.shields.io/badge/docker-257bd6?style=for-the-badge&logo=docker&logoColor=white)
![img.png](img.png)
