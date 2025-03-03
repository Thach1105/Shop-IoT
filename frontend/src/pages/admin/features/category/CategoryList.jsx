import { faPenToSquare, faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { getAllCategories } from "../../../../services/apiCategory";
import { Link } from "react-router-dom";

const getStatusClass = (status) => {
  switch (status) {
    case "Published":
      return "bg-green-100 text-green-700";
    case "Hidden":
      return "bg-red-100 text-red-700";
    default:
      return "";
  }
};

function buildId(id) {
  const k = 4 - id.toString().length;
  return "#" + "0".repeat(k) + id.toString();
}

function CategoryList() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    async function fetchCategories() {
      try {
        const res = await getAllCategories();
        const { data } = res;
        console.log(data);
        setCategories(data?.content);
      } catch (error) {
        console.log(error);
      }
    }

    fetchCategories();
  }, []);

  return (
    <div className="container mx-auto p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Quản lý danh mục sản phẩm</h1>
        <Link
          to={"/admin/category/create"}
          className="bg-purple-500 text-white px-4 py-2 rounded flex items-center"
        >
          <FontAwesomeIcon icon={faPlus} className="mr-2" /> Thêm danh mục mới
        </Link>
      </div>
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                ID
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Mã danh mục mẹ
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Tên danh mục
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Trạng thái
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Chỉnh sửa
              </th>
            </tr>
          </thead>
          <tbody>
            {categories.map((category, index) => (
              <tr key={index}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <Link to={`/admin/category/edit/${category.id}`}>
                    <p className="text-gray-900 whitespace-no-wrap hover:text-yellow-500">
                      {buildId(category.id)}
                    </p>
                  </Link>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  {category?.parent_id ? buildId(category.parent_id) : ""}
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <Link to={`/admin/category/edit/${category.id}`}>
                    <p className="text-gray-900 whitespace-no-wrap hover:text-yellow-500">
                      {category.name}
                    </p>
                  </Link>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <span
                    className={`relative inline-block px-3 py-1 font-semibold leading-tight ${getStatusClass(
                      category.enabled ? "Published" : "Hidden"
                    )}`}
                  >
                    <span
                      aria-hidden="true"
                      className="absolute inset-0 opacity-50 rounded-full"
                    ></span>
                    <span className="relative">
                      {category.enabled ? "Hiển thị" : "Ẩn"}
                    </span>
                  </span>
                </td>
                <td className="px-5 py-5 border-b text-center border-gray-200 bg-white text-sm">
                  <Link
                    to={`/admin/category/edit/${category.id}`}
                    className="text-green-500 hover:text-green-700 mr-2"
                    //onClick={navigate(`/admin/category/edit/${category.id}`)}
                  >
                    <FontAwesomeIcon icon={faPenToSquare} size="lg" />
                  </Link>
                  {/* <button className="text-red-500 hover:text-red-700">
                    <FontAwesomeIcon icon={faTrash} size="lg" />
                  </button> */}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default CategoryList;
