import { useAuth } from "../provider/authProvider";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import LoginDashboard from "../pages/login/LoginDashboard";
import Authenticate from "../components/Authenticate";
import AdminLayout from "../pages/admin/layout/AdminLayout";
import Dashboard from "../pages/admin/features/dashboard/Dashboard";
import CustomerList from "../pages/admin/features/customer/CustomerList";
import CustomerDetail from "../pages/admin/features/customer/CustomerDetail";
import OrderList from "../pages/admin/features/order/OrderList";
import OrderDetail from "../pages/admin/features/order/OrderDetail";
import CategoryList from "../pages/admin/features/category/CategoryList";
import CategoryAdd from "../pages/admin/features/category/CategoryAdd";
import CategoryEdit from "../pages/admin/features/category/CategoryEdit";
import BrandList from "../pages/admin/features/brand/BrandList";
import ProductAdd from "../pages/admin/features/product/ProductAdd";
import ProductList from "../pages/admin/features/product/ProductList";
import ProductEdit from "../pages/admin/features/product/ProductEdit";
import { getProductBySlug, getSingleProduct } from "../services/apiProduct";
import ProductDetail from "../pages/admin/features/product/ProductDetail";
import { getBrands } from "../services/apiBrand";
import ErrorPage from "../utils/ErrorPage";

import UserLayout from "../pages/user/layout/UserLayout";
import HomePage from "../pages/user/features/home/HomePage";
import CategoryPage from "../pages/user/features/category/CategoryPage";
import ProductPage from "../pages/user/features/product/ProductPage";
import CartPage from "../pages/user/features/cart/CartPage";
import SearchResultsPage from "../pages/user/features/category/SearchResultsPage";
import SearchOrderPage from "../pages/user/features/order/SearchOrderPage";
import { getMyCart } from "../services/apiCart";
import CreateOrder from "../pages/user/features/order/CreateOrder";
import AccountLayout from "../pages/user/features/account/AccountInfoLayout";
import MyOrder from "../pages/user/features/account/MyOrder";
import AccountInfomation from "../pages/user/features/account/AccountInfomation";
import PaymentNotice from "../utils/PaymentNotice";
import Register from "../pages/login/Register";
import AddSolution from "../pages/admin/features/solution/AddSolution";
import ListSolution from "../pages/admin/features/solution/ListSolution";
import {
  getListSolution,
  getSolutionById,
  getSolutionBySlug,
} from "../services/apiSolution";
import UpdateSolution from "../pages/admin/features/solution/UpdateSolution";
import ViewSolution from "../pages/user/features/solution/ViewSolution";

function Routes() {
  const { role } = useAuth();

  const routesForPublic = [
    {
      path: "/login",
      element: <LoginDashboard />,
    },
    {
      path: "/register",
      element: <Register />,
    },
  ];

  const routesForUserAuthenticatedOnly = [
    {
      path: "/",
      element: <ProtectedRoute />,
      children: [
        {
          path: "/",
          element: <UserLayout />,
          children: [
            {
              path: "/gio-hang",
              element: <CartPage />,
              loader: async () => {
                try {
                  const response = await getMyCart();
                  const { data } = response;
                  return data?.content;
                } catch (error) {
                  if (error.response?.status === 400) {
                    const { data } = error.response;
                    if (
                      data.code === 1010 &&
                      data.message === "Your cart is empty"
                    ) {
                      return null;
                    }
                  }
                  throw new Response(error.response?.data?.message, {
                    status: error.response?.status,
                  });
                }
              },
              errorElement: <ErrorPage />,
            },
            {
              path: "/dat-hang",
              element: <CreateOrder />,
            },
            {
              path: "/tai-khoan/quan-ly-don-hang",
              element: (
                <AccountLayout>
                  <MyOrder />
                </AccountLayout>
              ),
            },
            {
              path: "/tai-khoan/thong-tin-tai-khoan",
              element: (
                <AccountLayout>
                  <AccountInfomation />
                </AccountLayout>
              ),
            },
            {
              path: "/thanh-toan",
              element: <PaymentNotice />,
            },
          ],
        },
      ],
    },
  ];

  const routesForAdminAuthenticatedOnly = [
    {
      path: "/",
      element: <ProtectedRoute />,
      children: [
        {
          path: "/admin",
          element: <AdminLayout />,
          children: [
            { path: "", element: <Dashboard /> },
            {
              path: "dashboard",
              element: <Dashboard />,
            },
            {
              path: "customer-list",
              element: <CustomerList />,
            },
            {
              path: "customer-details/:customerId",
              element: <CustomerDetail />,
            },
            {
              path: "order-list",
              element: <OrderList />,
            },
            {
              path: "order-code/:orderCode",
              element: <OrderDetail />,
            },
            {
              path: "categories-list",
              element: <CategoryList />,
            },
            {
              path: "category/create",
              element: <CategoryAdd />,
            },
            {
              path: "category/edit/:categoryId",
              element: <CategoryEdit />,
            },
            {
              path: "brand/list",
              element: <BrandList />,
              loader: async () => {
                const response = await getBrands();
                const { data } = response;
                return data?.content;
              },
            },
            {
              path: "solution",
              element: <ListSolution />,
              loader: async () => {
                try {
                  const response = await getListSolution();
                  console.log(response);
                  const { data } = response;
                  return data.content;
                } catch (error) {
                  throw new Response(error.response.data?.message, {
                    status: error.response?.status,
                    //statusText: error.response.data?.message,
                  });
                }
              },
              errorElement: <ErrorPage />,
            },
            {
              path: "solution/add",
              element: <AddSolution />,
            },
            {
              path: "solution/:solutionId",
              element: <UpdateSolution />,
              loader: async ({ params }) => {
                try {
                  const response = await getSolutionById(params.solutionId);
                  console.log(response);
                  const { data } = response;
                  return data.content;
                } catch (error) {
                  throw new Response(error.response.data?.message, {
                    status: error.response?.status,
                    //statusText: error.response.data?.message,
                  });
                }
              },
              errorElement: <ErrorPage />,
            },
            {
              path: "product/add",
              element: <ProductAdd />,
            },
            {
              path: "product/list",
              element: <ProductList />,
            },
            {
              path: "product/edit/:productId",
              element: <ProductEdit />,
              loader: async ({ params }) => {
                try {
                  const response = await getSingleProduct(params.productId);
                  const { data } = response;
                  return data.content;
                } catch (error) {
                  throw new Response(error.response.data?.message, {
                    status: error.response?.status,
                    //statusText: error.response.data?.message,
                  });
                }
              },
              errorElement: <ErrorPage />,
            },
            {
              path: "product-detail/:productId",
              element: <ProductDetail />,
              loader: async ({ params }) => {
                const response = await getSingleProduct(params.productId);
                const { data } = response;
                return data.content;
              },
            },
          ],
        },
      ],
    },
  ];

  const routesForNotAuthenticatedOnly = [
    {
      path: "/",
      element: <UserLayout />,
      children: [
        {
          path: "/",
          element: <HomePage />,
        },
        {
          path: "/danh-muc/:slug",
          element: <CategoryPage />,
        },
        {
          path: "/san-pham/:slug",
          element: <ProductPage />,
          loader: async ({ params }) => {
            try {
              const response = await getProductBySlug(params.slug);
              const { data } = response;
              return data.content;
            } catch (error) {
              throw new Response(error.response.data?.message, {
                status: error.response?.status,
              });
            }
          },
          errorElement: <ErrorPage />,
        },
        {
          path: "/giai-phap/:slug",
          element: <ViewSolution />,
          loader: async ({ params }) => {
            try {
              const response = await getSolutionBySlug(params.slug);
              const { data } = response;
              return data.content;
            } catch (error) {
              throw new Response(error.response.data?.message, {
                status: error.response?.status,
                //statusText: error.response.data?.message,
              });
            }
          },
          errorElement: <ErrorPage />,
        },
        {
          path: "/tim-kiem",
          element: <SearchResultsPage />,
        },
        {
          path: "/tra-cuu-don-hang",
          element: <SearchOrderPage />,
        },
      ],
    },
    {
      path: "/authenticate-google",
      element: <Authenticate />,
    },
  ];

  const router = createBrowserRouter([
    ...routesForPublic,

    ...routesForNotAuthenticatedOnly,
    ...(role !== "ADMIN" ? routesForUserAuthenticatedOnly : []),
    ...(role !== "USER" ? routesForAdminAuthenticatedOnly : []),
  ]);

  return <RouterProvider router={router} />;
}

export default Routes;
