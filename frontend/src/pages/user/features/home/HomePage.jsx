import Slide from "./Slide";
import PopularProducts from "./PopularProducts";
import CardGrid from "./CardGrid";
import ViewedProducts from "../../../../utils/ViewedProducts";

function HomePage() {
  return (
    <div>
      <nav className="bg-white shadow"></nav>
      <main>
        <Slide />
        <CardGrid />
        <PopularProducts />
        <ViewedProducts />
      </main>
    </div>
  );
}

export default HomePage;
