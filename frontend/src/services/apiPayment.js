import axiosInstance from "../configs/axiosInstance";

export async function paymentByVNPAY(orderCode) {
  const response = await axiosInstance(
    `/payment/vn-pay/pay-order/${orderCode}`
  );

  return response;
}

export async function paymentByZALOPAY(orderCode) {
  const response = await axiosInstance(
    `/payment/zalo-pay/pay-order/${orderCode}`
  );

  return response;
}

export async function redirectFromVNPAY(paramString) {
  console.log(paramString);
  const response = await axiosInstance(
    `/payment/redirect-from-vnpay?${paramString}`
  );
  return response;
}

export async function redirectFromZALOPAY(paramString) {
  console.log(paramString);
  const response = await axiosInstance(
    `/payment/redirect-from-zalopay?${paramString}`
  );
  return response;
}
