import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { NotifProvider } from './context/NotifContext';
import Navbar from './components/Navbar';
import Home       from './pages/Home';
import Challenges from './pages/Challenges';
import Scoreboard from './pages/Scoreboard';
import Rules      from './pages/Rules';
import About      from './pages/About';
import Tickets    from './pages/Tickets';
import Login      from './pages/Login';
import Register   from './pages/Register';
import Admin      from './pages/Admin';

// Wrapper that redirects logged-in users away from auth pages
function GuestRoute({ children }) {
  const { isLoggedIn } = useAuth();
  return isLoggedIn ? <Navigate to="/challenges" replace /> : children;
}

// Wrapper that requires login
function ProtectedRoute({ children }) {
  const { isLoggedIn } = useAuth();
  return isLoggedIn ? children : <Navigate to="/login" replace />;
}

// Wrapper that requires admin
function AdminRoute({ children }) {
  const { isAdmin } = useAuth();
  return isAdmin ? children : <Navigate to="/" replace />;
}

function AppRoutes() {
  return (
    <>
      <Navbar />
      <main>
        <Routes>
          <Route path="/"           element={<Home />} />
          <Route path="/challenges" element={<ProtectedRoute><Challenges /></ProtectedRoute>} />
          <Route path="/scoreboard" element={<Scoreboard />} />
          <Route path="/rules"      element={<Rules />} />
          <Route path="/about"      element={<About />} />
          <Route path="/tickets"    element={<ProtectedRoute><Tickets /></ProtectedRoute>} />
          <Route path="/login"      element={<GuestRoute><Login /></GuestRoute>} />
          <Route path="/register"   element={<GuestRoute><Register /></GuestRoute>} />
          <Route path="/admin"      element={<AdminRoute><Admin /></AdminRoute>} />
          <Route path="*"           element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <NotifProvider>
          <AppRoutes />
        </NotifProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
