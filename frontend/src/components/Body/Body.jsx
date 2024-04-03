import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link,
  useNavigate,
} from "react-router-dom";
import "./Body.css";
import { UserContext } from "../../model/utils/context/UserContext";
import { useContext } from "react";

import Login from "../../pages/Login/Login";
import NotFound from "../../pages/NotFound/NotFound";
import Profile from "../../pages/Profile/MonProfile";
import SeeAbs from "../../pages/Absence/GestionAbs";
import CreateAbs from "../../pages/Absence/CreerAbs";
import ModifAbs from "../../pages/Absence/ModifAbs";
import DeleteAbs from "../../pages/Absence/DeleteAbs";
import CreerAbsenseGroup from "../../pages/Absence/CreerAbsenseGroup";
import RappGlobPage from "../../pages/Rapport/RapportGlobPage";
import HistogrammePage from "../../pages/Rapport/HistogrammePage";
import ListCollabActuel from "../../pages/Rapport/ListCollabActuel";
import AdminPage from "../../pages/Administration/AdminPage";
import TraitementAbs from "../../pages/Administration/TraitementAbs";
import TraitementDemande from "../../pages/Manager/TraitementDemande";
import ValideeAbs from "../../pages/Manager/ValideeAbs";
import RejeteeAbs from "../../pages/Manager/RejeteeAbs";

function Nav() {
  const isLogged = localStorage.getItem("isLogged");
  const { logOut } = useContext(UserContext);
  // eslint-disable-next-line no-unused-vars
  const navigate = useNavigate();

  async function handleLogout() {
    await logOut();
    window.location.href = "/";
  }

  return (
    <nav className="site-navigation">
      <div className="navbar-logo">
        <img src="/logo.jpg" alt="logo" />
      </div>
      <div className="navbar-links">
        <ul>
          {!isLogged ? (
            <li>
              <Link to="/">Login</Link>
            </li>
          ) : (
            <>
              <li>
                <Link to="/profile">Profil</Link>
              </li>
              <li>
                <Link to="/absence">Absence</Link>
              </li>
              <li>
                <Link to="/rapport">Rapport</Link>
              </li>
              <li>
                <Link to="/manager/traitement">Traitement demande</Link>
              </li>
              <li>
                <Link to="/admin">Administration</Link>
              </li>
              <li>
                {/* <button onClick={handleLogout}>Logout</button> */}
                <Link to="/" onClick={handleLogout} className="red">
                  Logout
                </Link>
              </li>
            </>
          )}
        </ul>
      </div>
    </nav>
  );
}

export default function Body() {
  const isLogged = localStorage.getItem("isLogged");

  return (
    <div className="main">
      <Router>
        <Nav />
        <Routes>
          <Route path="*" element={<NotFound />} />
          {!isLogged ? (
            <>
              <Route path="/" element={<Login />} />
            </>
          ) : (
            <>
              <Route path="/profile" element={<Profile />} />
              <Route path="/absence" element={<SeeAbs />} />
              <Route path="/absence/update/:id" element={<ModifAbs />} />
              <Route path="/absence/delete/:id" element={<DeleteAbs />} />
              <Route path="/absence/create" element={<CreateAbs />} />
              <Route
                path="/absence/group/create"
                element={<CreerAbsenseGroup />}
              />
              <Route path="/rapport" element={<RappGlobPage />} />
              <Route
                path="/rapport/histogramme"
                element={<HistogrammePage />}
              />
              <Route
                path="/rapport/collab-actu"
                element={<ListCollabActuel />}
              />
              <Route
                path="/manager/traitement"
                element={<TraitementDemande />}
              />
              <Route path="/manager/:id/validee" element={<ValideeAbs />} />
              <Route path="/manager/:id/rejetee" element={<RejeteeAbs />} />

              <Route path="/admin" element={<AdminPage />} />
              <Route path="/admin/traitement-abs" element={<TraitementAbs />} />
            </>
          )}

          {/* <Route path='/admin/home' element={<AdminPage />} />
						<Route path='/admin//dish/:id' element={<DishAuthPage />} />
						<Route path="/admin/create-dish" element={<CreateDishPage />} />
						<Route path="/admin/dish/:id/update" element={<UpdateDishPage />} /> */}
        </Routes>
      </Router>
    </div>
  );
}
