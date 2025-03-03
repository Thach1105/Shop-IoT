import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPenToSquare } from "@fortawesome/free-regular-svg-icons";
import { faEllipsisVertical, faPlus } from "@fortawesome/free-solid-svg-icons";
import { Link } from "react-router-dom";
import React, { useEffect, useState } from "react";
import { searchProduct } from "../../../../services/apiProduct";
import { formatNumber } from "../../../../utils/format";
import { getCategoriesTree } from "../../../../services/apiCategory";
import TablePagination from "@mui/material/TablePagination";

function categorySelect(categories, depth = 0) {
  return categories.map((category, index) => (
    <React.Fragment key={index}>
      <option className="text-xs" key={index} value={category.id}>
        {"--".repeat(depth) + " " + category.name}
      </option>
      {category.children && categorySelect(category.children, depth + 1)}
    </React.Fragment>
  ));
}

function ProductList() {
  const [productList, setProductList] = useState([]);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [pageDetail, setPageDetail] = useState(null);
  const [searchValue, setSearchValue] = useState("");
  const [active, setActive] = useState(-1);
  const [inStock, setInStock] = useState(-1);
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState(0);

  useEffect(() => {
    async function fetchCategories() {
      try {
        const response = await getCategoriesTree();
        const { data } = response;
        setCategories(data?.content);
      } catch (error) {
        console.log(error);
      }
    }

    fetchCategories();
  }, []);

  useEffect(() => {
    async function search() {
      try {
        const params = {
          search: searchValue,
          pageNumber: pageNumber + 1,
          pageSize,
          active,
          inStock,
          category: categoryId,
          sortField: "id_desc",
        };
        console.log(params);
        const response = await searchProduct(params);
        const { data } = response;
        console.log(data);

        setProductList(data?.content);
        setPageDetail(data?.pageDetails);
      } catch (error) {
        console.log(error);
      }
    }

    search();
  }, [searchValue, pageNumber, pageSize, active, inStock, categoryId]);

  const handleChangePage = (event, newPage) => {
    setPageNumber(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setPageSize(parseInt(event.target.value, 10));
    setPageNumber(0);
  };

  return (
    <div className="p-2">
      <div className="bg-white p-4 rounded-lg shadow-md">
        <h1 className="text-2xl font-semibold mb-6">Danh sách sản phẩm</h1>
        <div className="flex justify-between items-center mb-4">
          <div className="flex space-x-4">
            <select
              value={active}
              onChange={(e) => setActive(Number(e.target.value))}
              className="border border-gray-300 rounded-lg p-2"
            >
              <option value={-1}>Trạng thái</option>
              <option value={1}>Hiển thị</option>
              <option value={0}>Ẩn</option>
            </select>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(Number(e.target.value))}
              className="border border-gray-300 rounded-lg p-2 w-60"
            >
              <option value={0}>Danh mục</option>
              {categorySelect(categories)}
            </select>
            <select
              value={inStock}
              onChange={(e) => setInStock(Number(e.target.value))}
              className="border border-gray-300 rounded-lg p-2"
            >
              <option value={-1}>Số lượng</option>
              <option value={1}>Còn hàng</option>
              <option value={0}>Hết hàng</option>
            </select>
          </div>
          <div className="flex space-x-4">
            <input
              type="text"
              placeholder="Tìm kiếm tên sản phẩm, mã SKU"
              className="border border-gray-300 rounded-lg p-2 w-72"
              value={searchValue}
              onChange={(e) => {
                setSearchValue(e.target.value);
                setPageNumber(0);
              }}
            />
            <Link
              to={"/admin/product/add"}
              className="bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-yellow-400/80"
            >
              <FontAwesomeIcon icon={faPlus} /> Thêm sản phẩm
            </Link>
          </div>
        </div>
        {productList.length > 0 ? (
          <>
            <table className="min-w-full bg-white">
              <thead>
                <tr>
                  <th className="py-2 px-4 border-b text-center">
                    TÊN SẢN PHẨM
                  </th>
                  <th className="py-2 px-4 border-b text-center">DANH MỤC</th>
                  <th className="py-2 px-4 border-b text-center">
                    TÌNH TRẠNG KHO
                  </th>
                  <th className="py-2 px-4 border-b text-center">GIÁ BÁN</th>
                  <th className="py-2 px-4 border-b text-center">SỐ LƯỢNG</th>
                  <th className="py-2 px-4 border-b text-center">TRẠNG THÁI</th>
                  <th className="py-2 px-4 border-b text-center">TÙY CHỌN</th>
                </tr>
              </thead>
              <tbody>
                {productList.map((product, index) => (
                  <tr key={index} className="border-b">
                    <td className="py-2 px-4 flex items-center">
                      <img
                        src={product.image_url}
                        alt={product.name}
                        className="w-20 h-20 rounded-full mr-2 border-2"
                      />
                      <div>
                        <div className="font-semibold">{product.name}</div>
                        <div title="SKU" className="text-xs text-blue-600">
                          {`SKU: ${product.sku}`}
                        </div>
                      </div>
                    </td>
                    <td className="py-2 px-4 text-center">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          product.category?.name
                            ? "bg-yellow-100 text-yellow-800"
                            : ""
                        }`}
                      >
                        {product.category?.name}
                      </span>
                    </td>
                    <td className="py-2 px-4 text-center">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          product.inStock
                            ? "bg-blue-100 text-blue-800"
                            : "bg-stone-200 text-stone-800"
                        }`}
                      >
                        <p className="text-xs">
                          {product.inStock ? "Còn hàng" : "Hết hàng"}
                        </p>
                      </span>
                    </td>
                    <td className="py-2 px-4 text-right text-sm right-1">
                      <p className="whitespace-nowrap">
                        {formatNumber(product.cost)} đ
                      </p>
                    </td>
                    <td className="py-2 px-4 text-center">{product.stock}</td>
                    <td className="py-2 px-4 text-center">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          product.active
                            ? "bg-green-100 text-green-800"
                            : "bg-red-100 text-red-800"
                        }`}
                      >
                        {product.active ? "Hiển thị" : "Ẩn"}
                      </span>
                    </td>
                    <td className="py-2 px-4 text-center space-x-2">
                      <Link
                        to={`/admin/product-detail/${product.id}`}
                        title="View Detail"
                        className="text-gray-500 p-2 hover:text-blue-500 hover:text-xl"
                      >
                        <FontAwesomeIcon icon={faEllipsisVertical} size="lg" />
                      </Link>
                      <Link
                        to={`/admin/product/edit/${product.id}`}
                        title="Edit"
                        className="text-gray-500 hover:text-blue-500 hover:text-xl"
                      >
                        <FontAwesomeIcon icon={faPenToSquare} size="lg" />
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        ) : (
          <div className="italic text-center">
            Không tìm thấy danh sách sản phẩm phù hợp
          </div>
        )}
        <TablePagination
          component="div"
          count={pageDetail?.totalElements}
          page={pageNumber}
          onPageChange={handleChangePage}
          rowsPerPage={pageSize}
          onRowsPerPageChange={handleChangeRowsPerPage}
          color="primary"
        />
      </div>
    </div>
  );
}

export default ProductList;
