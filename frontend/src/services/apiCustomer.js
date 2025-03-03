//import config from "../configs/config";
import axiosInstance from "../configs/axiosInstance";

//const url = `${config.url_Backend}`;

export async function registerCustomer(newUser) {
  const response = await axiosInstance.post(`/users/register`, newUser, {
    headers: {
      "Content-Type": "application/json",
    },
  });

  return response;
}

export async function getUsers(pageNum, pageSize) {
  const response = await axiosInstance.get(
    `/users?pageNumber=${pageNum}&pageSize=${pageSize}`
  );
  return response;
}

export async function getUserById(userId) {
  const response = await axiosInstance.get(`/users/${userId}`);

  return response;
}

export async function updateCustomer(updateUser, id) {
  const response = await axiosInstance.put(`/users/${id}`, updateUser, {
    headers: {
      "Content-Type": "application/json",
    },
  });

  return response;
}

export async function changePassword(body, id) {
  const response = await axiosInstance.post(
    `/users/${id}/change-password`,
    body
  );

  return response;
}
