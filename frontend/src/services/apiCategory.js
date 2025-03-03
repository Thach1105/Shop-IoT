import axios from "axios";
import axiosInstance from "../configs/axiosInstance";
import config from "../configs/config";

const url = config.url_Backend;

export async function getAllCategories() {
  const response = await axiosInstance.get(`${url}/categories/all`);

  return response;
}

export async function createNewCateogory(newCategory) {
  const response = await axiosInstance.post(`${url}/categories`, newCategory);

  return response;
}

export async function getCategoriesTree() {
  const response = await axios.get(
    `${url}/categories`,
    {},
    {
      headers: {
        "ngrok-skip-browser-warning": true,
        "Content-Type": "application/json",
      },
    }
  );
  const { data } = response;
  console.log(data);
  return response;
}

export async function getCategoryById(id) {
  const response = await axios.get(`${url}/categories/${id}`);
  return response;
}

export async function getCategoryBySlug(slug) {
  const response = await axios.get(`${url}/categories/category-slug/${slug}`);
  return response;
}

export async function updateCategory(id, postCategory) {
  const response = await axiosInstance.put(
    `${url}/categories/${id}`,
    postCategory
  );
  return response;
}
