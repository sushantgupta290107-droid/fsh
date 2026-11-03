import { Navigate, Route, Routes } from 'react-router-dom'
import Home from './pages/Home'
import CustomerHome, {
  CustomerMessages,
  CustomerProfile,
  CustomerRequests,
} from './pages/Customer'
import RequestComposer from './pages/RequestComposer'
import LivePrizz from './pages/LivePrizz'
import ProviderHome, {
  ProviderActive,
  ProviderEarnings,
  ProviderOfferPage,
  ProviderProfile,
} from './pages/Provider'
import BookingPage from './pages/Booking'
import Admin from './pages/Admin'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/customer" element={<CustomerHome />} />
      <Route path="/customer/new" element={<RequestComposer />} />
      <Route path="/customer/requests" element={<CustomerRequests />} />
      <Route path="/customer/messages" element={<CustomerMessages />} />
      <Route path="/customer/profile" element={<CustomerProfile />} />
      <Route path="/live/:requestId" element={<LivePrizz />} />
      <Route path="/provider" element={<ProviderHome />} />
      <Route path="/provider/request/:requestId" element={<ProviderOfferPage />} />
      <Route path="/provider/active" element={<ProviderActive />} />
      <Route path="/provider/earnings" element={<ProviderEarnings />} />
      <Route path="/provider/profile" element={<ProviderProfile />} />
      <Route path="/booking/:bookingId" element={<BookingPage />} />
      <Route path="/admin" element={<Admin />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
