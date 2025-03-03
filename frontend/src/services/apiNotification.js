import axiosInstance from "../configs/axiosInstance";

export async function getNotificationForAdmin(number, size) {
  const response = await axiosInstance.get(
    `/notifications?number=${number}&size=${size}`
  );
  return response;
}

export async function viewedNotification(id) {
  const response = await axiosInstance.put(`/notifications/viewed/${id}`);
  return response;
}
