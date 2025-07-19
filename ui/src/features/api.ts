import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {PortfolioData} from "../types/portfolioData.ts";

export const api = createApi({
    reducerPath: "api",
    baseQuery: fetchBaseQuery({ baseUrl: "http://localhost:8080/api/" }),
    endpoints: (builder) => ({
        getPortfolioDataWeek: builder.query<PortfolioData[], void>({
            query: () => "portfolio/week",
        }),
        getPortfolioDataMonth: builder.query<PortfolioData[], void>({
            query: () => "portfolio/month",
        }),
        getPortfolioDataYear: builder.query<PortfolioData[], void>({
            query: () => "portfolio/year",
        }),
    }),
});

export const { useGetPortfolioDataWeekQuery, useGetPortfolioDataMonthQuery, useGetPortfolioDataYearQuery } = api;