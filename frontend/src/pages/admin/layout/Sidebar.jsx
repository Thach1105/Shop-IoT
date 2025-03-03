import {
  faGaugeHigh,
  faBox,
  faCartShopping,
  faUsers,
  faTags,
  faRightFromBracket,
  faBarsStaggered,
  faLightbulb,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../../../services/authentication";

function Sidebar() {
  const navigate = useNavigate();

  function handleLogout(e) {
    e.preventDefault();
    logout();
    navigate("/login");
  }

  return (
    <div className="bg-purple-600 text-white w-64 min-h-full p-4 rounded-2xl">
      <div className="flex items-center mb-8">
        <i className="fas fa-shopping-bag text-2xl mr-2"></i>
        <span className="text-2xl font-bold">Shop Ecommerce</span>
      </div>
      <nav className="flex flex-col gap-2">
        <Link
          className="p-2 rounded-lg hover:bg-purple-700"
          to={"/admin/dashboard"}
        >
          <span className="text-xl font-bold  hover:text-yellow-200">
            <FontAwesomeIcon icon={faGaugeHigh} /> Thống kê
          </span>
        </Link>
        {/* Products */}
        <div className="cursor-pointer p-2 rounded-lg hover:bg-purple-700">
          <details>
            <summary className="flex">
              <span className="text-xl font-bold hover:text-yellow-200">
                <FontAwesomeIcon icon={faBox} /> Sản phẩm
              </span>
            </summary>
            <ul className="bg-base-100 rounded-t-none p-2">
              <li>
                <Link to={"/admin/product/list"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Danh sách sản phẩm
                  </span>
                </Link>
              </li>
              <li>
                <Link to={"/admin/product/add"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Thêm sảm phẩm mới
                  </span>
                </Link>
              </li>
            </ul>
          </details>
        </div>
        {/* Brand */}

        <Link
          className="p-2 rounded-lg hover:bg-purple-700"
          to={"/admin/brand/list"}
        >
          <span className="text-xl font-bold  hover:text-yellow-200">
            <FontAwesomeIcon icon={faTags} /> Thương hiệu
          </span>
        </Link>
        {/* Categories */}
        <div className="cursor-pointer p-2 rounded-lg hover:bg-purple-700">
          <details>
            <summary className="flex">
              <span className="text-xl font-bold hover:text-yellow-200">
                <FontAwesomeIcon icon={faBarsStaggered} /> Danh mục
              </span>
            </summary>
            <ul className="bg-base-100 rounded-t-none p-2">
              <li>
                <Link to={"/admin/categories-list"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Danh sách danh mục
                  </span>
                </Link>
              </li>
              <li>
                <Link to={"/admin/category/create"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Thêm danh mục
                  </span>
                </Link>
              </li>
              {/* <li>
                <Link to={"/admin/category/edit/:categoryId"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Categories Edit
                  </span>
                </Link>
              </li> */}
            </ul>
          </details>
        </div>
        {/* Orders */}
        <div className="cursor-pointer p-2 rounded-lg hover:bg-purple-700">
          <details>
            <summary className="flex">
              <span className="text-xl font-bold hover:text-yellow-200">
                <FontAwesomeIcon icon={faCartShopping} /> Đơn hàng
              </span>
            </summary>
            <ul className="bg-base-100 rounded-t-none p-2">
              <li>
                <Link to={"/admin/order-list"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Danh sách đơn hàng
                  </span>
                </Link>
              </li>
              {/* <li>
                <Link to={"/admin/order-code/:orderCode"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Order Details
                  </span>
                </Link>
              </li> */}
            </ul>
          </details>
        </div>
        {/* Customers */}
        <div className="cursor-pointer p-2 rounded-lg hover:bg-purple-700">
          <details>
            <summary className="flex">
              <span className="text-xl font-bold hover:text-yellow-200">
                <FontAwesomeIcon icon={faUsers} /> Người dùng
              </span>
            </summary>
            <ul className="bg-base-100 rounded-t-none p-2">
              <li>
                <Link to={"/admin/customer-list"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Danh sách người dùng
                  </span>
                </Link>
              </li>
              <li>
                <Link to={"/admin/customer-details/:customerId"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Thông tin người dùng
                  </span>
                </Link>
              </li>
            </ul>
          </details>
        </div>
        {/* Solutions */}
        <div className="cursor-pointer p-2 rounded-lg hover:bg-purple-700">
          <details>
            <summary className="flex">
              <span className="text-xl font-bold hover:text-yellow-200">
                <FontAwesomeIcon icon={faLightbulb} /> Giải pháp
              </span>
            </summary>
            <ul className="bg-base-100 rounded-t-none p-2">
              <li>
                <Link to={"/admin/solution/add"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Thêm giải pháp
                  </span>
                </Link>
              </li>
              <li>
                <Link to={"/admin/solution"}>
                  <span className="hover:text-yellow-200 font-semibold text-lg">
                    Danh sách giải pháp
                  </span>
                </Link>
              </li>
            </ul>
          </details>
        </div>
        <div
          className="cursor-pointer p-2 rounded-lg hover:bg-purple-700 hover:text-yellow-200"
          onClick={handleLogout}
        >
          <span className="text-xl font-bold">
            <FontAwesomeIcon icon={faRightFromBracket} rotation={180} /> Đăng
            xuất
          </span>
        </div>
      </nav>
    </div>
  );
}

export default Sidebar;
