import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import { styled } from "@mui/material/styles";

// eslint-disable-next-line no-unused-vars
const LoadingOverlay = styled(Box)(({ theme }) => ({
  position: "absolute",
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: "rgba(0, 0, 0, 0.1)", // Màu mờ của overlay
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  zIndex: 9999, // Đảm bảo overlay nằm trên cùng
}));

export default function CircularIndeterminate() {
  return (
    <LoadingOverlay>
      <CircularProgress />
    </LoadingOverlay>
  );
}
