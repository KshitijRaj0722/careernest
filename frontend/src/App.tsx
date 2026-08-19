import NavBar from '@/components/layout/NavBar';
import AppRoutes from '@/routes/AppRoutes';

function App() {
  return (
    <div className="min-h-screen bg-slate-50">
      <NavBar />
      <main>
        <AppRoutes />
      </main>
    </div>
  );
}

export default App;
