// import { Typography, Button } from "@material-tailwind/react";
// import { FlagIcon } from "@heroicons/react/24/solid";
import { Button, Typography } from "@mui/material";
import { useRouteError } from "react-router-dom";

export function ErrorPage() {
  const error = useRouteError();

  return (
    <div className="h-screen mx-auto grid place-items-center text-center px-8">
      <div>
        <Typography
          variant="h1"
          color="blue-gray"
          className="mt-10 !text-3xl !leading-snug md:!text-4xl"
        >
          {`Error ${error.status}`} <br /> {error.data}
        </Typography>
        <Typography className="mt-8 mb-14 text-[18px] font-normal text-gray-500 mx-auto md:max-w-sm">
          Please try refreshing the page or come back later.
        </Typography>
        <Button color="gray" className="w-full px-4 md:w-[8rem]">
          back home
        </Button>
      </div>
    </div>
  );
}

export default ErrorPage;
