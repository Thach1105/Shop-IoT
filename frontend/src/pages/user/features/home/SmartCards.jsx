const SmartCards = () => {
  const cards = [
    {
      title: "Smart Local",
      description: "Giải pháp toàn vẹn cho căn phòng của bạn",
      image: "link_to_image_1", // Thay bằng link hình ảnh thực tế
    },
    {
      title: "Smart Home",
      description: "Chiếu sáng thông minh Lung linh nhà Việt",
      image: "link_to_image_2", // Thay bằng link hình ảnh thực tế
    },
    {
      title: "Smart Farming",
      description: "Hỗ trợ quang hợp Thúc đẩy cây xanh phát triển",
      image: "link_to_image_3", // Thay bằng link hình ảnh thực tế
    },
    {
      title: "Smart City",
      description: "Ánh sáng xanh Cho Thành phố trong lành",
      image: "link_to_image_4", // Thay bằng link hình ảnh thực tế
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 p-4">
      {cards.map((card, index) => (
        <div
          key={index}
          className="relative overflow-hidden rounded-lg shadow-lg"
        >
          <img
            src={card.image}
            alt={card.title}
            className="w-full h-48 object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black to-transparent opacity-50"></div>
          <div className="absolute bottom-0 left-0 p-4 text-white">
            <h3 className="text-lg font-bold">{card.title}</h3>
            <p>{card.description}</p>
            <button className="mt-2 bg-blue-500 text-white px-4 py-2 rounded">
              Khám phá ngay
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};

export default SmartCards;
