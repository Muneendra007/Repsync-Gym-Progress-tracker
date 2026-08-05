/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        gym: {
          dark: '#0B0F19',
          card: '#131A2A',
          cardHover: '#1A2338',
          accent: '#00F0FF',
          accentHover: '#00C8D6',
          purple: '#7B2CBF',
          success: '#10B981',
          warning: '#F59E0B',
          danger: '#EF4444'
        }
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
      }
    },
  },
  plugins: [],
}
