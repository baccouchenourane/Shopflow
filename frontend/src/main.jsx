import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import App from './App'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
      <Toaster
        position="top-right"
        toastOptions={{
          style: {
            background: '#1a1a1a',
            color: '#f0ede8',
            border: '1px solid #2a2a2a',
            fontFamily: "'DM Sans', sans-serif",
            fontSize: '14px',
          },
          success: { iconTheme: { primary: '#4caf7d', secondary: '#1a1a1a' } },
          error:   { iconTheme: { primary: '#e85d4a', secondary: '#1a1a1a' } },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>
)