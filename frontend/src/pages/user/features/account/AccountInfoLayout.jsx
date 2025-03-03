import { faUser } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { NavLink } from "react-router-dom";
import { useUserState } from "../../../../provider/UserContext";

// eslint-disable-next-line react/prop-types
function AccountLayout({ children }) {
  const { userInfo } = useUserState();
  console.log(userInfo);
  return (
    userInfo && (
      <div className="flex min-h-screen gap-4">
        {/* Sidebar */}
        <div className=" sticky h-fit top-6">
          <div className="p-4">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center text-white">
                NT
              </div>
              <div>
                <h3 className="font-medium">{userInfo.fullName}</h3>
                <p className="text-sm text-gray-500">Tài khoản thành viên</p>
              </div>
            </div>

            <nav className="space-y-1">
              <NavLink
                to="/tai-khoan/thong-tin-tai-khoan"
                className={({ isActive }) =>
                  `flex items-center gap-2 p-3 ${
                    isActive
                      ? "bg-white text-blue-500 border-blue-700 border-l-2"
                      : "text-gray-600 hover:bg-white"
                  }`
                }
              >
                <FontAwesomeIcon icon={faUser} /> Thông tin tài khoản
              </NavLink>

              <NavLink
                to="/tai-khoan/quan-ly-don-hang"
                className={({ isActive }) =>
                  `flex items-center gap-2 p-3 ${
                    isActive
                      ? "bg-blue-50 text-blue-500 border-blue-700 border-l-2"
                      : "text-gray-600 hover:bg-white"
                  }`
                }
              >
                Quản lý đơn hàng
              </NavLink>
            </nav>
          </div>
        </div>

        {/* Main Content */}
        <div className="flex-1 bg-white">{children}</div>
      </div>
    )
  );
}

export default AccountLayout;
