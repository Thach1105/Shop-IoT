import { faGoogle } from "@fortawesome/free-brands-svg-icons/faGoogle";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { getToken, login } from "../../services/authentication";
import { Link, useNavigate } from "react-router-dom";
import oauthConfig from "../../configs/oauthConfig";
import { useAuth } from "../../provider/authProvider";
import TextField from "@mui/material/TextField";
import { jwtDecode } from "jwt-decode";

function LoginDashboard() {
  const { setToken, role } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errMsg, setErrMsg] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const accessToken = getToken();

    if (accessToken) {
      console.log(role);
      if (role === "ADMIN") navigate("/admin");
      if (role === "USER") navigate("/");
    }
  }, [navigate, role]);

  async function handleLogin(e) {
    e.preventDefault();
    const account = {
      username,
      password,
    };

    try {
      const { data } = await login(account);
      console.log(data);
      setErrMsg();
      setToken(data?.content?.token);
      const decode = jwtDecode(data?.content?.token);
      if (decode.scope === "ADMIN") navigate("/admin");
      if (decode.scope === "USER") navigate(-1);
    } catch (error) {
      if (error.status === 400) {
        console.log(error);
        const { response } = error;
        const { data } = response;
        const { code } = data;
        if (code === 11000) {
          setErrMsg("Mật khẩu không chính xác");
        } else if (code === 1003) {
          setErrMsg("Tên đăng nhập không tồn tại");
        }
      } else {
        setErrMsg("Đã xảy ra lỗi");
      }
    }
  }

  function handleLoginWithGoogle(e) {
    e.preventDefault();
    const callbackUrl = oauthConfig.redirectUri;
    const authUrl = oauthConfig.authUri;
    const googleClientId = oauthConfig.clientId;

    const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(
      callbackUrl
    )}&response_type=code&client_id=${googleClientId}&scope=openid%20email%20profile`;
    window.location.href = targetUrl;
  }

  return (
    <div className="bg-gradient-to-r from-purple-500 to-pink-500 flex items-center justify-center min-h-screen">
      <div className="flex w-full max-w-4xl mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="w-full md:w-1/2 p-8">
          <h2 className="text-3xl font-bold mb-4">ĐĂNG NHẬP</h2>
          {errMsg ? (
            <p className="text-sm text-red-600 italic mb-4">{errMsg}</p>
          ) : (
            <p className="text-sm italic mb-4">
              Nhập tên tài khoản và mật khẩu của bạn để đăng nhập
            </p>
          )}
          <form>
            <div className="mb-4">
              <TextField
                type="text"
                id="textfield-username"
                label="Tên đăng nhập"
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div className="mb-4">
              <TextField
                type="password"
                placeholder="********"
                id="textfield-password"
                label="Mật khẩu"
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <div className="flex justify-between items-center mb-4">
              <Link
                to={"/forgot-password"}
                className="text-gray-700 hover:underline"
              >
                Quên mật khẩu
              </Link>
              <Link to={"/register"} className="text-gray-700 hover:underline">
                Đăng ký
              </Link>
            </div>
            <button
              className="w-full bg-violet-500 text-white py-2 rounded-lg hover:bg-gray-800"
              onClick={handleLogin}
            >
              SIGN IN
            </button>

            <button
              className="w-full mt-4 bg-white border border-gray-300 text-gray-700 py-2 rounded-lg flex items-center justify-center hover:bg-gray-100"
              onClick={handleLoginWithGoogle}
            >
              <i className="pr-2">
                <FontAwesomeIcon icon={faGoogle} />
              </i>
              ĐĂNG NHẬP VỚI GOOGLE
            </button>
          </form>
        </div>
        <Link
          to={"/"}
          className="hidden md:block md:w-1/2 bg-cover cursor-pointer"
          style={{ backgroundImage: "url('./Logo-ShopIoT.webp')" }}
        ></Link>
      </div>
    </div>
  );
}

export default LoginDashboard;
