import InfoCard from "./InfoCard";
import { faUser } from "@fortawesome/free-solid-svg-icons";
import { faCartShopping } from "@fortawesome/free-solid-svg-icons";
import { faCalculator } from "@fortawesome/free-solid-svg-icons";
import { faBox } from "@fortawesome/free-solid-svg-icons";
import { formatNumber } from "../../../../utils/format";
import { useEffect, useState } from "react";
import { getStatisticsData } from "../../../../services/apiStatistic";
import { Alert } from "@mui/material";

function Dashboard() {
  const [messageErr, setMessageErr] = useState("");
  const [statistic, setStatistic] = useState();
  const [startDate, setStartDate] = useState(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  });
  const [endDate, setEndDate] = useState(() => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  });

  useEffect(() => {
    async function loadData() {
      if (startDate && endDate) {
        const response = await getStatisticsData(startDate, endDate);
        const { data } = response;

        setStatistic(data.content);
      }
    }

    loadData();
  }, [startDate, endDate]);

  const handleStartDateChange = (event) => {
    const now = new Date();
    const dateChange = new Date(event.target.value);
    if (dateChange <= now && dateChange <= new Date(endDate)) {
      setStartDate(event.target.value);
    } else {
      setMessageErr("Vui lòng chọn thời gian phù hợp");
    }
  };

  const handleEndDateChange = (event) => {
    const now = new Date();
    const dateChange = new Date(event.target.value);

    if (dateChange <= now && dateChange >= new Date(startDate)) {
      setEndDate(event.target.value);
    } else {
      setMessageErr("Vui lòng chọn thời gian phù hợp");
    }
  };

  return (
    <div className="p-6">
      <span className="text-2xl font-semibold">Khoảng thời gian thống kê:</span>
      {messageErr && (
        <Alert
          className="my-2 w-fit"
          severity="warning"
          onClose={() => {
            setMessageErr("");
          }}
        >
          {messageErr}
        </Alert>
      )}
      {/* Phần input chọn khoảng thời gian */}
      <div className="flex gap-6 my-4">
        <div className="font-semibold">
          <label className="text-lg">Từ: </label>
          <input
            className="rounded-md p-2"
            type="date"
            value={startDate}
            onChange={handleStartDateChange}
          />
        </div>

        <div className="font-semibold">
          <label className="text-lg">Đến: </label>

          <input
            className="rounded-md p-2"
            type="date"
            value={endDate}
            onChange={handleEndDateChange}
          />
        </div>
      </div>

      {/* Các thẻ thông tin */}
      <div className="grid grid-cols-4 gap-4">
        <InfoCard
          title="Khách hàng"
          value={statistic?.totalCustomer || "0"}
          icon={faUser}
        />
        <InfoCard
          title="Số lượng đơn hàng"
          value={statistic?.totalOrder || "0"}
          icon={faCartShopping}
        />
        <InfoCard
          title="Tổng doanh thu đã nhận"
          value={`${formatNumber(statistic?.totalPrice || 0)}đ`}
          icon={faCalculator}
        />
        <InfoCard
          title="Tổng sản phẩm đã bán"
          value={statistic?.totalProduct || "0"}
          icon={faBox}
        />
      </div>

      {/* Bảng sản phẩm bán chạy */}
      {statistic?.topOrderedProduct?.length > 0 && (
        <div className="mt-6">
          <h2 className="text-xl font-semibold mb-4">
            Sản phẩm bán chạy nhất:
          </h2>
          <table className="bg-white table-auto border-collapse border border-gray-300 w-full">
            <thead>
              <tr className="bg-white">
                <th className="border border-gray-300 px-4 py-2">Hình ảnh</th>
                <th className="border border-gray-300 px-4 py-2">
                  Tên sản phẩm
                </th>
                <th className="border border-gray-300 px-4 py-2">Giá</th>
                <th className="border border-gray-300 px-4 py-2 w-12">
                  <p className="text-nowrap">Số lượng đặt</p>
                </th>
              </tr>
            </thead>
            <tbody>
              {statistic.topOrderedProduct.map((product) => (
                <tr key={product.productId}>
                  <td className="border border-gray-300 px-4 py-2">
                    <img
                      src={product.productImage}
                      alt={product.productName}
                      className="w-16 h-16 object-cover"
                    />
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    <div>
                      <p className="text-blue-700 text-lg font-semibold hover:underline cursor-pointer">
                        {product.productName}
                      </p>
                      <p className="text-xs font-semibold">
                        SKU: {product.sku}
                      </p>
                    </div>
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {formatNumber(product.cost)}đ
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {product.totalOrdered}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
