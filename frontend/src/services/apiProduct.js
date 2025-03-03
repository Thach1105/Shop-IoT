import config from "../configs/config";
import axiosInstance from "../configs/axiosInstance";
import axios from "axios";

const url = config.url_Backend;

const prepareDataForRequest = (product) => {
  return {
    ...product,
    productDetails: product.productDetails.reduce((map, detail) => {
      map[detail.option] = detail.value;
      return map;
    }, {}),
  };
};

export async function createNewProduct(newProduct, image) {
  const formData = new FormData();
  const productReq = prepareDataForRequest(newProduct);

  formData.append(
    "product",
    new Blob([JSON.stringify(productReq)], { type: "application/json" })
  );

  if (image) formData.append("image", image);

  const response = await axiosInstance.post(`${url}/products`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return response;
}

export async function updateProduct(postProduct, image, productId) {
  const formData = new FormData();
  const productReq = prepareDataForRequest(postProduct);

  formData.append(
    "product",
    new Blob([JSON.stringify(productReq)], { type: "application/json" })
  );

  if (image) {
    formData.append("image", image);
  }

  const response = await axiosInstance.put(
    `${url}/products/${productId}`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return response;
}

export async function getProducts(pageNumber, pageSize) {
  const response = await axiosInstance.get(
    `${url}/products?size=${pageSize}&page=${pageNumber}`
  );

  return response;
}

export async function getPopularProduct(pageSize) {
  const response = await axios.get(
    `${url}/products?size=${pageSize}&page=1&sortBy=rating&order=desc`
  );

  return response;
}

export async function getSingleProduct(productId) {
  const requestURL = `${url}/products/${productId}`;
  const response = await axiosInstance.get(requestURL);
  return response;
}

export async function searchProduct(params) {
  const { pageNumber, pageSize, search, active, inStock, category, sortField } =
    params;

  const requestURL = `${url}/products/search?size=${pageSize}&page=${pageNumber}&q=${search}${
    active === 1 ? `&active=${true}` : active === 0 ? `&active=${false}` : ""
  }${
    inStock === 1
      ? `&inStock=${true}`
      : inStock === 0
      ? `&inStock=${false}`
      : ""
  }${category > 0 ? `&category=${category}` : ""}&sortField=${sortField}`;
  const response = await axiosInstance.get(requestURL);

  return response;
}

export async function searchForCustomer(params) {
  const { size, keyword, page, minPrice, maxPrice, sortField } = params;
  const requestURL =
    `${url}/products/search?size=${size}&q=${keyword}&page=${page}` +
    `${minPrice ? `&minPrice=${minPrice}` : ""}` +
    `${maxPrice ? `&maxPrice=${maxPrice}` : ""}` +
    `${sortField ? `&sortField=${sortField}` : ""}`;

  const response = await axios.get(requestURL);
  return response;
}

export async function getProductByCategory(params) {
  const { size, page, minPrice, maxPrice, categoryId, sortField } = params;
  const requestURL = `${url}/products/category/${categoryId}?size=${size}&number=${page}${
    minPrice ? `&minPrice=${minPrice}` : ""
  }${maxPrice ? `&maxPrice=${maxPrice}` : ""}${
    sortField ? `&sortField=${sortField}` : ""
  }`;

  const response = await axios.get(requestURL);
  return response;
}

export async function getProductBySlug(slug) {
  const response = axios.get(`${url}/products/slug/${slug}`);
  return response;
}
