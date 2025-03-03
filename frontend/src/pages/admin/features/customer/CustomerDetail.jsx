import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getOrdersByUserId } from "../../../../services/apiOrder";
import { getUserById } from "../../../../services/apiCustomer";
import TablePagination from "@mui/material/TablePagination";
import { formatDateTime, formatNumber } from "../../../../utils/format";

function CustomerDetail() {
  const { customerId } = useParams();
  const [customer, setCustomer] = useState(null);
  const [orders, setOrders] = useState([]);
  const [pageDetail, setPageDetail] = useState(null);
  const [pageNum, setPageNum] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  useEffect(() => {
    async function fetchCustomer() {
      try {
        const response = await getUserById(customerId);
        console.log(response);
        const { data } = response;
        setCustomer(data?.content);
      } catch (error) {
        console.log(error);
      }
    }

    fetchCustomer();
  }, [customerId]);

  useEffect(() => {
    async function fetchDataOrder() {
      try {
        const response = await getOrdersByUserId(
          customerId,
          pageNum + 1,
          pageSize
        );
        const { data } = response;
        setOrders(data?.content);
        setPageDetail(data?.pageDetails);
        console.log(response);
      } catch (error) {
        console.log(error);
      }
    }

    fetchDataOrder();
  }, [customerId, pageNum, pageSize]);

  const handleChangePage = (event, newPage) => {
    setPageNum(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setPageSize(parseInt(event.target.value, 10));
    setPageNum(0);
  };

  return (
    customer && (
      <div className="p-8 bg-gray-100 min-h-screen">
        <h1 className="text-3xl font-bold mb-6">THÔNG TIN KHÁCHH HÀNG</h1>
        <div className="grid grid-cols-2 gap-6 mb-6">
          <div className="bg-white p-6 rounded-lg shadow">
            <h2 className="text-xl font-bold mb-4">TÀI KHOẢN</h2>
            <p className="mb-2">
              <span className="font-bold">Tên đăng nhập:</span>{" "}
              {customer.username}
            </p>
            <p className="mb-2">
              <span className="font-bold">Họ và tên: </span> {customer.fullName}
            </p>
            <p className="mb-2">
              <span className="font-bold">Email: </span> {customer.email}
            </p>
            <p className="mb-2">
              <span className="font-bold">Số điện thoại: </span>{" "}
              {customer.phoneNumber}
            </p>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow">
          <h2 className="text-xl font-bold mb-4">ĐƠN HÀNG</h2>
          <table className="min-w-full bg-white">
            <thead>
              <tr>
                <th className="py-2 text-left">STT</th>
                <th className="py-2 text-left">MÃ ĐƠN HÀNG</th>
                <th className="py-2 text-left">TRẠNG THÁI ĐƠN</th>
                <th className="py-2 text-left">KIỂU THANH TOÁN</th>
                <th className="py-2 text-left">NGÀY TẠO ĐƠN</th>
                <th className="py-2 text-left">TỔNG TIỀN</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order, index) => (
                <tr key={index}>
                  <td className="py-2">{index + 1}</td>
                  <td className="py-2 flex items-center">
                    <Link to={`/admin/order-code/${order.orderCode}`}>
                      <p className="text-gray-900 whitespace-no-wrap hover:text-yellow-500">
                        {order.orderCode}
                      </p>
                    </Link>
                  </td>
                  <td className="py-2">{order.orderStatus}</td>
                  <td className="py-2 items-center">{order.paymentType}</td>
                  <td className="py-2">{formatDateTime(order.orderTime)}</td>
                  <td className="py-2">{`${formatNumber(
                    order.totalPrice
                  )} đ`}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {pageDetail && (
            <TablePagination
              component="div"
              count={pageDetail.totalElements}
              page={pageNum}
              onPageChange={handleChangePage}
              rowsPerPage={pageSize}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          )}
        </div>
      </div>
    )
  );
}

export default CustomerDetail;
