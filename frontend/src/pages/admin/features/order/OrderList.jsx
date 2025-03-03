import { useEffect, useState } from "react";
import TablePagination from "@mui/material/TablePagination";
import OrderRow from "./OrderRow";
import {
  getAllOrder,
  getOrderByOrderCode,
} from "../../../../services/apiOrder";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass } from "@fortawesome/free-solid-svg-icons";

function OrderList() {
  const [orders, setOrders] = useState([]);
  const [pageNum, setPageNum] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [pageDetail, setPageDetail] = useState(null);
  const [search, setSearch] = useState("");

  useEffect(() => {
    async function fetchOrders() {
      try {
        const { data } = await getAllOrder(pageNum + 1, pageSize);
        console.log(data);
        setOrders(data?.content);
        setPageDetail(data?.pageDetails);
      } catch (error) {
        console.log(error);
      }
    }

    if (!search) fetchOrders();
  }, [pageNum, pageSize, search]);

  const handleChangePage = (event, newPage) => {
    setPageNum(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setPageSize(parseInt(event.target.value, 10));
    setPageNum(0);
  };

  async function handleSearch() {
    try {
      const response = await getOrderByOrderCode(search);
      console.log(response);
    } catch (error) {
      if (error.status === 400) {
        const { response } = error;
        setOrders([]);
        setPageDetail(null);
        console.log(response);
      }
    }
  }

  return (
    <div className="container mx-auto p-4">
      <div className="flex justify-between m-4">
        <h1 className="text-2xl font-bold mb-4">Quản lý đơn hàng</h1>
        <div className="relative">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value.toUpperCase())}
            placeholder="Tìm mã đơn hàng"
            className="p-2 pl-2 rounded-full border border-gray-300 focus:outline-none"
          />
          <button
            className="bg-gray-500 p-2 rounded-xl ml-1"
            onClick={handleSearch}
          >
            <FontAwesomeIcon icon={faMagnifyingGlass} className=" text-white" />
          </button>
        </div>
      </div>

      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <table className="min-w-full bg-white">
          <thead>
            <tr>
              <th className="py-2 px-6 bg-gray-100 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Mã đơn hàng
              </th>
              <th className="py-2 px-6 bg-gray-100 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thời gian tạo
              </th>
              <th className="py-2 px-6 bg-gray-100 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tên khách hàng
              </th>
              <th className="py-2 px-6 bg-gray-100 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Phương thức thanh toán
              </th>
              <th className="py-2 px-6 bg-gray-100 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tổng tiền
              </th>
              <th className="py-2 px-6 bg-gray-100 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Trạng thái đơn
              </th>
            </tr>
          </thead>

          {orders.length === 0 ? (
            <div className=" border-b w-full text-center">
              <p className="py-3 italic">No order</p>
            </div>
          ) : (
            <tbody>
              {orders.map((order) => (
                <OrderRow key={order.id} order={order} />
              ))}
            </tbody>
          )}
        </table>

        {pageDetail && (
          <TablePagination
            component="div"
            count={pageDetail?.totalElements}
            page={pageNum}
            onPageChange={handleChangePage}
            rowsPerPage={pageSize}
            onRowsPerPageChange={handleChangeRowsPerPage}
          />
        )}
      </div>
    </div>
  );
}

export default OrderList;
