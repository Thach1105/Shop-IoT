import axios from "axios";
import { getToken, setToken } from "../services/authentication";
import config from "./config";

const url = config.url_Backend;

const axiosInstance = axios.create({
  baseURL: url, // Thay thế bằng URL của bạn
});

const accessToken = getToken();

// Thêm token vào header của các yêu cầu
axiosInstance.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor xử lý khi token hết hạn
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Kiểm tra nếu lỗi là 401 và chưa refresh token trước đó
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Gửi yêu cầu lấy token mới
        const response = await axios.post(
          `${url}/auth/refresh`,
          {
            token: accessToken,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        // Lấy token mới từ phản hồi
        const newAccessToken = response.data.content.token;

        // Lưu token mới vào localStorage
        setToken(newAccessToken);

        // Thêm token mới vào header và gửi lại yêu cầu ban đầu
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;
        return axiosInstance(originalRequest);
      } catch (err) {
        // Nếu refresh token cũng hết hạn, điều hướng đến trang đăng nhập
        console.log(err);
        localStorage.removeItem("token");
        window.location.href = "/login";
      }
    }

    if (error.response.status === 403) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
