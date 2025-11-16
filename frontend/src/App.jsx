import { RouterProvider } from "react-router-dom";
import router from "./router";
import { Toaster } from "react-hot-toast";
import { AccrescimoProvider } from "./context/AccrescimoProvider";
import "./styles/Global.css"

function App() {
  return (
    <>
      <AccrescimoProvider>
        <Toaster position="top-right" />
        <RouterProvider router={router} />
      </AccrescimoProvider>
    </>
  );
}

export default App;