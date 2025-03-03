const CardGrid = () => {
  const cards = [
    {
      title: "Smart Local",
      description: "Giải pháp toàn vẹn cho căn phòng của bạn",
      image: "./section-smart-local.jpg",
    },
    {
      title: "Smart Home",
      description: "Chiếu sáng thông minh\nLung linh nhà Việt",
      image: "./section-smart-home.jpg",
    },
    {
      title: "Smart Farming",
      description: "Hỗ trợ quang hợp\nThúc đẩy cây xanh phát triển",
      image: "./section-smart-farming.jpg",
    },
    {
      title: "Smart City",
      description: "Ánh sáng xanh\nCho Thành phố trong lành",
      image: "./section-smart-city.jpg",
    },
  ];

  return (
    <div className="p-8 bg-gray-100">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
        {cards.map((card, index) => (
          <div
            key={index}
            className="relative bg-cover bg-center rounded-lg shadow-lg h-72"
            style={{ backgroundImage: `url(${card.image})` }}
          >
            <div className="absolute inset-0 bg-gradient-to-t from-blue-900 to-transparent opacity-70 rounded-lg"></div>
            <div className="absolute bottom-0 left-0 p-6 text-white space-y-2">
              <h2 className="text-xl font-bold">{card.title}</h2>
              <p className="text-sm">{card.description}</p>
              <button className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-700">
                Khám phá ngay
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CardGrid;
