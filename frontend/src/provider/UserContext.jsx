import { createContext, useContext, useState, useEffect, useMemo } from "react";
import { useAuth } from "./authProvider";
import { getMyCart } from "../services/apiCart";
import { getMyInfo } from "../services/authentication";

const UserContext = createContext();

export const useUserState = () => useContext(UserContext);

// eslint-disable-next-line react/prop-types
export function UserProvider({ children }) {
  const { token } = useAuth();
  const [cart, setCart] = useState([]);
  const [userInfo, setUserInfo] = useState({});
  const [changedCart, setChangedCart] = useState(true);
  const [changedViewedProduct, setChangeViewedProduct] = useState(true);
  const [changeUserInfo, setChangedUserInfo] = useState(true);
  const [viewedProducts, setViewedProducts] = useState(
    JSON.parse(localStorage.getItem("viewedProducts")) || []
  );

  console.log(userInfo);

  useEffect(() => {
    if (token && changedCart) {
      async function fetchMyCart() {
        try {
          const response = await getMyCart();
          const { data } = response;
          const { content } = data;
          console.log(content);
          setCart(content);
          setChangedCart(false);
        } catch {
          setChangedCart(false);
          setCart([]);
        }
      }
      fetchMyCart();
    }
  }, [token, changedCart]);

  useEffect(() => {
    if (token && changeUserInfo) {
      async function fetchMyInfo() {
        try {
          const response = await getMyInfo();
          console.log(response);
          const { data } = response;
          const { content } = data;
          setUserInfo(content);
          setChangedUserInfo(false);
        } catch {
          setUserInfo({});
          setChangedUserInfo(false);
        }
      }

      fetchMyInfo();
    }
  }, [token, changeUserInfo]);

  const cartValue = useMemo(
    () => ({
      cart,
      setCart,
      setChangedCart,
      viewedProducts,
      setChangeViewedProduct,
      userInfo,
      setChangedUserInfo,
    }),
    [cart, setCart, viewedProducts, userInfo]
  );

  useEffect(() => {
    if (changedViewedProduct) {
      setViewedProducts(
        JSON.parse(localStorage.getItem("viewedProducts")) || []
      );
      setChangeViewedProduct(false);
    }
  }, [changedViewedProduct]);

  return (
    <UserContext.Provider value={cartValue}>{children}</UserContext.Provider>
  );
}
