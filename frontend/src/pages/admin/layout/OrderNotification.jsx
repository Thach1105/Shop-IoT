// import { useEffect, useState } from "react";
// import { Stomp } from "@stomp/stompjs";
// import SockJS from "sockjs-client";

// const OrderNotification = ({ setCount }) => {
//   const [notifications, setNotifications] = useState([]);
//   const token = localStorage.getItem("token");

//   useEffect(() => {
//     const socket = new SockJS(`http://localhost:8080/shopIoT/api/ws`);
//     const stompClient = Stomp.over(socket);

//     stompClient.connect(
//       { Authorization: `Bearer ${token}` },
//       (frame) => {
//         console.log("Connected: " + frame);
//         stompClient.subscribe("/topic/admin", (message) => {
//           if (message.body) {
//             setNotifications((prevNotifications) => [
//               ...prevNotifications,
//               message.body,
//             ]);
//             setCount((c) => c + 1);
//           }
//         });
//       },
//       (error) => {
//         console.error("Connection error: ", error);
//       }
//     );

//     return () => {
//       stompClient.disconnect();
//     };
//   }, [token, setCount]);

//   return null; // Return null to avoid unnecessary rendering
// };

// export default OrderNotification;
