/* eslint-disable react/prop-types */
import { Backdrop, Box, Modal, Typography } from "@mui/material";

const styleModal = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  p: 4,
};

function MyModal({ err, handleResetError }) {
  return (
    <Modal
      aria-labelledby="transition-modal-title"
      aria-describedby="transition-modal-description"
      open={err !== null && err !== undefined}
      onClose={handleResetError}
      closeAfterTransition
      slots={{ backdrop: Backdrop }}
      slotProps={{
        backdrop: {
          timeout: 500,
        },
      }}
    >
      <Box sx={styleModal}>
        <h1 className="text-xl text-red-500 underline">Thông báo:</h1>
        <Typography id="transition-modal-description" sx={{ mt: 2 }}>
          {err?.errorMessage}
        </Typography>
      </Box>
    </Modal>
  );
}

export default MyModal;
