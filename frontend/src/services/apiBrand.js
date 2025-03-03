import axiosInstance from "../configs/axiosInstance";
import config from "../configs/config";

const url = config.url_Backend;

export async function getBrands() {
  const response = await axiosInstance.get(`${url}/brands`);

  return response;
}

export async function createBrand(newBrand, logo) {
  const formData = new FormData();
  formData.append(
    "brand",
    new Blob([JSON.stringify(newBrand)], { type: "application/json" })
  );

  if (logo) {
    formData.append("logo", logo);
  }

  const response = await axiosInstance.post(`${url}/brands`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response;
}

export async function updateBrand(newBrand, logo, id) {
  const formData = new FormData();
  formData.append(
    "brand",
    new Blob([JSON.stringify(newBrand)], { type: "application/json" })
  );

  if (logo) {
    formData.append("logo", logo);
  }

  const response = await axiosInstance.put(`${url}/brands/${id}`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response;
}
