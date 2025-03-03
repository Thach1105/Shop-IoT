import { useEffect, useState } from "react";
import { redirectFromVNPAY, redirectFromZALOPAY } from "../services/apiPayment";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCircleCheck,
  faCircleXmark,
  faSpinner,
} from "@fortawesome/free-solid-svg-icons";

function PaymentNotice() {
  const [paymentStatus, setPaymentStatus] = useState({
    status: "loading",
    message: "",
  });

  useEffect(() => {
    async function callServer() {
      try {
        const searchParams = new URLSearchParams(window.location.search);
        const paramObject = {};
        searchParams.forEach((value, key) => {
          paramObject[key] = value;
        });

        const response =
          paramObject["paymentType"] === "VNPAY"
            ? await redirectFromVNPAY(
                new URLSearchParams(paramObject).toString()
              )
            : await redirectFromZALOPAY(
                new URLSearchParams(paramObject).toString()
              );
        const { data } = response;
        console.log(data);
        setPaymentStatus({
          status: data.success ? "success" : "error",
          message: "Đơn hàng của bạn đã được thanh toán thành công",
        });
      } catch (e) {
        console.log(e);
        setPaymentStatus({
          status: "error",
          message: "Có lỗi xảy ra trong quá trình thanh toán",
        });
      }
    }
    callServer();
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8">
        {paymentStatus.status === "loading" ? (
          <div className="flex flex-col items-center">
            <FontAwesomeIcon
              icon={faSpinner}
              className="h-12 w-12 text-blue-500 animate-spin"
            />
            <p className="mt-4 text-gray-600">Đang xử lý thanh toán...</p>
          </div>
        ) : paymentStatus.status === "success" ? (
          <div className="text-center">
            <FontAwesomeIcon
              icon={faCircleCheck}
              className="h-16 w-16 text-green-500"
            />
            <h2 className="mt-4 text-2xl font-bold text-gray-800">
              Thanh toán thành công!
            </h2>
            <p className="mt-2 text-gray-600">{paymentStatus.message}</p>
            <button
              onClick={() =>
                (window.location.href = "/tai-khoan/quan-ly-don-hang")
              }
              className="mt-6 bg-green-500 text-white px-6 py-2 rounded-full hover:bg-green-600 transition-colors"
            >
              Về quản lý đơn hàng
            </button>
          </div>
        ) : (
          <div className="text-center">
            <FontAwesomeIcon
              icon={faCircleXmark}
              className="h-16 w-16 text-red-500"
            />
            <h2 className="mt-4 text-2xl font-bold text-gray-800">
              Xảy ra lỗi trong quá trình thanh toán
            </h2>
            <p className="mt-2 text-gray-600">{paymentStatus.message}</p>
            <button
              onClick={() =>
                (window.location.href = "/tai-khoan/quan-ly-don-hang")
              }
              className="mt-6 bg-red-500 text-white px-6 py-2 rounded-full hover:bg-red-600 transition-colors"
            >
              Về quản lý đơn hàng
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default PaymentNotice;
