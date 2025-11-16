import { createBrowserRouter } from 'react-router-dom';
import TablesPage from './pages/TablesPage';
import Historic from './pages/Historic';
import DetailsTable from './pages/DetailsTable';
import GeralSettings from './pages/GeralSettings';
import Clientes from './pages/Clientes';
import Profile from './pages/Profile';
import Products from './pages/Products';
import Cateogries from './pages/Categories';
import ChangeAccrescimo from './pages/ChangeAccrescimo';
import Delivery from './pages/Delivery';

const router = createBrowserRouter([
    {
        path: "/",
        element: <TablesPage/>
    },
    {
        path:"/historic",
        element: <Historic/>
    }, {
        path: "/table/:id",
        element: <DetailsTable/>
    },
    {
        path: "/settings",
        element: <GeralSettings/>
    },
    {
        path: "/clientes",
        element: <Clientes/>
    }, {
        path: "/profile",
        element: <Profile/>
    }, {
        path: "/products",
        element: <Products/>
    },
    {
        path: "/categories",
        element: <Cateogries/>
    }, {
        path: "/acrescimo",
        element: <ChangeAccrescimo/>
    }, {
        path: "/delivery",
        element: <Delivery/>
    }
])

export default router;