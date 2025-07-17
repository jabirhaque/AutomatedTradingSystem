import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

export const api = createApi({
    reducerPath: "api",
    baseQuery: fetchBaseQuery({ baseUrl: "https://dummyjson.com/" }), //TODO: Change to your API base URL
    endpoints: (builder) => ({
        getAllProducts: builder.query({ //TODO: Change to your API endpoint
            query: () => "products",
        }),
        getProduct: builder.query({
            query: (product) => `products/search?q=${product}`,
        }),
    }),
});

export const { useGetAllProductsQuery, useGetProductQuery } = api;