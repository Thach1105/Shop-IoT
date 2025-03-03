import { useState } from "react";

function FormSearchOrder({ handleSetOrderCode }) {
  const [orderCode, setOrderCode] = useState("");
  return (
    <div className=" bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      {/* Container chính */}
      <div className="max-w-3xl mx-auto">
        {/* Form tra cứu */}
        <div className="bg-white p-8 rounded-lg shadow-md">
          {/* Hình ảnh minh họa */}
          <div className="mb-4">
            <img
              src="/search-order.jpg"
              alt="Tracking illustration"
              className="w-full max-w-lg mx-auto"
            />
          </div>
          <h1 className="text-3xl font-bold text-center text-gray-900 mb-8">
            Tra cứu thông tin đơn hàng
          </h1>

          <div className="space-y-6">
            <div>
              <p className="block font-medium text-gray-700 mb-2">
                Mã đơn hàng <span className="text-red-500">*</span>
              </p>
              <input
                value={orderCode}
                type="text"
                onChange={(e) => setOrderCode(e.target.value)}
                placeholder="Nhập mã đơn hàng"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            <div className="flex justify-center">
              <button
                onClick={() => handleSetOrderCode(orderCode)}
                type="submit"
                className=" w-fit bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 transition duration-200 uppercase font-semibold"
              >
                Tra cứu
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// FormSearchOrder.propTypes = {
//   setOrderCode: PropTypes.func.isRequired,
//   orderCode: PropTypes.string.isRequired,
// };

export default FormSearchOrder;
