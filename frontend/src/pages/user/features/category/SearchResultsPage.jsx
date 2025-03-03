import { Link, useSearchParams } from "react-router-dom";
import PriceRangeFilter from "./PriceRangeFilter";
import SortTab from "./SortTab";
import ProductList from "./ProductList";
import { Pagination } from "@mui/material";
import { useEffect, useState } from "react";
import { searchForCustomer } from "../../../../services/apiProduct";
import { formatNumber } from "../../../../utils/format";

function SearchResultsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const keyword = searchParams.get("keyword");

  const [products, setProducts] = useState([]);
  const [pageDetail, setPageDetail] = useState();
  const [sortField, setSortField] = useState(searchParams.get("sort") || "");
  const [page, setPage] = useState(Number(searchParams.get("page")) || 1);
  const [minPrice, setMinPrice] = useState(
    Number(searchParams.get("price")?.split("-")[0]) || 0
  );
  const [maxPrice, setMaxPrice] = useState(
    Number(searchParams.get("price")?.split("-")[1]) || 100000000
  );

  useEffect(() => {
    async function searchProduct() {
      const params = {
        size: 9,
        page,
        keyword,
        minPrice,
        maxPrice,
        sortField,
      };
      const response = await searchForCustomer(params);
      const { data } = response;
      setProducts(data?.content);
      setPageDetail(data?.pageDetails);
    }

    searchProduct();
  }, [page, keyword, minPrice, maxPrice, sortField]);

  const handleChangePriceRange = (min, max) => {
    setMaxPrice(max);
    setMinPrice(min);
    setPage(1);

    setSearchParams((prevParams) => {
      const newParams = new URLSearchParams(prevParams);
      newParams.set("price", `${min}-${max}`);
      newParams.set("page", "1");
      return newParams;
    });
  };

  const handleChangeSortField = (sort) => {
    setSortField(sort);
    setPage(1);
    setSearchParams((prevParams) => {
      const newParams = new URLSearchParams(prevParams);
      sort ? newParams.set("sort", sort) : newParams.delete("sort");
      newParams.set("page", "1");
      return newParams;
    });
  };

  const handleUpdateParams = (param, value) => {
    setSearchParams((prevParams) => {
      const newParams = new URLSearchParams(prevParams);
      if (value) {
        newParams.set(param, value);
      } else {
        newParams.delete(param);
      }
      return newParams;
    });
  };

  const handleChangePage = (event, value) => {
    setPage(value);
    handleUpdateParams("page", value);
  };

  return (
    <div className="container mx-auto p-4 bg-stone-100/20">
      {/* Breadcrumb */}
      <nav className="text-sm text-gray-600 mb-4">
        <Link className="hover:underline p-1" to={"/"}>
          Trang chủ
        </Link>
        <span className="mx-2">&gt;</span>
        <span>Tìm kiếm</span>
      </nav>

      {/* Main content */}
      <div className="flex gap-6">
        <aside className="w-1/4 bg-white p-4 shadow-md">
          <PriceRangeFilter
            handleChangePriceRange={handleChangePriceRange}
            minPrice={minPrice}
            maxPrice={maxPrice}
          />
        </aside>

        {/* Products grid */}

        <main className="w-3/4 ml-4">
          <h1 className="text-3xl font-bold mb-4">{`Kết quả tìm kiếm cho "${keyword}"`}</h1>
          <SortTab
            handleChangeSortField={handleChangeSortField}
            sortField={sortField}
          />
          {!(minPrice === 0 && maxPrice === 100000000) && (
            <div className="text-stone-600 px-2 bg-gray-200 w-fit pb-1 rounded-md">
              {`Từ ${formatNumber(minPrice)} - ${formatNumber(maxPrice)}`}
              <span
                onClick={() => {
                  setMaxPrice(100000000);
                  setMinPrice(0);
                  handleUpdateParams("price", null);
                }}
                className="text-xl ml-1 cursor-pointer"
              >
                &times;
              </span>
            </div>
          )}

          <ProductList products={products} />
        </main>
      </div>
      {pageDetail && pageDetail.totalPages > 1 && (
        <div className="mt-4 flex justify-end">
          <Pagination
            count={pageDetail.totalPages}
            page={pageDetail.page}
            onChange={handleChangePage}
            color="primary"
          />
        </div>
      )}
    </div>
  );
}

export default SearchResultsPage;
