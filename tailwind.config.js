/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        paper: '#F5F1E8',
        ink: '#111111',
        prizz: '#FF3B30',
        signal: '#FFD60A',
        success: '#20C759',
        interactive: '#2457FF',
        stone: '#6F6A61',
        border: '#D8D1C5',
      },
      borderRadius: {
        card: '14px',
        btn: '12px',
        tight: '8px',
      },
      fontFamily: {
        sans: ['"DM Sans"', 'system-ui', 'sans-serif'],
        display: ['"Bebas Neue"', 'Impact', 'sans-serif'],
      },
      keyframes: {
        'slide-in': {
          '0%': { opacity: '0', transform: 'translateY(-16px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        pulseLive: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.45' },
        },
      },
      animation: {
        'slide-in': 'slide-in 0.4s ease-out',
        'pulse-live': 'pulseLive 1.4s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}
