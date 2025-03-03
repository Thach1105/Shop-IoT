import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import TextEditor from "../../../../utils/TextEditor";
import { faPlus, faX } from "@fortawesome/free-solid-svg-icons";
import React, { useEffect, useState } from "react";
import { Link, useLoaderData } from "react-router-dom";
import { getCategoriesTree } from "../../../../services/apiCategory";
import { updateProduct } from "../../../../services/apiProduct";
import { formatDateTime } from "../../../../utils/format";
import { getBrands } from "../../../../services/apiBrand";
import { TextField } from "@mui/material";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

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

function brandSelect(brands) {
  return brands.map((brand, index) => (
    <option className="text-xs" value={brand.id} key={index}>
      {brand.name}
    </option>
  ));
}

function convertDetailToArray(details) {
  return Object.keys(details).map((option) => ({
    option,
    value: details[option],
  }));
}

function ProductEdit() {
  const [thisProduct, setProduct] = useState(useLoaderData());

  const [openSnackBar, setOpenSnackBar] = useState(false);
  const [image, setImage] = useState();
  const [productName, setProductName] = useState(thisProduct.name);
  const [sku, setSKU] = useState(thisProduct.sku);
  const [stock, setStock] = useState(thisProduct.stock);
  const [shortDescription, setShortDescription] = useState(
    thisProduct.shortDescription
  );
  const [longDescription, setLongDescription] = useState(
    thisProduct.longDescription
  );
  const [options, setOptions] = useState(
    convertDetailToArray(thisProduct.productDetails)
  );
  const [price, setPrice] = useState(thisProduct.price);
  const [cost, setCost] = useState(thisProduct.cost);
  const [active, setActive] = useState(thisProduct.active);
  const [categoryId, setCategoryId] = useState(thisProduct.category?.id || 0);
  const [brandId, setBrandId] = useState(thisProduct.brand?.id || 0);
  const [categories, setCategories] = useState([]);
  const [slug, setSlug] = useState(thisProduct.slug);
  const [brands, setBrands] = useState([]);

  console.log(thisProduct);
  useEffect(() => {
    async function fetchData() {
      try {
        const categoriesList = await getCategoriesTree();
        setCategories(categoriesList.data?.content);

        const brandList = await getBrands();
        setBrands(brandList.data?.content);
      } catch (error) {
        console.log(error);
      }
    }

    fetchData();
  }, []);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setImage(file);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    const file = e.dataTransfer.files[0];
    setImage(file);
  };

  const removeImage = () => {
    setImage(null);
  };

  const handleAddOption = () => {
    setOptions([...options, { option: "", value: "" }]);
  };

  const handleOptionChange = (index, field, value) => {
    const updatedOptions = options.map((opt, idx) =>
      idx === index ? { ...opt, [field]: value } : opt
    );
    setOptions(updatedOptions);
  };

  const handleRemoveOption = (index) => {
    const updatedOptions = options.filter((_, idx) => idx !== index);
    setOptions(updatedOptions);
  };

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setOpenSnackBar(false);
  };

  async function handleSave() {
    const postProduct = {
      name: productName,
      sku,
      stock: Number(stock),
      price: Number(price),
      cost: Number(cost),
      shortDescription,
      longDescription,
      slug,
      productDetails: options,
      category_id: Number(categoryId) || null,
      brand_id: Number(brandId) || null,
      active,
      discountPercentage: Number(
        (((Number(price) - Number(cost)) / Number(price)) * 100).toFixed(2)
      ),
    };

    const res = await updateProduct(postProduct, image, thisProduct.id);
    setOpenSnackBar(true);
    const { data } = res;
    setProduct(data.content);
  }

  return (
    <div className="max-w-full mx-auto bg-white p-6 rounded-lg shadow-md">
      <Snackbar
        open={openSnackBar}
        autoHideDuration={6000}
        onClose={handleCloseSnackBar}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity="success"
          variant="filled"
          sx={{ width: "100%" }}
        >
          Cập nhật sản phẩm thành công
        </Alert>
      </Snackbar>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Cập nhật sản phẩm</h1>
        <div className="space-x-2">
          <button
            onClick={handleSave}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-green-600"
          >
            Lưu
          </button>
        </div>
      </div>
      <p className="text-gray-500 mb-6">
        Sản phẩm sẽ được hiển thị trên cửa hàng
      </p>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="md:col-span-2">
          <div className="bg-gray-100 p-4 rounded-lg mb-6">
            <h2 className="text-lg font-semibold mb-4">Thông tin sản phẩm</h2>
            <label className="block mb-2 text-gray-600">Tên sản phẩm</label>
            <input
              type="text"
              placeholder="Tên sản phẩm"
              className="w-full p-2 mb-4 border bg-white rounded"
              value={productName}
              onChange={(e) => setProductName(e.target.value)}
            />
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div>
                <label className="block mb-2 text-gray-600">SKU</label>
                <input
                  type="text"
                  placeholder="SKU"
                  className="w-full p-2 border rounded"
                  value={sku}
                  onChange={(e) => setSKU(e.target.value)}
                />
              </div>

              <div>
                <label className="block mb-2 text-gray-600">
                  Số lượng sản phẩm trong kho
                </label>
                <input
                  type="number"
                  placeholder="Stock"
                  className="w-full p-2 border rounded bg-white"
                  value={stock}
                  onChange={(e) => setStock(e.target.value)}
                />
              </div>
            </div>
            <div className="flex mb-6">
              <span className="bg-gray-200 border border-r-0 rounded-l px-3 py-2">
                https://ShopIoT.com/san-pham/
              </span>
              <input
                type="text"
                className="bg-white w-full border rounded-r px-3 py-2"
                placeholder="dien-thoai"
                value={slug}
                onChange={(e) => setSlug(e.target.value)}
              />
            </div>
            <div className="mb-4">
              <label className="block mb-2 text-gray-600">
                Mô tả ngắn gọn (Tùy chọn)
              </label>

              <TextField
                className="w-full border-2 rounded bg-white"
                value={shortDescription}
                onChange={(e) => setShortDescription(e.target.value)}
                id="filled-textarea"
                //label="Short Description (Optional)"
                placeholder="Nhập mô tả"
                multiline
                variant="filled"
              />
            </div>
            <div>
              <label className="block mb-2 text-gray-600">
                Mô tả đầy đủ (Tùy chọn)
              </label>
              <TextEditor text={longDescription} setText={setLongDescription} />
            </div>
          </div>

          <div className="bg-gray-100 p-4 rounded-lg mb-6">
            <h2 className="text-lg font-semibold mb-4">Ảnh sản phẩm</h2>
            <div
              onDrop={handleDrop}
              onDragOver={(e) => e.preventDefault()}
              className="border-dashed border-2 border-gray-300 p-6 text-center rounded-lg"
            >
              {image ? (
                <div className="flex flex-col items-center">
                  <img
                    src={URL.createObjectURL(image)}
                    alt="Product"
                    className="object-cover mb-4"
                  />
                  <button
                    onClick={removeImage}
                    className="text-red-600 py-1 px-2 bg-gray-100 border-red-600 border-2 rounded-2xl"
                  >
                    Xóa ảnh
                  </button>
                </div>
              ) : (
                <div className="flex flex-col items-center">
                  <img
                    src={thisProduct.image_url}
                    alt="Product"
                    className="object-cover mb-4"
                  />

                  <label className="text-blue-600 py-1 px-2 bg-gray-100 border-blue-600 border-2 rounded-2xl cursor-pointer">
                    Chọn ảnh
                    <input
                      type="file"
                      accept="image/*"
                      onChange={(e) => handleFileChange(e)}
                      className="hidden"
                    />
                  </label>
                </div>
              )}
            </div>
          </div>

          <div className="bg-gray-100 p-4 rounded-lg">
            <h2 className="text-lg font-semibold mb-4">Thông số kỹ thuật</h2>
            <div className="border-dashed border-2 border-gray-300 p-6 text-center rounded-lg">
              {options.map((opt, index) => (
                <div key={index} className="flex items-center space-x-4 mb-4">
                  <div className="flex-1">
                    <div className="mt-1 relative rounded-md shadow-sm">
                      <TextField
                        className="block w-full pr-10 sm:text-sm bg-white border-gray-300 rounded-md p-2 mb-4 border"
                        id="outlined-basic"
                        label="Option"
                        variant="outlined"
                        value={opt.option}
                        onChange={(e) =>
                          handleOptionChange(index, "option", e.target.value)
                        }
                      />
                    </div>
                  </div>
                  <div className="flex-1">
                    <div className="mt-1 relative rounded-md shadow-sm">
                      <TextField
                        id="outlined-multiline-flexible"
                        label="Value"
                        multiline
                        maxRows={6}
                        className=" bg-white block w-full sm:text-sm border-gray-300 rounded-md p-2 mb-4 border"
                        value={opt.value}
                        onChange={(e) =>
                          handleOptionChange(index, "value", e.target.value)
                        }
                      />
                    </div>
                  </div>
                  <button
                    onClick={() => handleRemoveOption(index)}
                    className="text-red-600 hover:text-red-800"
                  >
                    <FontAwesomeIcon icon={faX} />
                  </button>
                </div>
              ))}
              <button
                onClick={handleAddOption}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <i className="fas fa-plus mr-2"></i> Thêm thông số kỹ thuật
              </button>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="bg-gray-100 p-4 rounded-lg">
            <h2 className="text-lg font-semibold mb-4">Giá cả</h2>
            <div className="flex items-center mb-3">
              <TextField
                id="outlined-basic"
                label="Giá niêm yết"
                variant="outlined"
                className="w-full mb-4 border rounded"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
              />
              <p className="p-2 font-semibold">VND</p>
            </div>
            <div className="flex items-center mb-3">
              <TextField
                id="outlined-basic"
                label="Giá bán"
                variant="outlined"
                className="w-full mb-4 border rounded"
                value={cost}
                onChange={(e) => setCost(e.target.value)}
              />
              <p className="p-2 font-semibold">VND</p>
            </div>

            <div className="flex">
              <p className="p-2 font-semibold whitespace-nowrap">Giảm giá: </p>
              <p className="w-full p-2 mb-4 border rounded bg-white">
                {(
                  ((Number(price) - Number(cost)) / Number(price)) *
                  100
                ).toFixed(2)}
              </p>
              <p className="p-2 font-semibold">%</p>
            </div>
          </div>
          <div className="bg-gray-100 p-4 rounded-lg">
            <h2 className="text-lg font-semibold mb-4">Phân loại</h2>
            <div className="flex items-center mb-4">
              <div className="flex-1">
                <p className="mb-1 text-gray-600 px-2">Danh mục sản phẩm</p>
                <select
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                  className="w-full p-2 border rounded"
                >
                  <option value={0}>Thương hiệu sản phẩm</option>
                  {categorySelect(categories)}
                </select>
              </div>

              <Link
                to={"/admin/category/create"}
                className="ml-2 px-2 bg-blue-600 text-white rounded-full"
              >
                <FontAwesomeIcon icon={faPlus} size="1x" />
              </Link>
            </div>

            <div className="">
              <p className="mb-1 text-gray-600 px-2">Thương hiệu</p>
              <select
                value={brandId}
                onChange={(e) => setBrandId(e.target.value)}
                className="w-full p-2 mb-4 border rounded"
              >
                <option value={0}>Chọn thương hiệu</option>
                {brandSelect(brands)}
              </select>
            </div>

            <div>
              <p className="mb-1 text-gray-600 px-2">Trạng thái</p>
              <select
                value={active}
                onChange={(e) => setActive(e.target.value)}
                className="w-full p-2 mb-4 border rounded"
              >
                <option value={true}>Hiển thị</option>
                <option value={false}>Ẩn</option>
              </select>
            </div>
          </div>

          <div className="bg-gray-100 p-4 rounded-lg">
            <h2 className="text-lg font-semibold mb-4">Thông tin khác</h2>
            <div>
              <p className="mb-1 text-gray-600 px-2">ID: </p>
              <p className="flex-1 p-2 mb-4 border rounded">{`#${thisProduct.id}`}</p>
            </div>
            <div>
              <p className="mb-1 text-gray-600 px-2">
                Số lượng sản phẩm đã bán:
              </p>
              <p className="flex-1 p-2 mb-4 border rounded">
                {thisProduct.salesNumber}
              </p>
            </div>
            <div>
              <p className="mb-1 text-gray-600 px-2">Thời gian tạo: </p>
              <p className="flex-1 p-2 mb-4 border rounded">
                {formatDateTime(thisProduct.createdAt)}
              </p>
            </div>
            <div>
              <p className="mb-1 text-gray-600 px-2">
                Thời gian cập nhật gần nhất:
              </p>
              <p className="flex-1 p-2 mb-4 border rounded">
                {formatDateTime(thisProduct.updatedAt)}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductEdit;
