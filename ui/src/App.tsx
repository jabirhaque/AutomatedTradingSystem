import {useGetAllProductsQuery} from "./features/api.ts";

export default function App() {
    const {data: allProductsData, isLoading} = useGetAllProductsQuery({});
    if (isLoading) {
        return(
        <>
            <p>Loading...</p>
        </>
    )}

    console.log(allProductsData);

    return (
    <>
        <p>Hello World</p>
    </>
  )
}