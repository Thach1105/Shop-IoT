import React, { useEffect, useState, useCallback } from "react";
import TextEditor from "../../../../utils/TextEditor";
import {
  getCategoriesTree,
  getCategoryById,
  updateCategory,
} from "../../../../services/apiCategory";
import { useNavigate, useParams } from "react-router-dom";

function CategoryOptions({ categories, depth = 0, excludeId }) {
  return categories.map((category) =>
    category.id !== Number(excludeId) ? (
      <React.Fragment key={category.id}>
        <option className="text-xs" value={category.id}>
          {"--".repeat(depth) + " " + category.name}
        </option>
        {category.children && (
          <CategoryOptions
            categories={category.children}
            depth={depth + 1}
            excludeId={excludeId}
          />
        )}
      </React.Fragment>
    ) : null
  );
}

function CategoryEdit() {
  const { categoryId } = useParams();
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState("");
  const [slug, setSlug] = useState("");
  const [parentId, setParentId] = useState(0);
  const [enabled, setEnabled] = useState();
  const [description, setDescription] = useState("");
  const [category, setCategory] = useState(null);

  const navigate = useNavigate();

  const fetchCategories = useCallback(async () => {
    try {
      const { data } = await getCategoriesTree();
      console.log(data);
      setCategories(data?.content || []);
    } catch (error) {
      console.error(error);
    }
  }, []);

  const fetchCategoryDetails = useCallback(async () => {
    try {
      const { data } = await getCategoryById(categoryId);
      console.log(data);
      if (data?.content) {
        setCategory(data.content);
        setEnabled(data.content.enabled);
        setName(data.content.name);
        setSlug(data.content.slug);
        setDescription(data.content.description);
        setParentId(data.content.parent_id || 0);
      }
    } catch (error) {
      console.error(error);
    }
  }, [categoryId]);

  useEffect(() => {
    fetchCategories();
    fetchCategoryDetails();
  }, [fetchCategories, fetchCategoryDetails]);

  async function handleSave() {
    const postCategory = {
      name,
      parent: parentId === 0 ? null : parentId,
      description,
      enabled,
      slug,
    };
    console.log(postCategory);

    try {
      const repsonse = await updateCategory(categoryId, postCategory);
      console.log(repsonse);
      navigate("/admin/categories-list");
    } catch (error) {
      console.log(error);
    }
  }

  return (
    category && (
      <div className="max-w-full mx-auto bg-white p-8 rounded-lg shadow-md">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">Chỉnh sửa danh mục sản phẩm</h1>
          <button
            className="bg-purple-500 text-white px-4 py-2 rounded"
            onClick={handleSave}
          >
            Lưu
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Visibility Status */}
          <div className="bg-white p-6 rounded-lg shadow-md col-span-1">
            <>
              <h2 className="text-lg font-semibold mb-4">
                Trạng thái hiển thị
              </h2>
              <div className="flex flex-col space-y-2">
                <label className="flex items-center">
                  <input
                    type="radio"
                    value={true}
                    name="visibility"
                    className="mr-2"
                    onChange={(e) => setEnabled(e.target.value)}
                    defaultChecked={enabled}
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
                    defaultChecked={!enabled}
                  />
                  Ẩn
                </label>
              </div>
            </>

            <>
              <div className="my-4">
                <label className="text-lg font-semibold mb-4">
                  Danh mục cha
                </label>
                <select
                  value={parentId}
                  onChange={(e) => setParentId(e.target.value)}
                  className="w-full p-2 border rounded"
                >
                  <option className="text-xs" value={0}>
                    --- Không có ---
                  </option>
                  <CategoryOptions
                    categories={categories}
                    excludeId={categoryId}
                  />
                </select>
              </div>
            </>
          </div>

          {/* Basic information */}
          <div className="bg-white p-6 rounded-lg shadow-md col-span-2">
            <h2 className="text-lg font-semibold mb-4 ">Thông tin cơ bản</h2>
            <div className="text-sm font-medium mb-4">{`ID: #${categoryId.padStart(
              4,
              "0"
            )}`}</div>
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
    )
  );
}

export default CategoryEdit;
