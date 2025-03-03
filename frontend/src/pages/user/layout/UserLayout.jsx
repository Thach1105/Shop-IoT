import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";
import { UserProvider } from "../../../provider/UserContext";
// import { useEffect, useState } from "react";
// import { getMyCart } from "../../../services/apiCart";
// import { useAuth } from "../../../provider/authProvider";
// import { CartProvider } from "../../../provider/CartProvider";

function UserLayout() {
  // const { token } = useAuth();
  // const [cart, setCart] = useState([]);

  // useEffect(() => {
  //   if (token) {
  //     async function fetchMyCart() {
  //       try {
  //         const response = await getMyCart();
  //         const { data } = response;
  //         const { content } = data;
  //         setCart(content);
  //       } catch {
  //         return [];
  //       }
  //     }
  //     fetchMyCart();
  //   }
  // }, [token]);

  return (
    <UserProvider>
      <div className="flex flex-col min-h-screen w-full">
        <div>
          <Header />
        </div>
        <div className="w-full bg-gray-100">
          <div className="w-5/6 flex-1 m-auto py-8 bg-gray-100">
            <Outlet />
          </div>
        </div>
        <div>
          <Footer />
        </div>
      </div>
    </UserProvider>
  );
}

export default UserLayout;
