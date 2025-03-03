import axiosInstance from "../configs/axiosInstance";

export async function getOrdersByUserId(userId, pageNum, pageSize) {
  const response = await axiosInstance.get(
    `/orders/user-id=${userId}?pageNumber=${pageNum}&pageSize=${pageSize}`
  );

  return response;
}

export async function getAllOrder(pageNum, pageSize) {
  const response = await axiosInstance.get(
    `/orders/all?pageNumber=${pageNum}&pageSize=${pageSize}`
  );

  return response;
}

export async function getOrderByOrderCode(orderCode) {
  const response = await axiosInstance.get(`/orders/code/${orderCode}`);

  return response;
}

export async function changeStatusOrder(orderCode, status) {
  const response = await axiosInstance.put(
    `/orders/code/${orderCode}/change-status`,
    {
      newStatus: status,
    }
  );

  return response;
}

export async function changePaymentStatusOrder(orderCode, status) {
  const response = await axiosInstance.put(
    `/orders/code/${orderCode}/change-payment-status?paymentStatus=${status}`
  );

  return response;
}

export async function checkPrevOrder(bodyRequest) {
  const response = await axiosInstance.post(
    `/orders/checkPreOrder`,
    bodyRequest
  );

  return response;
}

export async function createOrder(newOrder) {
  const response = await axiosInstance.post(`/orders`, newOrder);
  return response;
}

export async function getMyOrder() {
  const response = await axiosInstance.get(`/orders/my-orders`);
  return response;
}

export async function cancelOrder(orderCode) {
  const response = await axiosInstance.put(
    `/orders/my-order/cancel/${orderCode}`
  );
  return response;
}
