function Footer() {
  return (
    <footer className="bg-emerald-900 py-4 text-white">
      <div className="container mx-auto grid grid-cols-1 md:grid-cols-4 gap-4 w-10/12">
        <div>
          <img
            src="./Logo-ShopIoT.webp"
            alt="Shop IoT Logo"
            className="h-[80px]"
          />
          <h2>HỖ TRỢ KHÁCH HÀNG</h2>
          <p>
            Hotline: <strong>1900 xxxx - 0898109810</strong>
          </p>

          <p>
            Email:{" "}
            <a href="mailto:rangdongstore@rangdong.com.vn">
              shopiot.ptit@gmail.com.vn
            </a>
          </p>
          <p>Thời gian phục vụ: 7h30 - 17h30</p>
          {/* <img
            src="https://placehold.co/100x50"
            alt="Đã Thông Báo Bộ Công Thương"
          /> */}
        </div>
        <div>
          <h2 className="text-white font-sans font-bold">VỀ SHOP IOT</h2>
          <p>
            <a href="#" className="text-white  font-sans">
              Giới thiệu
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Khuyến mãi
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Chính sách bảo mật
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Điều khoản sử dụng
            </a>
          </p>
        </div>
        <div>
          <h2 className="font-semibold">CHĂM SÓC KHÁCH HÀNG</h2>
          <p>
            <a href="#" className="text-white  font-sans">
              Hướng dẫn đặt hàng
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Chính sách vận chuyển
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Chính sách thanh toán
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Chính sách đổi hàng
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Chính sách bảo hành
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Chính sách hủy đơn
            </a>
          </p>
          <p>
            <a href="#" className="text-white  font-sans">
              Những câu hỏi thường gặp
            </a>
          </p>
        </div>
        <div>
          <h2 className="font-semibold">PHƯƠNG THỨC THANH TOÁN</h2>
          <div className="flex space-x-2 p-3">
            <img src="./zalopay-logo.png" alt="ZaloPay" className="h-14 w-14" />
            <img src="./vnpay-logo.jpg" alt="VNPay" className="h-14 w-14" />
            <img src="./cod.png" alt="COD" className="h-14 w-14 bg-white" />
          </div>
          <h2 className="text-center font-sans">Kết nối với chúng tôi</h2>
          <div className="social-icons flex justify-center mt-2 gap-4 items-center pr-6">
            <a href="#" className="text-white  font-sans">
              <img src="./icon-fb.png" alt="facebook" className="h-14 w-14" />
            </a>
            <a href="#" className="text-white  font-sans">
              <img
                src="./icon-youtube.png"
                alt="youtube"
                className="h-14 w-14"
              />
            </a>
          </div>
          <div className="text-center "></div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
