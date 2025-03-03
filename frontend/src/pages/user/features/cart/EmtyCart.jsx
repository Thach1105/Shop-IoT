import { Link } from "react-router-dom";

function EmtyCart() {
  return (
    <div className="flex flex-col items-center min-h-[400px] p-8 bg-white shadow-lg">
      <div className="w-72 h-72 mb-6">
        <img
          src="./emptycart.png"
          alt="Empty Cart"
          className="w-full h-full object-contain opacity-60"
        />
      </div>

      <h2 className="text-gray-600 text-xl mb-6">
        Không có sản phẩm nào trong giỏ hàng của bạn
      </h2>

      <Link
        to={"/"}
        className="bg-blue-500 text-white px-6 py-3 rounded-xl hover:bg-blue-600 transition-colors"
      >
        TIẾP TỤC MUA SẮM
      </Link>
    </div>
  );
}

export default EmtyCart;
