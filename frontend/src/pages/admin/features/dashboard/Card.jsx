// eslint-disable-next-line react/prop-types
export default function Card({ color, icon, title, value }) {
  return (
    <div className={`${color} p-4 rounded-lg flex items-center`}>
      <i className={`${icon} text-2xl mr-4`}></i>
      <div>
        <div className="text-gray-600">{title}</div>
        <div className="text-xl font-bold">{value}</div>
      </div>
    </div>
  );
}
