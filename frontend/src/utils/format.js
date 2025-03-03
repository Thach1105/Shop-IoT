export function formatDateTime(dateString) {
  const date = new Date(dateString);
  const options = {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  };
  return date.toLocaleString("vi-VN", options);
}

export function formatNumber(numberString) {
  const formattedNumber = numberString.toLocaleString("vi-VN");

  return formattedNumber;
}
