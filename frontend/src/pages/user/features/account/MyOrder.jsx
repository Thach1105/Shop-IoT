// import { useEffect, useState } from "react";
// import { getMyOrder } from "../../../../services/apiOrder";
// import { formatDateTime, formatNumber } from "../../../../utils/format";
import OrderInformation from "./OrderInfomation";
// import { useLocation } from "react-router-dom";

// const displayStatusOrder = (status) => {
//   switch (status) {
//     case "PENDING":
//       return <span className="text-blue-500 font-semibold">Chờ xử lý</span>;

//     case "PROCESSING":
//       return <span className="text-orange-500 font-semibold">Đang xử lý</span>;

//     case "SHIPPED":
//       return (
//         <span className="text-yellow-500 font-semibold">Đang vận chuyển</span>
//       );

//     case "DELIVERED":
//       return <span className="text-green-500 font-semibold">Đã nhận hàng</span>;

//     case "CANCELLED":
//       return <span className="text-black font-semibold">Đã hủy</span>;

//     default:
//       break;
//   }
// };

// function MyOrder() {
//   const location = useLocation();
//   const [myOrders, setMyOrders] = useState([]);
//   const [orderSelected, setOrderSelected] = useState(location?.state);
//   console.log(myOrders);

//   useEffect(() => {
//     async function fetchOrder() {
//       const response = await getMyOrder();
//       const { data } = response;
//       setMyOrders(data?.content);
//     }

//     fetchOrder();
//   }, []);

//   return orderSelected ? (
//     <OrderInformation order={orderSelected} setOrder={setOrderSelected} />
//   ) : (
//     <div className="p-6">
//       <h1 className="text-2xl font-semibold mb-6">Quản lý đơn hàng</h1>

//       <div className="bg-white rounded-lg shadow-sm">
//         <table className="w-full">
//           <thead>
//             <tr className="border-b">
//               <th className="text-left p-4">Mã đơn hàng</th>
//               <th className="text-left p-4">Ngày mua</th>
//               <th className="text-right p-4">Tổng tiền</th>
//               <th className="text-right p-4">Trạng thái đơn hàng</th>
//             </tr>
//           </thead>
//           <tbody>
//             {myOrders.map((order) => (
//               <tr key={order.id} className="border-b hover:bg-gray-50">
//                 <td onClick={() => setOrderSelected(order)} className="p-4">
//                   <p className="text-blue-500 font-semibold cursor-pointer hover:text-blue-700">
//                     {order.orderCode}
//                   </p>
//                 </td>
//                 <td className="p-4">{formatDateTime(order.orderTime)}</td>
//                 <td className="p-4 text-right">
//                   {formatNumber(order.totalPrice)} ₫
//                 </td>
//                 <td className="p-4 text-right">
//                   {displayStatusOrder(order.orderStatus)}
//                 </td>
//               </tr>
//             ))}
//           </tbody>
//         </table>
//       </div>
//     </div>
//   );
// }

// export default MyOrder;

import { useEffect, useState } from "react";
import { getMyOrder } from "../../../../services/apiOrder";
import { formatNumber } from "../../../../utils/format";
import { useLocation } from "react-router-dom";

const displayStatusOrder = (status) => {
  switch (status) {
    case "PENDING":
      return <span className="text-blue-500 font-semibold">Chờ xử lý</span>;
    case "PROCESSING":
      return <span className="text-orange-500 font-semibold">Đang xử lý</span>;
    case "SHIPPED":
      return (
        <span className="text-yellow-500 font-semibold">Đang vận chuyển</span>
      );
    case "DELIVERED":
      return <span className="text-green-500 font-semibold">Đã nhận hàng</span>;
    case "CANCELLED":
      return <span className="text-black font-semibold">Đã hủy</span>;
    default:
      return <></>;
  }
};

function MyOrder() {
  const location = useLocation();
  const [myOrders, setMyOrders] = useState([]);
  const [orderSelected, setOrderSelected] = useState(location?.state);
  const [filter, setFilter] = useState("ALL");
  const [currentOrders, setCurrentOrders] = useState([]);
  console.log(myOrders);
  console.log(orderSelected);

  useEffect(() => {
    async function fetchOrder() {
      const response = await getMyOrder();
      const { data } = response;
      setMyOrders(data?.content);
      setCurrentOrders(data?.content);
    }

    fetchOrder();
  }, []);

  useEffect(() => {
    if (filter === "ALL") {
      setCurrentOrders(myOrders);
    } else if (filter === "UNPAID") {
      const list = myOrders.filter(
        (p) => !p.paymentStatus && p.orderStatus !== "CANCELLED"
      );
      setCurrentOrders(list);
    } else {
      const list = myOrders.filter((p) => p.orderStatus === filter);
      setCurrentOrders(list);
    }
  }, [filter, myOrders]);

  return orderSelected ? (
    <OrderInformation order={orderSelected} setOrder={setOrderSelected} />
  ) : (
    <div className="p-6">
      <h1 className="text-2xl font-semibold mb-6">Quản lý đơn hàng</h1>
      <div className="max-w-4xl mx-auto bg-white shadow-md mt-10">
        <div className="border-b">
          <ul className="flex justify-between text-gray-600 text-sm">
            {[
              "ALL",
              "UNPAID",
              "PENDING",
              "PROCESSING",
              "SHIPPED",
              "DELIVERED",
              "CANCELLED",
            ].map((status) => (
              <li
                key={status}
                onClick={() => setFilter(status)}
                className={`p-4 border-b-2 border-transparent ${
                  filter === status
                    ? "border-blue-500 text-blue-500 font-semibold"
                    : "hover:border-blue-500"
                } cursor-pointer`}
              >
                {status === "ALL"
                  ? "Tất cả"
                  : status === "UNPAID"
                  ? "Chờ thanh toán"
                  : status === "PENDING"
                  ? "Chờ xử lý"
                  : status === "PROCESSING"
                  ? "Chờ giao hàng"
                  : status === "SHIPPED"
                  ? "Đang giao hàng"
                  : status === "DELIVERED"
                  ? "Đã nhận"
                  : "Đã hủy"}
              </li>
            ))}
          </ul>
        </div>
        {currentOrders.length === 0 ? (
          <div className="text-center">
            <p className="text-gray-500 p-6 italic">Chưa có đơn hàng</p>
          </div>
        ) : (
          currentOrders.map((order) => (
            <div
              key={order.id}
              className="p-4 border-2 my-6 shadow-gray-400 shadow-md"
            >
              <div className="flex justify-between items-center">
                <div className="flex items-center">
                  <span className="font-bold">
                    Mã đơn hàng: {order.orderCode}
                  </span>
                </div>
                <span className="text-red-500 font-bold">
                  {displayStatusOrder(order.orderStatus)}
                </span>
              </div>
              {order.orderDetail.map((item, index) => (
                <div
                  key={index}
                  className="border-t-2 pt-2 flex flex-row items-end justify-between mt-4"
                >
                  <div className="flex">
                    <img
                      src={item.product.imageUrl}
                      alt={item.product.productName}
                      className="w-12 h-12"
                    />
                    <div className="ml-4">
                      <div className="font-bold">
                        {item.product.productName}
                      </div>
                      <div className="text-gray-500">
                        Số lượng: x{item.quantity}
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2 justify-between items-center mt-4">
                    {/* <div className="text-gray-500 line-through">₫70.000</div> */}
                    <div className="text-red-500 font-bold">
                      ₫{formatNumber(item.unitPrice)}
                    </div>
                  </div>
                </div>
              ))}

              <div className="border-t-2 pt-2 flex justify-end items-center mt-4">
                <div className="text-gray-600">
                  Thành tiền:{" "}
                  <span className="text-red-500 font-bold">
                    ₫{formatNumber(order.totalPrice)}
                  </span>
                </div>
              </div>
              <div className="flex justify-end mt-4">
                <button
                  onClick={() => setOrderSelected(order)}
                  className="bg-blue-500 text-white px-4 py-2 rounded mr-2"
                >
                  Xem chi tiết đơn hàng
                </button>
                {/* <button className="border border-gray-300 text-gray-600 px-4 py-2 rounded mr-2">
                  Xem chi tiết đơn hàng
                </button> */}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default MyOrder;
