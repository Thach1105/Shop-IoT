// src/pages/login/Register.jsx
import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import "tailwindcss/tailwind.css"; // Đảm bảo bạn đã cài đặt Tailwind CSS
import { registerCustomer } from "../../services/apiCustomer";

function Register() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const onSubmit = async (data) => {
    const newUser = {
      username: data.username,
      password: data.password,
      email: data.email,
      fullName: data.fullName,
    };

    try {
      await registerCustomer(newUser);
      setMessage("Tạo tài khoản thành công");
      navigate("/login", { replace: true });
    } catch {
      setMessage("");
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-r from-blue-400 to-purple-500">
      <div className="flex bg-white rounded-lg shadow-lg md:w-1/2">
        <div className="w-full p-6">
          <h2 className="text-2xl font-bold text-center mb-6">Đăng Ký</h2>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="mb-4">
              <label
                htmlFor="username"
                className="block text-sm font-medium text-gray-700"
              >
                Tên đăng nhập:
              </label>
              <input
                type="text"
                id="username"
                {...register("username", { required: true })}
                className={`mt-1 block w-full border rounded-md p-2 ${
                  errors.username ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.username && (
                <span className="text-red-500 text-sm">
                  Tên đăng nhập không được để trống
                </span>
              )}
            </div>
            <div className="mb-4">
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700"
              >
                Mật khẩu:
              </label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  {...register("password", { required: true })}
                  className={`mt-1 block w-full border rounded-md p-2 ${
                    errors.password ? "border-red-500" : "border-gray-300"
                  }`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500"
                >
                  {showPassword ? "Ẩn" : "Hiện"}
                </button>
              </div>
              {errors.password && (
                <span className="text-red-500 text-sm">
                  Mật khẩu không được để trống
                </span>
              )}
            </div>
            <div className="mb-4">
              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-gray-700"
              >
                Nhập lại mật khẩu:
              </label>
              <div className="relative">
                <input
                  type={showConfirmPassword ? "text" : "password"}
                  id="confirmPassword"
                  {...register("confirmPassword", { required: true })}
                  className={`mt-1 block w-full border rounded-md p-2 ${
                    errors.confirmPassword
                      ? "border-red-500"
                      : "border-gray-300"
                  }`}
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500"
                >
                  {showConfirmPassword ? "Ẩn" : "Hiện"}
                </button>
              </div>
              {errors.confirmPassword && (
                <span className="text-red-500 text-sm">
                  Mật khẩu xác nhận không trùng khớp
                </span>
              )}
            </div>
            <div className="mb-4">
              <label
                htmlFor="fullName"
                className="block text-sm font-medium text-gray-700"
              >
                Họ tên:
              </label>
              <input
                type="text"
                id="fullName"
                {...register("fullName", { required: true })}
                className={`mt-1 block w-full border rounded-md p-2 ${
                  errors.fullName ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.fullName && (
                <span className="text-red-500 text-sm">
                  Chưa nhập Họ và tên
                </span>
              )}
            </div>
            <div className="mb-4">
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700"
              >
                Email:
              </label>
              <input
                type="email"
                id="email"
                {...register("email", { required: true })}
                className={`mt-1 block w-full border rounded-md p-2 ${
                  errors.email ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.email && (
                <span className="text-red-500 text-sm">Chưa nhập email</span>
              )}
            </div>
            <div className="mb-3 text-right underline">
              <Link to={"/login"}>Đăng nhập</Link>
            </div>
            <button
              type="submit"
              className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700 transition duration-200"
            >
              Đăng ký
            </button>
          </form>
        </div>

        <div className="hidden lg:block">
          <Link to={"/"}>
            <img
              src="/Logo-ShopIoT.webp"
              alt="Placeholder"
              className="object-cover h-full rounded-r-lg"
            />
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Register;
