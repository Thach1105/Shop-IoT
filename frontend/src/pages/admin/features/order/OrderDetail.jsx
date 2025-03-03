import { useEffect, useState } from "react";
import {
  changePaymentStatusOrder,
  changeStatusOrder,
  getOrderByOrderCode,
} from "../../../../services/apiOrder";
import { useParams } from "react-router-dom";
import { formatDateTime, formatNumber } from "../../../../utils/format";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

function OrderDetail() {
  const { orderCode } = useParams();
  const [order, setOrder] = useState(null);
  const [orderDetail, setOrderDetail] = useState([]);
  const [paymentStatus, setPaymentStatus] = useState(null);
  const [orderStatus, setOrderStatus] = useState(null);

  const [open, setOpen] = useState(false);

  const handleClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setOpen(false);
  };

  useEffect(() => {
    async function fetchOrder() {
      try {
        const response = await getOrderByOrderCode(orderCode);
        console.log(response);
        const { data } = response;
        setOrder(data?.content);
        setOrderDetail(data?.content?.orderDetail);
        setPaymentStatus(data?.content.paymentStatus);
        setOrderStatus(data?.content.orderStatus);
      } catch (error) {
        console.log(error);
      }
    }

    fetchOrder();
  }, [orderCode]);

  const subTotalPrice = orderDetail.reduce(
    (accumulator, item) => accumulator + item.totalPrice,
    0
  );

  async function handleSubmitStatus(e) {
    e.preventDefault();
    if (orderStatus !== order.orderStatus) {
      try {
        const response = await changeStatusOrder(orderCode, orderStatus);
        const { data } = response;
        setOrder(data.content);
        setOpen(true);
      } catch (error) {
        console.log(error);
      }
    }

    if (paymentStatus !== order.paymentStatus) {
      try {
        const response = await changePaymentStatusOrder(
          orderCode,
          paymentStatus
        );
        console.log(response);
      } catch (error) {
        console.log(error);
      }
    }
  }

  return (
    order && (
      <div className="p-8">
        <Snackbar
          open={open}
          autoHideDuration={3000}
          onClose={handleClose}
          anchorOrigin={{ vertical: "top", horizontal: "right" }}
        >
          <Alert
            onClose={handleClose}
            severity="success"
            variant="filled"
            sx={{ width: "100%" }}
          >
            Cập nhật trạng thái đơn hàng thành công!
          </Alert>
        </Snackbar>
        <h1 className="text-2xl font-bold mb-4">
          CHI TIẾT ĐƠN HÀNG:
          <span className="text-blue-600">{` #${order.orderCode}`}</span>
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <div className="bg-green-100 p-4 rounded flex items-center">
            <i className="fas fa-shopping-cart text-2xl text-green-600 mr-4"></i>
            <div>
              <p className="text-sm">Thời gian tạo đơn</p>
              <p className="font-bold">{formatDateTime(order.orderTime)}</p>
            </div>
          </div>
          <div className="bg-red-100 p-4 rounded flex items-center">
            <i className="fas fa-user text-2xl text-red-600 mr-4"></i>
            <div>
              <p className="text-sm">Tên người dùng</p>
              <p className="font-bold">{order.fullName}</p>
            </div>
          </div>
          <div className="bg-yellow-100 p-4 rounded flex items-center">
            <i className="fas fa-envelope text-2xl text-yellow-600 mr-4"></i>
            <div>
              <p className="text-sm">Email</p>
              <p className="font-bold">{order.email}</p>
            </div>
          </div>
          <div className="bg-blue-100 p-4 rounded flex items-center">
            <i className="fas fa-phone text-2xl text-blue-600 mr-4"></i>
            <div>
              <p className="text-sm">Số điện thoại</p>
              <p className="font-bold">{order.phone}</p>
            </div>
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
          <div className="bg-white p-4 rounded shadow">
            <div className="flex flex-col mb-2">
              <h2 className="font-bold">Tên người nhận:</h2>
              <p>{order.consigneeName}</p>
            </div>
            <div className="flex flex-col">
              <h2 className="font-bold">Địa chỉ giao hàng:</h2>
              <p>{order.address}</p>
            </div>
          </div>
          <div className="bg-white p-4 rounded shadow">
            <div className="flex justify-between items-center mb-4">
              <h2 className="font-bold">Thông tin hóa đơn:</h2>
            </div>
            <p>
              <span className="font-semibold">Hình thức thanh toán: </span>{" "}
              {order.paymentType}
            </p>
            {order.paymentStatus ? (
              <p className="text-green-600">Đã thanh toán</p>
            ) : (
              <p className="text-red-500">Chưa thanh toán</p>
            )}
          </div>
          <div className="bg-white p-4 rounded shadow">
            <div className="flex justify-between items-center mb-4">
              <h2 className="font-bold">Ghi chú từ người mua:</h2>
            </div>
            <p className="italic">{order.notes || "(không có)"}</p>
          </div>
        </div>

        <div className="flex justify-between gap-4 ">
          <div className="w-2/3 bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-xl font-bold mb-4">TÓM TẮT ĐƠN HÀNG</h2>
            <table className="w-full text-left">
              <thead>
                <tr className="border-b">
                  <th className="py-2">Tên sản phẩm</th>
                  <th className="py-2 text-center">Số lượng</th>
                  <th className="py-2 text-right">Giá</th>
                </tr>
              </thead>
              <tbody>
                {orderDetail.map((item, index) => (
                  <tr key={index} className="border-b">
                    <td className="flex gap-2 py-2">
                      <img
                        //src="https://placehold.co/50x50"
                        src={item?.product.imageUrl}
                        alt="Oculus VR"
                        className="w-12 h-12"
                      />
                      <div>
                        <div>{item?.product.productName}</div>
                        <div className="text-blue-500">{item?.product.sku}</div>
                      </div>
                    </td>
                    <td className="text-center py-2">{item.quantity}</td>
                    <td className="py-2 text-right">{`${formatNumber(
                      item.totalPrice
                    )} đ`}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="mt-4">
              <div className="flex justify-between">
                <div>Tạm tính:</div>
                <div>{`${formatNumber(subTotalPrice)} VND`}</div>
              </div>

              <div className="flex justify-between">
                <div>Phí vận chuyển:</div>
                <div>{`${formatNumber(
                  order.totalPrice - subTotalPrice
                )} VND`}</div>
              </div>
              <div className="flex justify-between font-bold">
                <div>Tổng tiền:</div>
                <div>{`${formatNumber(order.totalPrice)} VND`}</div>
              </div>
            </div>
          </div>
          <div className="w-1/3 bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-xl font-bold mb-4">Trạng thái đơn hàng</h2>
            <form onSubmit={handleSubmitStatus}>
              <div className="mb-4">
                <label className="block text-gray-700">ID</label>
                <input
                  type="text"
                  value={order.id}
                  className="w-full p-2 border rounded"
                  disabled
                />
              </div>
              <div className="mb-4">
                <label className="block text-gray-700">Tiến trình</label>
                <select
                  value={orderStatus}
                  onChange={(e) => setOrderStatus(e.target.value)}
                  className="w-full p-2 border rounded"
                >
                  <option value={"PENDING"}>Tiếp nhận đơn hàng</option>
                  <option value={"PROCESSING"}>Đang xử lý</option>
                  <option value={"SHIPPED"}>Đang vẫn chuyển</option>
                  <option value={"DELIVERED"}>Đã nhận</option>
                  <option value={"CANCELLED"}>Đã hủy</option>
                </select>
              </div>

              <div className="mb-4">
                <label className="block text-gray-700">
                  Trạng thái thanh thoán
                </label>
                <select
                  value={paymentStatus}
                  onChange={(e) => setPaymentStatus(e.target.value)}
                  className="w-full p-2 border rounded"
                >
                  <option value={true}>Đã thanh toán</option>
                  <option value={false}>Chưa thanh toán</option>
                </select>
              </div>

              <button
                type="submit"
                className="w-full bg-purple-500 text-white p-2 rounded"
              >
                CẬP NHẬT TRẠNG THÁI
              </button>
            </form>
          </div>
        </div>
      </div>
    )
  );
}

export default OrderDetail;
