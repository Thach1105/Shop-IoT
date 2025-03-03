//import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { createContext, useContext, useEffect, useMemo, useState } from "react";

const AuthContext = createContext();

// eslint-disable-next-line react/prop-types
const AuthProvider = ({ children }) => {
  // State to hold the authentication token
  const [token, setToken_] = useState(localStorage.getItem("token"));
  const [role, setRole] = useState(
    localStorage.getItem("token") &&
      jwtDecode(localStorage.getItem("token")).scope
  );

  const setToken = (newToken) => {
    setToken_(newToken);
    const decode = jwtDecode(newToken);
    setRole(decode.scope);
  };

  useEffect(() => {
    if (token) {
      //axios.defaults.headers.common["Authorization"] = "Bearer " + token;
      localStorage.setItem("token", token);
      const decode = jwtDecode(token);
      setRole(decode.scope);
    } else {
      //delete axios.defaults.headers.common["Authorization"];
      localStorage.removeItem("token");
      setRole(null);
    }
  }, [token]);

  // Memoized value of the authentication context
  const contextValue = useMemo(
    () => ({
      token,
      setToken,
      role,
    }),
    [token, role]
  );

  // Provide the authentication context to the children components
  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  return useContext(AuthContext);
};

export default AuthProvider;
