import { useAuth } from "../../../provider/authProvider";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";
import { useAdminState } from "../../../provider/AdminContext";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCartPlus } from "@fortawesome/free-solid-svg-icons";
import { formatDateTime } from "../../../utils/format";
import { Link } from "react-router-dom";
import { viewedNotification } from "../../../services/apiNotification";

export default function Header() {
  const {
    setCountNewOrder,
    notifications,
    hasNextNotify,
    setNotifications,
    setLoadNotify,
    setPageNumber,
  } = useAdminState();
  const [isOpen, setIsOpen] = useState(false);
  const { countNewOrder } = useAdminState();
  const { token } = useAuth();
  const [userInfo, setUserInfo] = useState({});

  useEffect(() => {
    const decode = jwtDecode(token);
    const { data } = decode;
    setUserInfo(data);
  }, [token]);

  const toggleDropdown = () => {
    setCountNewOrder(0);
    setIsOpen(!isOpen);
  };

  const markAsRead = async (id) => {
    const index = notifications.findIndex((n) => n.id === id);
    console.log(index);
    if (index) {
      setNotifications((prevNotifications) =>
        prevNotifications.map((notification) =>
          notification.id === id
            ? { ...notification, hasViewed: true } // Tạo bản sao và thay đổi trạng thái
            : notification
        )
      );
    }

    await viewedNotification(id);
  };

  return (
    <div className="flex justify-end items-center py-2 px-4 bg-gradient-to-r from-pink-300 via-purple-400 to-blue-500 rounded-lg">
      <div className="flex items-center gap-3">
        <div className="flex items-center">
          {/* thông báo đơn hàng mới */}
          <div className="relative">
            {/* Icon giỏ hàng */}
            <button className="relative text-gray-700" onClick={toggleDropdown}>
              <FontAwesomeIcon icon={faCartPlus} size="2xl" />
              {/* Badge */}

              {countNewOrder > 0 ? (
                <span className="absolute top-0 right-0 transform translate-x-2 -translate-y-2 bg-red-500 text-white text-[10px] font-bold rounded-full w-5 h-5 flex items-center justify-center shadow-md">
                  {countNewOrder}
                </span>
              ) : (
                <></>
              )}
            </button>

            {/* Dropdown thông báo */}
            {isOpen && (
              <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg z-10">
                <div className="p-4 border-b">
                  <h2 className="text-xl font-bold text-gray-700">Thông báo</h2>
                </div>
                <ul className="max-h-72 overflow-y-auto">
                  {notifications.map((notification) => (
                    <Link
                      to={`/admin/order-code/${notification.orderCode}`}
                      key={notification.id}
                      className={`p-4 flex items-start gap-3 ${
                        notification.hasViewed ? "bg-gray-100" : "bg-white"
                      } hover:bg-gray-200 cursor-pointer border-b`}
                      onClick={() => {
                        markAsRead(notification.id);
                        isOpen(false);
                      }}
                    >
                      <div className="flex-grow">
                        <p className="text-sm font-bold text-gray-800">
                          {notification.message}
                        </p>
                        <p className="text-xs text-gray-500">
                          Mã đơn hàng: {notification.orderCode}
                        </p>
                        <p className="text-xs text-gray-400 mt-1">
                          {formatDateTime(notification.timestamp)}
                        </p>
                      </div>
                      {!notification.hasViewed && (
                        <span className="bg-blue-500 text-white text-[10px] font-bold rounded-full w-3 h-3 flex items-center justify-center"></span>
                      )}
                    </Link>
                  ))}
                </ul>
                {hasNextNotify && (
                  <div
                    onClick={() => {
                      setLoadNotify(true);
                      setPageNumber((p) => p + 1);
                    }}
                    className="p-3 text-center text-sm text-blue-500 cursor-pointer hover:underline"
                  >
                    Xem thêm thông báo trước
                  </div>
                )}
              </div>
            )}
          </div>

          <img
            src="https://placehold.co/40x40"
            alt="User profile"
            className="rounded-full w-10 h-10 mr-2 ml-4"
          />
          <div>
            <div className="font-bold">{userInfo.username}</div>
            {/* <div className="text-sm text-gray-500">Admin Profile</div> */}
          </div>
        </div>
      </div>
    </div>
  );
}
