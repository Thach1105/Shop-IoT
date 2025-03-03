import axiosInstance from "../configs/axiosInstance";

export async function getStatisticsData(startDate, endDate) {
  const from = new Date(startDate).getTime();
  const to = new Date(endDate).getTime();
  const response = await axiosInstance.get(
    `/statistics?startDate=${from}&endDate=${to}`
  );

  return response;
}
