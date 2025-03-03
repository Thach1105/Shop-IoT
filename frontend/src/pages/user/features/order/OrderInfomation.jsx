function OrderInfomation() {
  return (
    <div className="w-full md:w-3/4 mx-auto p-6">
      <div className="border-2 border-yellow-400 p-6">
        {/* Grid layout cho 2 cột thông tin */}
        <div className="flex flex-col md:grid md:grid-cols-2 gap-6">
          {/* Cột thông tin người mua */}
          <div>
            <h2 className="text-gray-700 font-semibold text-lg mb-4">
              THÔNG TIN NGƯỜI MUA
            </h2>
            <div className="space-y-3">
              <div>
                <span className="text-gray-600">Mã hóa đơn: </span>
                <span>T89814</span>
              </div>
              <div>
                <span className="text-gray-600">Tình trạng: </span>
                <span className="text-blue-600">Đã xác nhận</span>
              </div>
              <div>
                <span className="text-gray-600">Ngày đặt: </span>
                <span>17/07/2018 11:19</span>
              </div>
              <div>
                <span className="text-gray-600">Người mua: </span>
                <span className="text-blue-600">NGUYỄN VĂN A</span>
              </div>
              <div>
                <span className="text-gray-600">Số điện thoại: </span>
                <span>0123456789</span>
              </div>
              <div>
                <span className="text-gray-600">Email: </span>
                <span>somuaraindo@gmail.com</span>
              </div>
              <div>
                <span className="text-gray-600">Địa chỉ giao hàng: </span>
                <p>69 Thạch Thị Thanh, Phường Tân Định, Quận 1, TPHCM</p>
              </div>
            </div>
            {/* Ghi chú */}
            <div className="mt-4 bg-yellow-50 p-3 text-sm italic text-yellow-800">
              Khách hàng ghi chú: Giao hàng gấp trước chiều nay (trước 17h30)
            </div>
          </div>

          {/* Cột thông tin đơn hàng */}
          <div>
            <h2 className="text-gray-700 font-semibold text-lg mb-4">
              THÔNG TIN ĐƠN HÀNG
            </h2>
            <div className="border border-gray-200">
              {/* Header của bảng */}
              <div className="grid grid-cols-4 bg-gray-50 p-2 text-sm font-medium">
                <div className="col-span-2">SẢN PHẨM</div>
                <div className="text-center">Số lượng</div>
                <div className="text-right">Thành tiền</div>
              </div>

              {/* Sản phẩm */}
              <div className="grid grid-cols-4 p-2 items-center border-t border-gray-200">
                <div className="col-span-2 flex items-center gap-4">
                  <img
                    src="/path-to-product-image.jpg"
                    alt="Sản phẩm"
                    className="w-20 h-20 object-cover"
                  />
                  <span className="text-red-500">
                    Bộ đồ mua thông thường GEM
                  </span>
                </div>
                <div className="text-center">1</div>
                <div className="text-right">520,000 đ</div>
              </div>
            </div>

            {/* Tổng tiền */}
            <div className="text-right mt-4">
              <span className="text-xl font-bold text-red-500">520,000 đ</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default OrderInfomation;
