import { useEffect, useState } from "react";
import TextEditor from "../../../../utils/TextEditor";
import {
  createNewCateogory,
  getCategoriesTree,
} from "../../../../services/apiCategory";
import { useNavigate } from "react-router-dom";

function categorySelect(categories, depth = 0) {
  return categories.map((category, index) => (
    <>
      <option className="text-xs" key={index} value={category.id}>
        {"--".repeat(depth) + " " + category.name}
      </option>
      {category.children && categorySelect(category.children, depth + 1)}
    </>
  ));
}

function CategoryAdd() {
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState("");
  const [slug, setSlug] = useState("");
  const [parentId, setParentId] = useState(0);
  const [enabled, setEnabled] = useState(true);
  const [description, setDescription] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchCategories() {
      try {
        const res = await getCategoriesTree();
        const { data } = res;
        setCategories(data?.content);
      } catch (error) {
        console.log(error);
      }
    }

    fetchCategories();
  }, []);

  async function handleSave() {
    const newCategory = {
      name,
      parent: parentId === 0 ? null : parentId,
      description,
      enabled,
      slug,
    };
    console.log(newCategory);

    try {
      const repsonse = await createNewCateogory(newCategory);
      console.log(repsonse);
      navigate("/admin/categories-list");
    } catch (error) {
      console.log(error);
    }
  }

  return (
    <div className="max-w-full mx-auto bg-white p-8 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Thêm danh mục sản phẩm mới</h1>
        <button
          className="bg-purple-500 text-white px-4 py-2 rounded"
          onClick={handleSave}
        >
          Thêm mới
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Visibility Status */}
        <div className="bg-white p-6 rounded-lg shadow-md col-span-1">
          <>
            <h2 className="text-lg font-semibold mb-4">Trạng thái hiển thị</h2>
            <div className="flex flex-col space-y-2">
              <label className="flex items-center">
                <input
                  type="radio"
                  value={true}
                  name="visibility"
                  className="mr-2"
                  onChange={(e) => setEnabled(e.target.value)}
                  checked={enabled}
                />
                Hiển thị
              </label>
              <label className="flex items-center">
                <input
                  type="radio"
                  name="visibility"
                  value={false}
                  className="mr-2"
                  onChange={(e) => setEnabled(e.target.value)}
                />
                Ẩn
              </label>
            </div>
          </>

          <>
            <div className="my-4">
              <label className="text-lg font-semibold mb-4">Danh mục cha</label>
              <select
                value={parentId}
                onChange={(e) => setParentId(e.target.value)}
                className="w-full p-2 border rounded"
              >
                <option className="text-xs" value={0}>
                  --- Không có ---
                </option>
                {categorySelect(categories)}
              </select>
            </div>
          </>
        </div>

        {/* Basic information */}
        <div className="bg-white p-6 rounded-lg shadow-md col-span-2">
          <h2 className="text-lg font-semibold mb-4">Thông tin cơ bản</h2>
          <div className="grid grid-cols-1  gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">
                Tên danh mục
              </label>
              <input
                type="text"
                className="w-full border rounded px-3 py-2"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Tên danh mục"
              />
            </div>
          </div>
          <div className="mt-4">
            <label className="block text-sm font-medium mb-1">
              Đường dẫn của danh mục
            </label>
            <div className="flex">
              <span className="bg-gray-200 border border-r-0 rounded-l px-3 py-2">
                https://ShopIoT.com/danh-muc/
              </span>
              <input
                type="text"
                className="w-full border rounded-r px-3 py-2"
                placeholder="dien-thoai"
                value={slug}
                onChange={(e) => setSlug(e.target.value)}
              />
            </div>
          </div>
          <div className="mt-4">
            <p className="block text-sm font-medium mb-1">Mô tả</p>
            <TextEditor text={description} setText={setDescription} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default CategoryAdd;
