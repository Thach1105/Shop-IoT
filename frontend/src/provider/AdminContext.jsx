import { createContext, useContext, useState, useEffect } from "react";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuth } from "./authProvider";
import { getNotificationForAdmin } from "../services/apiNotification";

const AdminContext = createContext();

export const useAdminState = () => useContext(AdminContext);

// eslint-disable-next-line react/prop-types
export const AdminProvider = ({ children }) => {
  const { token } = useAuth();

  // Khởi tạo state từ sessionStorage nếu có, nếu không thì dùng giá trị mặc định
  const [notifications, setNotifications] = useState(() => {
    const cachedNotifications = sessionStorage.getItem("notifications");
    return cachedNotifications ? JSON.parse(cachedNotifications) : [];
  });
  const [countNewOrder, setCountNewOrder] = useState(0);
  const [hasNextNotify, setHasNextNotify] = useState(() => {
    const cachedHasNextNotify = sessionStorage.getItem("hasNextNotify");
    return cachedHasNextNotify ? JSON.parse(cachedHasNextNotify) : true;
  });
  const [pageNumber, setPageNumber] = useState(() => {
    const cachedPageNumber = sessionStorage.getItem("pageNumber");
    return cachedPageNumber ? JSON.parse(cachedPageNumber) : 1;
  });
  const [loadNotify, setLoadNotify] = useState(notifications.length === 0); // Chỉ tải nếu chưa có dữ liệu trong cache
  const pageSize = 5;

  // Lưu trạng thái vào sessionStorage mỗi khi các biến thay đổi
  useEffect(() => {
    sessionStorage.setItem("notifications", JSON.stringify(notifications));
  }, [notifications]);

  useEffect(() => {
    sessionStorage.setItem("hasNextNotify", JSON.stringify(hasNextNotify));
  }, [hasNextNotify]);

  useEffect(() => {
    sessionStorage.setItem("pageNumber", JSON.stringify(pageNumber));
  }, [pageNumber]);

  // Fetch notifications
  useEffect(() => {
    if (token && loadNotify) {
      console.log("Fetching notifications...");
      async function fetchNotify() {
        try {
          const response = await getNotificationForAdmin(pageNumber, pageSize);
          const { data } = response;
          const newNotifications = data.content;

          setNotifications((prev) => [...prev, ...newNotifications]);
          setHasNextNotify(data.pageDetails.hasNext);
          setLoadNotify(false);
        } catch (error) {
          console.error("Failed to fetch notifications:", error);
        }
      }

      fetchNotify();
    }
  }, [token, pageNumber, loadNotify]);

  // Kết nối WebSocket
  useEffect(() => {
    if (!token) return;

    const socket = new SockJS(`http://localhost:8080/shopIoT/api/ws`);
    const stompClient = Stomp.over(socket);

    stompClient.connect(
      { Authorization: `Bearer ${token}` },
      (frame) => {
        console.log("Connected to WebSocket: " + frame);
        stompClient.subscribe("/topic/admin", (message) => {
          if (message.body) {
            console.log("New WebSocket message received:", message.body);
            const parsedMessage = JSON.parse(message.body);

            setNotifications((prev) => [parsedMessage, ...prev]);
            setCountNewOrder((c) => c + 1);
          }
        });
      },
      (error) => {
        console.error("WebSocket connection error:", error);
      }
    );

    return () => {
      stompClient.disconnect(() => {
        console.log("WebSocket disconnected");
      });
    };
  }, [token]);

  return (
    <AdminContext.Provider
      value={{
        notifications,
        countNewOrder,
        setCountNewOrder,
        hasNextNotify,
        setNotifications,
        setLoadNotify,
        setPageNumber,
      }}
    >
      {children}
    </AdminContext.Provider>
  );
};
