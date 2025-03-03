/* eslint-disable react/prop-types */
import { faCircleUser, faFileLines } from "@fortawesome/free-regular-svg-icons";
import {
  faBars,
  faCartShopping,
  faMagnifyingGlass,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useAuth } from "../../../provider/authProvider";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
// import { jwtDecode } from "jwt-decode";
import { getCategoriesTree } from "../../../services/apiCategory";
import { searchForCustomer } from "../../../services/apiProduct";
import { logout } from "../../../services/authentication";
import { formatNumber } from "../../../utils/format";
import { useUserState } from "../../../provider/UserContext";
import { getListSolutionPublic } from "../../../services/apiSolution";

function Header() {
  const { cart, userInfo } = useUserState();

  const cartItems = cart?.products || [];
  const { token, setToken } = useAuth();
  //const [userInfo, setUserInfo] = useState({});
  const [search, setSearch] = useState("");
  const [isProductMenuOpen, setIsProductMenuOpen] = useState(false);
  const [isSolutionMenuOpen, setIsSolutionMenuOpen] = useState(false);
  const [categories, setCategories] = useState([]);
  const [solutions, setSolutions] = useState([]);
  const [categorySelected, setCategorySelected] = useState();
  const [resultSearch, setResultSearch] = useState([]);
  const [isCartOpen, setIsCartOpen] = useState(false);

  // useEffect(() => {
  //   if (token) {
  //     const decode = jwtDecode(token);
  //     const { data } = decode;

  //     setUserInfo(data);
  //   }
  // }, [token]);

  useEffect(() => {
    async function fetchCategories() {
      const response = await getCategoriesTree();
      const { data } = response;
      setCategories(data?.content);
    }

    async function fetchListSolutions() {
      const response = await getListSolutionPublic();
      console.log(response);
      const { data } = response;
      setSolutions(data?.content);
    }

    fetchCategories();
    fetchListSolutions();
  }, []);

  useEffect(() => {
    if (search) {
      async function fetchDataProduct() {
        const params = {
          size: 8,
          page: 1,
          keyword: search,
        };
        const response = await searchForCustomer(params);
        const { data } = response;
        console.log(data?.content);
        setResultSearch(data?.content);
      }

      fetchDataProduct();
    } else {
      setResultSearch([]);
    }
  }, [search]);

  return (
    <header className="bg-gradient-to-r from-indigo-500 from-10% via-sky-500 via-30% to-blue-500 to-90% text-white divide-y-2 divide-stone-300">
      <div className="container mx-auto flex items-center justify-between py-4 w-4/5">
        <Link to={"/"} className="flex items-center cursor-pointer">
          <img
            src="/Logo-ShopIoT.webp"
            alt="Shop IoT"
            className="mr-2 rounded-xl h-20"
          />
          <h1 className="text-3xl font-bold">Shop IoT</h1>
        </Link>

        <div className="flex items-center justify-center md:w-1/3 relative">
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            type="text"
            placeholder="Tìm sản phẩm..."
            className="px-4 py-3 rounded-l-md flex-1 text-black font-semibold"
          />
          <Link
            onClick={() => setSearch("")}
            to={`/tim-kiem?keyword=${search}`}
            className="p-3 rounded-r-xl bg-sky-400 hover:bg-sky-700"
          >
            <FontAwesomeIcon icon={faMagnifyingGlass} size="xl" />
          </Link>

          {/* Danh sách sản phẩm kết quả tìm kiếm */}
          {resultSearch.length > 0 && (
            <div className="absolute top-full left-0 w-full bg-white text-black mt-2 shadow-lg rounded-lg z-10">
              <div className="flex flex-col p-2 space-y-1">
                {resultSearch.map((product) => (
                  <Link
                    key={product.id}
                    onClick={() => setSearch("")}
                    to={`/san-pham/${product.slug}`}
                    className="hover:bg-gray-200 p-2"
                  >
                    <FontAwesomeIcon
                      icon={faMagnifyingGlass}
                      className="mr-2"
                    />
                    {product.name}
                  </Link>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Tra cứu đơn hàng */}
        <div className="flex items-center space-x-4">
          <div className="flex items-center">
            <FontAwesomeIcon icon={faFileLines} size="xl" />
            <Link to={"/tra-cuu-don-hang"} href="#" className="ml-2 ">
              <p className="font-semibold text-base">
                Tra cứu
                <br /> đơn hàng
              </p>
            </Link>
          </div>

          {/* Đăng nhập hoặc tài khoản người dùng */}
          <div className="flex items-center gap-1">
            <FontAwesomeIcon icon={faCircleUser} size="xl" />
            {token ? (
              <div className="relative group">
                <button className="flex items-center gap-2 hover:text-blue-200 py-2 px-3 rounded-lg">
                  <span className="text-sm font-semibold">
                    {userInfo.fullName}
                  </span>
                  <svg
                    className="w-4 h-4 transition-transform group-hover:rotate-180"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M19 9l-7 7-7-7"
                    />
                  </svg>
                </button>

                <div className="absolute right-0 mt-1 w-48 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50">
                  <div className="py-2 bg-white rounded-lg shadow-xl border text-gray-800">
                    <Link
                      to="/tai-khoan/thong-tin-tai-khoan"
                      className="block px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    >
                      Thông tin tài khoản
                    </Link>
                    <Link
                      to="/tai-khoan/quan-ly-don-hang"
                      className="block px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    >
                      Đơn hàng của tôi
                    </Link>
                    <hr className="my-1" />
                    <Link
                      onClick={() => {
                        setToken();
                        logout();
                      }}
                      to={"/"}
                      className="w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100 cursor-pointer"
                    >
                      Đăng xuất
                    </Link>
                  </div>
                </div>
              </div>
            ) : (
              <Link to={"/login"}>
                <p className="font-semibold text-base">
                  Đăng nhập
                  <br className="text-xl font-semibold hidden md:block" /> Tài
                  khoản
                </p>
              </Link>
            )}
          </div>

          {/* Giỏ hàng */}
          <div
            className="relative group"
            onMouseEnter={() => setIsCartOpen(true)}
            //onMouseLeave={() => setIsCartOpen(false)}
          >
            <Link to={"/gio-hang"} className="flex items-center">
              <FontAwesomeIcon icon={faCartShopping} size="xl" />
              <span className="ml-1 bg-blue-600 rounded-full px-2">
                {cart?.cartSummary?.totalQuantity}
              </span>
            </Link>

            {isCartOpen && ( // Hiển thị giỏ hàng khi di chuột vào
              <div
                //onMouseEnter={() => setIsCartOpen(true)}
                onMouseLeave={() => setIsCartOpen(false)}
                //className="w-96 absolute right-0 mt-2 bg-white text-black shadow-lg rounded-md p-4 flex flex-col z-[51] divide-y-2"
                className="absolute right-0 mt-1 w-96  group-hover:visible invisible bg-white text-black p-2  transition-all duration-100 z-[51]"
              >
                <p className="font-semibold border-b-2 mb-1">
                  Sản phẩm mới thêm
                </p>
                <div className="divide-y-2 h-60 overflow-y-scroll pr-2 border-b-2">
                  {cartItems.map((item, index) => (
                    <div
                      key={index}
                      className="flex justify-between items-center my-1 "
                    >
                      <div className="flex gap-1">
                        <img className="w-12 h-12" src={item.image} />
                        <p className="text-xs ">{item.name}</p>
                      </div>
                      <div className="items-center">
                        <p className="text-xs font-semibold text-red-600 whitespace-nowrap">
                          {formatNumber(item.total)} ₫
                        </p>
                        <p className="text-xs text-right">x{item.quantity}</p>
                      </div>
                    </div>
                  ))}
                </div>

                <Link
                  to="/gio-hang"
                  className="block text-center bg-red-500 text-white rounded py-2 mt-2 hover:bg-red-700"
                >
                  Xem giỏ hàng
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="container mx-auto flex items-center justify-start py-1 w-4/5">
        <div className="flex-1 flex space-x-4 items-center relative">
          {/* Menu Danh mục sản phẩm */}
          <div
            onMouseEnter={() => setIsProductMenuOpen(true)}
            onMouseLeave={() => {
              setIsProductMenuOpen(false);
              setCategorySelected();
            }}
            className="hover:bg-sky-600 p-2 w-1/4 font-semibold cursor-pointer "
          >
            <p className="text-center">
              <FontAwesomeIcon className="mr-1" icon={faBars} />
              Danh mục sản phẩm
            </p>
            {isProductMenuOpen && (
              <div
                onMouseEnter={() => setIsProductMenuOpen(true)}
                onMouseLeave={() => {
                  setIsProductMenuOpen(false);
                  setCategorySelected();
                }}
                className="absolute top-full left-0 bg-white text-stone-900 w-full shadow-lg rounded-sm z-10 flex overflow-auto"
              >
                <div className="bg-white w-1/5 px-2">
                  <div className="flex flex-col p-2 space-y-2">
                    {categories.map(
                      (category, index) =>
                        category.enabled && (
                          <Link
                            key={index}
                            className="hover:bg-sky-200 hover:text-blue-500 p-2 cursor-pointer"
                            onMouseEnter={() => setCategorySelected(category)}
                            to={`/danh-muc/${category.slug}`}
                            state={category}
                          >
                            {category.name}
                          </Link>
                        )
                    )}
                  </div>
                </div>
                <div className="bg-gray-200 flex-1 grid grid-cols-3 gap-3 p-2">
                  {categorySelected?.children &&
                    categorySelected.children.map(
                      (child, index) =>
                        child.enabled && (
                          <Link
                            to={`/danh-muc/${child.slug}`}
                            state={child}
                            key={index}
                            className="text-stone-800 text-center py-2 font-normal hover:underline hover:text-blue-500 hover:italic"
                          >
                            {child.name}
                          </Link>
                        )
                    )}
                </div>
                <img
                  src="https://placehold.co/150x400"
                  alt="Warehouse logo"
                  className="flex-initial p-2"
                />
              </div>
            )}
          </div>

          {/* Menu Giải pháp */}
          <div
            onMouseEnter={() => setIsSolutionMenuOpen(true)}
            onMouseLeave={() => setIsSolutionMenuOpen(false)}
            className="hover:bg-sky-600 p-2 w-1/4 font-semibold relative"
          >
            <p className="text-center">Giải pháp</p>

            {isSolutionMenuOpen && (
              <div className="absolute top-full left-0 bg-white text-stone-900 w-[600px] shadow-lg rounded-sm z-10">
                <div className="flex flex-col p-3 space-y-2">
                  {solutions.map((solution) => (
                    <Link
                      to={`/giai-phap/${solution.slug}`}
                      key={solution.id}
                      className="hover:bg-sky-200 hover:text-blue-500 p-2 cursor-pointer"
                    >
                      {solution.name}
                    </Link>
                  ))}

                  <Link className="hover:bg-sky-200 hover:text-blue-500 p-2 cursor-pointer">
                    Giải pháp
                  </Link>
                </div>
              </div>
            )}
          </div>
          {/* <div className="font-semibold cursor-pointer p-2 w-1/4 hover:bg-teal-400">
            <p className="text-center">Sản phẩm mới</p>
          </div> */}
        </div>
      </div>
    </header>
  );
}

export default Header;
