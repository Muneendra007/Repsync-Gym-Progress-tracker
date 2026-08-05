import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import WorkoutStrategy from './pages/WorkoutStrategy';
import ExerciseLibrary from './pages/ExerciseLibrary';
import PRAnalytics from './pages/PRAnalytics';
import NutritionOptimizer from './pages/NutritionOptimizer';

function App() {
 return (
 <AuthProvider>
 <Router>
 <div className="min-h-screen bg-gym-dark text-white flex flex-col font-sans">
 <Navbar />
 <main className="flex-1">
 <Routes>
 {/* Public Routes */}
 <Route path="/login" element={<Login />} />
 <Route path="/register" element={<Register />} />

 {/* Protected Command Center Routes */}
 <Route
 path="/dashboard"
 element={
 <ProtectedRoute>
 <Dashboard />
 </ProtectedRoute>
 }
 />
 <Route
 path="/workouts/plan"
 element={
 <ProtectedRoute>
 <WorkoutStrategy />
 </ProtectedRoute>
 }
 />
 <Route
 path="/exercises"
 element={
 <ProtectedRoute>
 <ExerciseLibrary />
 </ProtectedRoute>
 }
 />
 <Route
 path="/analytics"
 element={
 <ProtectedRoute>
 <PRAnalytics />
 </ProtectedRoute>
 }
 />
 <Route
 path="/nutrition"
 element={
 <ProtectedRoute>
 <NutritionOptimizer />
 </ProtectedRoute>
 }
 />

 {/* Default redirect to Dashboard */}
 <Route path="/" element={<Navigate to="/dashboard" replace />} />
 <Route path="*" element={<Navigate to="/dashboard" replace />} />
 </Routes>
 </main>
 </div>
 </Router>
 </AuthProvider>
 );
}

export default App;
