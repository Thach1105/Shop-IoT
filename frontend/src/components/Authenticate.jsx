import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Loading from "../utils/Loading";
import { jwtDecode } from "jwt-decode";
import { useAuth } from "../provider/authProvider";

export default function Authenticate() {
  const { setToken } = useAuth();
  const navigate = useNavigate();
  const [isLoggedin, setIsLoggedin] = useState(false);
  const [role, setRole] = useState();

  useEffect(() => {
    const authCodeRegex = /code=([^&]+)/;
    const isMatch = window.location.href.match(authCodeRegex);

    if (isMatch) {
      const authCode = isMatch[1];

      fetch(
        `http://localhost:8080/shopIoT/api/auth/oauth/authorization/google?code=${authCode}`,
        { method: "POST" }
      )
        .then((response) => {
          return response.json();
        })
        .then((data) => {
          localStorage.setItem("token", data.content?.token);
          const decode = jwtDecode(data.content?.token);
          setRole(decode.scope);
          setIsLoggedin(true);
        });
    }
  }, []);

  useEffect(() => {
    if (isLoggedin) {
      setToken(localStorage.getItem("token"));
      if (role === "ADMIN") {
        navigate("/admin", { replace: true });
      } else if (role === "USER") {
        navigate("/", { replace: true });
      }
    }
  }, [isLoggedin, navigate, role, setToken]);

  return (
    <div className="flex-col justify-center min-h-screen gap-8">
      <div>Authentcating...</div>
      <Loading />
    </div>
  );
}
