import React, { createContext, useState, useContext } from 'react';
import axiosClient from '../api/axiosClient';

const AuthContext = createContext(null);

const sanitizeUser = (u) => {
 if (!u) return null;
 const { password, ...safeUser } = u;
 return safeUser;
};

export const AuthProvider = ({ children }) => {
 const [user, setUser] = useState(() => {
 const savedUser = localStorage.getItem('user');
 if (!savedUser) return null;
 const parsed = JSON.parse(savedUser);
 const safeUser = sanitizeUser(parsed);
 if (parsed && parsed.password) {
 localStorage.setItem('user', JSON.stringify(safeUser));
 }
 return safeUser;
 });
 const [token, setToken] = useState(() => localStorage.getItem('token') || null);
 const [loading, setLoading] = useState(false);

 const login = async (username, password) => {
 setLoading(true);
 try {
 const res = await axiosClient.post('/auth/login', { username, password });
 const { token: jwt, user: userData } = res.data;
 const safeUser = sanitizeUser(userData);
 localStorage.setItem('token', jwt);
 localStorage.setItem('user', JSON.stringify(safeUser));
 setToken(jwt);
 setUser(safeUser);
 return { success: true };
 } catch (err) {
 let msg = err.response?.data?.message || 'Login failed. Please check your credentials.';
 if (err.response?.data?.errors) {
 msg = Object.values(err.response.data.errors).join(', ');
 }
 return { success: false, message: msg };
 } finally {
 setLoading(false);
 }
 };

 const register = async (userData) => {
 setLoading(true);
 try {
 await axiosClient.post('/auth/register', userData);
 return { success: true, message: 'Athlete account created successfully! Please sign in.' };
 } catch (err) {
 let msg = err.response?.data?.message || 'Registration failed.';
 if (err.response?.data?.errors) {
 msg = Object.values(err.response.data.errors).join(', ');
 }
 return { success: false, message: msg };
 } finally {
 setLoading(false);
 }
 };

 const updateGoal = async (goal) => {
 try {
 const res = await axiosClient.put(`/auth/goal?goal=${goal}`);
 const updatedUser = res.data;
 const safeUser = sanitizeUser(updatedUser);
 localStorage.setItem('user', JSON.stringify(safeUser));
 setUser(safeUser);
 return { success: true, user: safeUser };
 } catch (err) {
 console.error('Failed to update goal:', err);
 return { success: false };
 }
 };

 const updateProfile = async (profileData) => {
 try {
 const updatedUser = {
 ...user,
 weightKg: parseFloat(profileData.weightKg) || user?.weightKg || 75,
 heightCm: parseFloat(profileData.heightCm) || user?.heightCm || 175,
 fitnessGoal: profileData.fitnessGoal || user?.fitnessGoal || 'STRENGTH'
 };
 const safeUser = sanitizeUser(updatedUser);
 localStorage.setItem('user', JSON.stringify(safeUser));
 setUser(safeUser);

 try {
 await axiosClient.put(`/auth/profile`, profileData);
 } catch (e) {
 // Safe fallback for local client-side persistence
 }
 return { success: true, user: updatedUser };
 } catch (err) {
 console.error('Failed to update profile:', err);
 return { success: false };
 }
 };

 const logout = () => {
 localStorage.removeItem('token');
 localStorage.removeItem('user');
 setToken(null);
 setUser(null);
 };

 return (
 <AuthContext.Provider value={{ user, token, loading, login, register, logout, updateGoal, updateProfile, isAuthenticated: !!token }}>
 {children}
 </AuthContext.Provider>
 );
};

export const useAuth = () => useContext(AuthContext);
