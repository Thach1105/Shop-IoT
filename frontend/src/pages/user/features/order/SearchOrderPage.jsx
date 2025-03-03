import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import FormSearchOrder from "./FormSearchOrder";
import OrderInfomation from "./OrderInfomation";

function SearchOrderPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [orderCode, setOrderCode] = useState(searchParams.get("code") || "");

  const changeOrderCode = (code) => {
    setSearchParams((prevParams) => {
      const newParams = new URLSearchParams(prevParams);
      code ? newParams.set("code", code) : newParams.delete("code");
      return newParams;
    });

    setOrderCode(code);
  };

  return orderCode ? (
    <OrderInfomation />
  ) : (
    <FormSearchOrder handleSetOrderCode={changeOrderCode} />
  );
}

export default SearchOrderPage;
