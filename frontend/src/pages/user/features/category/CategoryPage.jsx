import React, { useEffect, useState } from "react";
import {
  Link,
  useLocation,
  useParams,
  useSearchParams,
} from "react-router-dom";
import {
  getCategoryById,
  getCategoryBySlug,
} from "../../../../services/apiCategory";
import { Pagination } from "@mui/material";
import { getProductByCategory } from "../../../../services/apiProduct";
import ProductList from "./ProductList";
import SortTab from "./SortTab";
import PriceRangeFilter from "./PriceRangeFilter";
import { formatNumber } from "../../../../utils/format";

async function buildBreadcrumb(id, breadCrumbList = []) {
  const response = await getCategoryById(id);

  const { data } = response;
  const category = data?.content;

  breadCrumbList.unshift(category);
  if (category?.parent_id) {
    await buildBreadcrumb(category.parent_id, breadCrumbList);
  }

  return breadCrumbList;
}

function CategoryPage() {
  const { slug } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const location = useLocation();
  const { state } = location;

  const [category, setCategory] = useState();
  const [breadCrumbs, setBreadCrumbs] = useState([]);
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(Number(searchParams.get("page")) || 1);
  const [pageDetail, setPageDetail] = useState();
  const [sortField, setSortField] = useState(searchParams.get("sort") || "");
  const [minPrice, setMinPrice] = useState(
    Number(searchParams.get("price")?.split("-")[0]) || 0
  );
  const [maxPrice, setMaxPrice] = useState(
    Number(searchParams.get("price")?.split("-")[1]) || 100000000
  );

  useEffect(() => {
    if (!state) {
      async function fetChCategory() {
        const response = await getCategoryBySlug(slug);
        const { data } = response;
        setCategory(data?.content);
      }
      fetChCategory();
    } else {
      setCategory(state);
    }
  }, [slug, state]);

  useEffect(() => {
    async function callBreadCrumbFunc() {
      if (category?.id) {
        const breadCrumb = await buildBreadcrumb(category.id);
        setBreadCrumbs(breadCrumb);
      }
    }

    callBreadCrumbFunc();
  }, [category]);

  useEffect(() => {
    if (category) {
      async function fetchProductByCategory() {
        const params = {
          categoryId: category.id,
          page,
          size: 9,
          maxPrice,
          minPrice,
          sortField,
        };

        const response = await getProductByCategory(params);
        const { data } = response;
        setProducts(data?.content);
        setPageDetail(data?.pageDetails);
      }

      fetchProductByCategory();
    }
  }, [category, page, maxPrice, minPrice, sortField]);

  const handleChangePage = (event, value) => {
    setPage(value);
    handleUpdateParams("page", value);
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

  return (
    category && (
      <div className="container mx-auto p-4 bg-stone-100/20">
        <nav className="text-sm text-gray-600 mb-4">
          <Link to={"/"} className="p-1 hover:underline">
            Trang chủ
          </Link>
          {breadCrumbs.map((cate, index) => (
            <React.Fragment key={index}>
              <span> &gt; </span>
              <Link
                to={`/danh-muc/${cate.slug}`}
                className="hover:underline"
                state={cate}
              >
                {cate.name}
              </Link>
            </React.Fragment>
          ))}
        </nav>
        <div className="flex">
          <aside className="w-1/4 bg-white p-4 shadow-md">
            <h2 className="font-bold text-lg mb-4">DANH MỤC</h2>
            <ul className="space-y-2 text-gray-700 list-disc px-4">
              <li className="list-none">
                <Link
                  to={`/danh-muc/${category.slug}`}
                  className="hover:underline"
                  state={category}
                >
                  {category.name}
                </Link>
              </li>
              {category.children.map((child, index) => (
                <li className="pl-3" key={index}>
                  <Link
                    to={`/danh-muc/${child.slug}`}
                    className="hover:underline"
                    state={child}
                  >
                    {child.name}
                  </Link>
                </li>
              ))}
            </ul>

            <PriceRangeFilter
              handleChangePriceRange={handleChangePriceRange}
              minPrice={minPrice}
              maxPrice={maxPrice}
            />
          </aside>
          <main className="w-3/4 ml-4">
            <h1 className="text-2xl font-bold mb-4">{category.name}</h1>
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
    )
  );
}

export default CategoryPage;
