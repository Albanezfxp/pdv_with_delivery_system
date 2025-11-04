import { createBrowserRouter } from 'react-router-dom';
import TablesPage from './pages/TablesPage';
import Historic from './pages/Historic';
import DetailsTable from './pages/DetailsTable';

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
    }
])

export default router;