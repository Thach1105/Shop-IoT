import { Button } from "@mui/material";
import PropTypes from "prop-types";
import { formatDateTime, formatNumber } from "../../../../utils/format";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faAngleLeft } from "@fortawesome/free-solid-svg-icons";
import { cancelOrder } from "../../../../services/apiOrder";
import { useState } from "react";
//import { useNavigate } from "react-router-dom";
import MyModal from "../../../../utils/MyModal";
import {
  paymentByVNPAY,
  paymentByZALOPAY,
} from "../../../../services/apiPayment";
import MyReviewProduct from "./MyReviewProduct";
// import { getMyReviewForProduct } from "../../../../services/apiReview";

const displayStatusOrder = (status) => {
  switch (status) {
    case "PENDING":
      return (
        <h1 className="text-blue-500 text-2xl font-semibold">
          Tạo đơn hàng thành công
        </h1>
      );

    case "PROCESSING":
      return (
        <h1 className="text-orange-500 text-2xl font-semibold">Đang xử lý</h1>
      );

    case "SHIPPED":
      return (
        <h1 className="text-yellow-500 text-2xl font-semibold">
          Đang vận chuyển
        </h1>
      );

    case "DELIVERED":
      return (
        <h1 className="text-green-500 text-2xl font-semibold">Đã nhận hàng</h1>
      );

    case "CANCELLED":
      return <h1 className="text-black text-2xl font-semibold">Đã hủy</h1>;

    default:
      break;
  }
};

function OrderInformation({ order, setOrder }) {
  const { orderDetail } = order;
  //const navigate = useNavigate();

  const [err, setErr] = useState();
  const [selectedProductRv, setSelectedProductRV] = useState();
  console.log(orderDetail);
  const totalPriceItems = orderDetail.reduce(
    (total, item) => total + item.totalPrice,
    0
  );

  // const [myReview, setMyReview] = useState();

  // useEffect(() => {
  //   async function fetchMyReviewProduct() {
  //     try {
  //       const response = getMyReviewForProduct();
  //     } catch (error) {}
  //   }
  // }, []);

  const handleCancelOrder = async () => {
    try {
      const response = await cancelOrder(order.orderCode);
      const { data } = response;
      const { content } = data;

      setOrder(content);
    } catch (error) {
      if (error.status === 400) {
        const { response } = error;
        const { data } = response;
        const err = {
          errorCode: data?.code,
          errorMessage:
            data?.code === 1008
              ? "Đơn hàng đã được vận chuyển hoặc thanh toán nên không thể hủy bỏ"
              : "Bạn không có quyền hủy đơn hàng này",
        };
        setErr(err);
      }
    }
  };

  const handleResetErr = () => setErr(null);

  const handlePaymentVNPay = async () => {
    try {
      const response = await paymentByVNPAY(order.orderCode);
      const { data } = response;
      const paymentURL = data?.content;
      window.location.href = paymentURL;
    } catch (error) {
      const { response } = error;
      const { data } = response;
      const err = {
        errorCode: data?.code,
        errorMessage:
          data?.code === 1011
            ? "Đơn hàng đã được thanh toán hoặc hủy bỏ"
            : "Đã xảy ra lỗi",
      };
      setErr(err);
    }
  };

  const handlePaymenByZalopay = async () => {
    try {
      const response = await paymentByZALOPAY(order.orderCode);
      const { data } = response;
      const { content } = data;
      if (content?.return_code == 1) {
        const paymentURL = content.order_url;
        window.location.href = paymentURL;
      } else {
        const err = {
          errorCode: 9999,
          errorMessage: "Lỗi hệ thống",
        };
        setErr(err);
      }
    } catch (error) {
      const { response } = error;
      const { data } = response;
      const err = {
        errorCode: data?.code,
        errorMessage:
          data?.code === 1011
            ? "Đơn hàng đã được thanh toán hoặc hủy bỏ"
            : "Đã xảy ra lỗi",
      };
      setErr(err);
    }
  };

  return (
    <div className="p-6 w-full mx-auto bg-white">
      <MyModal err={err} handleResetError={handleResetErr} />
      {selectedProductRv && (
        <MyReviewProduct
          selectedProductRv={selectedProductRv}
          setSelectedProductRV={setSelectedProductRV}
        />
      )}
      {/* Header */}
      <div className="flex items-center gap-2 mb-6">
        <span
          onClick={() => {
            setOrder();
            //navigate(0);
          }}
          className="cursor-pointer"
        >
          <FontAwesomeIcon icon={faAngleLeft} />
        </span>
        <h1 className="text-2xl font-semibold">
          {`Chi tiết đơn hàng #${order.orderCode} -`}
        </h1>
        {displayStatusOrder(order.orderStatus)}
      </div>

      {/* Order Information */}
      <div className="p-6 rounded-lg shadow-sm mb-6">
        <h2 className="text-gray-600 font-medium mb-4">THÔNG TIN ĐƠN HÀNG</h2>
        <div className="flex flex-col bg-sky-100 p-4 shadow-sm gap-2">
          <div className="flex-1 flex">
            <p className="text-gray-500 w-1/4">Ngày đặt hàng:</p>
            <p>{formatDateTime(order.orderTime)}</p>
          </div>

          <div className="flex-1 flex">
            <p className="text-gray-500 w-1/4">Phương thức nhận hàng:</p>
            {order.homeDelivery ? (
              <p>Giao hàng tận nhà</p>
            ) : (
              <p>Nhận tại cửa hàng</p>
            )}
          </div>

          <div className="flex-1 flex">
            <p className="text-gray-500 w-1/4">Địa chỉ nhận hàng:</p>
            <p>{order.address}</p>
          </div>
        </div>
      </div>

      {/* Customer Information */}
      <div className="grid grid-cols-2 gap-6 mb-6">
        <div className="bg-white p-6 rounded-lg shadow-sm">
          <h2 className="text-gray-600 font-medium mb-4">
            THÔNG TIN NGƯỜI NHẬN
          </h2>
          <div className="space-y-1 bg-sky-100 p-4">
            <p className="font-semibold text-base">{order.consigneeName}</p>

            <div className="flex justify-center items-center gap-2">
              <p className="text-sm text-gray-500 w-fit">Điện thoai:</p>
              <p className="flex-1">{order.phone}</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm">
          <h2 className="text-gray-600 font-medium mb-4">
            HÌNH THỨC THANH TOÁN
          </h2>
          <div className="p-4 bg-sky-100">
            <p className="font-semibold">
              {order.paymentType === "COD"
                ? "Thanh toán bằng tiền mặt khi nhận hàng (COD)"
                : `Thanh toán bằng ví ${order.paymentType}`}
            </p>
            {order.paymentStatus ? (
              <p className="text-sm text-green-600">(đã thanh toán)</p>
            ) : (
              <p className="text-s  text-red-600">(chưa thanh toán)</p>
            )}
          </div>
        </div>
      </div>

      {/* Products */}
      <div className="bg-white p-6 rounded-lg shadow-sm mb-6">
        <h2 className="text-gray-600 font-medium mb-4">Kiện Hàng</h2>
        <table className="w-full">
          <thead>
            <tr className="border-b">
              <th className="text-left py-2">Sản phẩm</th>
              <th className="text-right">Giá sản phẩm</th>
              <th className="text-right">Số lượng</th>
              <th className="text-right">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            {orderDetail.map((item, index) => (
              <tr key={index} className="border-b">
                <td className="py-4">
                  <div className="flex items-center gap-4">
                    <img
                      src={item.product.imageUrl}
                      alt={item.product.productName}
                      className="w-16 h-16 object-cover"
                    />
                    <div>
                      <p>{item.product.productName}</p>
                      <p className="text-gray-500 text-sm">
                        {item.product.sku}
                      </p>
                      <div className="flex gap-2 mt-2">
                        {order.orderStatus === "DELIVERED" && (
                          <Button
                            onClick={() => setSelectedProductRV(item.product)}
                            size="small"
                            variant="outlined"
                          >
                            Đánh giá
                          </Button>
                        )}

                        {/* <Button size="small" variant="outlined">
                          Mua lại
                        </Button> */}
                      </div>
                    </div>
                  </div>
                </td>
                <td className="text-right">{formatNumber(item.unitPrice)} đ</td>
                <td className="text-right">{item.quantity}</td>
                <td className="text-right">
                  {formatNumber(item.totalPrice)} đ
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* Order Summary */}
        <div className="mt-6 space-y-2">
          <div className="flex justify-between">
            <span>Tạm tính</span>
            <span>{formatNumber(totalPriceItems)} đ</span>
          </div>

          <div className="flex justify-between">
            <span>Phí vận chuyển</span>
            <span>{formatNumber(order.totalPrice - totalPriceItems)} đ</span>
          </div>
          <div className="flex justify-between font-bold">
            <span>Tổng tiền</span>
            <span className="text-red-600">
              {formatNumber(order.totalPrice)} đ
            </span>
          </div>
        </div>
      </div>

      {/* Actions */}
      {!order.paymentStatus && (
        <div className="flex justify-end gap-4">
          <Button
            onClick={handleCancelOrder}
            variant="outlined"
            color="inherit"
            disabled={
              order.orderStatus !== "PENDING" &&
              order.orderStatus !== "PROCESSING"
            }
          >
            HỦY ĐƠN HÀNG
          </Button>
          <Button
            onClick={
              order.paymentType === "VNPAY"
                ? handlePaymentVNPay
                : handlePaymenByZalopay
            }
            variant="contained"
            color="primary"
            disabled={
              order.paymentType === "COD" || order.orderStatus === "CANCELLED"
            }
          >
            THANH TOÁN
          </Button>
        </div>
      )}
    </div>
  );
}

OrderInformation.propTypes = {
  order: PropTypes.shape({
    orderDetail: PropTypes.array.isRequired,
    orderCode: PropTypes.string.isRequired,
    orderStatus: PropTypes.string.isRequired,
    orderTime: PropTypes.string.isRequired,
    address: PropTypes.string.isRequired,
    consigneeName: PropTypes.string.isRequired,
    phone: PropTypes.string.isRequired,
    paymentType: PropTypes.string.isRequired,
    paymentStatus: PropTypes.bool.isRequired,
    homeDelivery: PropTypes.bool.isRequired,
    totalPrice: PropTypes.number.isRequired,
  }).isRequired,
  setOrder: PropTypes.func.isRequired,
};

export default OrderInformation;
