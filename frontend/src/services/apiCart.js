import axiosInstance from "../configs/axiosInstance";
import config from "../configs/config";

const url = config.url_Backend;

export async function getMyCart() {
  const response = await axiosInstance.get(`${url}/carts/my-cart`);

  return response;
}

export async function addProductToCart(body) {
  const response = await axiosInstance.post(`${url}/carts/add-to-cart`, body);

  return response;
}

export async function deleteProductFormCart(productId) {
  const response = await axiosInstance.delete(
    `${url}/carts/delete-from-cart?productId=${productId}`
  );

  return response;
}

export async function changeProductQuantityInCart(cartId, reqBody) {
  const response = await axiosInstance.put(
    `/carts/${cartId}/change-quantity`,
    reqBody
  );

  return response;
}

export async function themVaoGioHang() {
  const response = await axiosInstance.post(
    `${url}/carts/them-vao-gio-hang`,
    {},
    {
      withCredentials: true, // Bật gửi cookie
    }
  );

  return response;
}
