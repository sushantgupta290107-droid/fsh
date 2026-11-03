import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import AppShell from '../components/AppShell'
import MobileNav from '../components/MobileNav'
import StatusBadge from '../components/StatusBadge'
import { SupabaseBanner } from '../components/SupabaseBanner'
import { fetchOpenRequestsForProviders, fetchProviderActiveBooking, submitBid, fetchAdminSnapshot } from '../lib/api'
import { DEMO_PROVIDER_ID } from '../lib/constants'
import { useBids } from '../hooks/useBids'
import { useRequest } from '../hooks/useRequest'
import { simulateNewOffer } from '../lib/demoSeed'

export default function ProviderHome() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    fetchOpenRequestsForProviders()
      .then(setRequests)
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <AppShell>
      <SupabaseBanner />
      <div className="max-w-4xl mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-5xl mb-2">FIND WORK NEAR YOU.</h1>
        <p className="text-stone mb-8">{DEMO_PROVIDER_ID}</p>

        <h2 className="font-bold uppercase text-sm border-b-2 border-ink pb-2 mb-4">Open requests</h2>
        {loading && <p className="text-stone">Loading…</p>}
        <div className="space-y-0 divide-y-2 divide-ink border-2 border-ink">
          {requests.map((r) => (
            <div key={r.id} className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-paper">
              <div>
                <h3 className="font-bold uppercase text-lg">{r.title}</h3>
                <p className="text-sm text-stone mt-1">
                  {r.location} · {r.scheduled_date ?? 'Today'}
                  {r.duration_hours ? ` · ${r.duration_hours} hours` : ''}
                </p>
                {r.budget && (
                  <p className="font-display text-2xl mt-2">Budget ₹{Number(r.budget).toLocaleString('en-IN')}</p>
                )}
              </div>
              <Link to={`/provider/request/${r.id}`} className="btn-secondary shrink-0">
                View request
              </Link>
            </div>
          ))}
          {!loading && !requests.length && (
            <p className="p-6 text-stone">No open requests. Reset demo or wait for a customer post.</p>
          )}
        </div>
      </div>
      <MobileNav variant="provider" />
    </AppShell>
  )
}

export function ProviderOfferPage() {
  const { requestId } = useParams()
  const { request, loading: reqLoading } = useRequest(requestId)
  const { bids } = useBids(requestId)
  
  const [amount, setAmount] = useState('')
  const [eta, setEta] = useState('35')
  const [availability, setAvailability] = useState('Tonight')
  const [message, setMessage] = useState('Delivery + pickup included')
  const [services, setServices] = useState('Delivery, Pickup, Setup')
  const [error, setError] = useState(null)
  
  // Auto-simulator for demo purposes
  useEffect(() => {
    if (!request || !request.is_demo_seed) return
    const lockedStatus = ['payment_pending', 'confirmed', 'in_progress', 'proof_submitted', 'completed']
    if (lockedStatus.includes(request.status)) return
    const timer = setInterval(() => {
      simulateNewOffer(request.id)
    }, 8000) // spawn a competitor bid every 8 seconds
    return () => clearInterval(timer)
  }, [request])

  const sortedBids = [...bids].sort((a, b) => Number(a.amount) - Number(b.amount))
  const lowestBidId = sortedBids.length > 0 ? sortedBids[0].id : null
  const locked = request && !['open', 'bidding'].includes(request.status)

  // Find if current provider has a bid
  const myBid = bids.find(b => b.provider_id === DEMO_PROVIDER_ID)

  const send = async () => {
    setError(null)
    try {
      await submitBid({
        requestId,
        amount: Number(amount),
        etaMinutes: Number(eta),
        availability,
        message,
        includedServices: services.split(',').map((s) => s.trim()).filter(Boolean),
      })
      setAmount('') // reset amount for counter-offer
    } catch (e) {
      setError(e.message)
    }
  }

  if (reqLoading) return <AppShell><p className="p-8">Loading...</p></AppShell>

  return (
    <AppShell>
      <div className="max-w-7xl mx-auto px-4 py-8 pb-24 grid lg:grid-cols-2 gap-8">
        {/* Left side: Live Market */}
        <div className="space-y-6">
           <Link to="/provider" className="text-sm font-bold uppercase underline">
             ← Back to Open Requests
           </Link>
           <div>
             <h1 className="prizz-display text-4xl mt-4 leading-none">{request?.title}</h1>
             <p className="text-stone uppercase text-sm mt-2">{request?.location}</p>
           </div>

           {locked ? (
             <div className="panel-border p-6 bg-ink text-paper">
               <p className="prizz-display text-3xl text-prizz">MARKET CLOSED</p>
               <p className="mt-2 text-border">This request has been locked by the customer.</p>
             </div>
           ) : (
             <div className="space-y-3">
               <div className="flex items-center justify-between border-b-2 border-ink pb-2">
                 <h2 className="font-bold uppercase text-sm">Live Competitor Feed</h2>
                 <span className="text-xs font-bold text-prizz uppercase animate-pulse">Live</span>
               </div>
               
               {bids.length === 0 && <p className="text-stone text-sm">No offers yet. Be the first!</p>}
               {bids.map(b => (
                 <div key={b.id} className={`panel-border p-3 flex justify-between items-center transition-all ${b.is_new ? 'animate-slide-in-spring' : ''} ${b.id === lowestBidId ? 'border-prizz shadow-lg' : ''}`}>
                   <div>
                     <p className="font-bold uppercase text-sm flex items-center gap-2">
                       {b.provider_id === DEMO_PROVIDER_ID ? 'You (Aman Event Rentals)' : 'Competitor'}
                       {b.id === lowestBidId && <span className="text-[9px] bg-prizz text-ink px-1.5 py-0.5 uppercase tracking-widest font-black">Leader</span>}
                     </p>
                     <p className="text-xs text-stone mt-1">ETA {b.eta_minutes} min</p>
                   </div>
                   <p className="font-display text-2xl">₹{Number(b.amount).toLocaleString('en-IN')}</p>
                 </div>
               ))}
             </div>
           )}
        </div>

        {/* Right side: Bid Form */}
        <div>
          <div className="panel-border p-6 bg-signal/10 sticky top-[80px]">
             <h2 className="prizz-display text-3xl mb-4">
               {myBid ? 'UPDATE YOUR OFFER' : 'SEND OFFER'}
             </h2>
             {!locked && (
               <div className="space-y-4">
                 {[
                   ['Bid amount (₹)', amount, setAmount, 'e.g. 2100'],
                   ['ETA (minutes)', eta, setEta],
                   ['Availability', availability, setAvailability],
                   ['Message', message, setMessage],
                   ['Included services (comma-separated)', services, setServices],
                 ].map(([label, val, set, placeholder]) => (
                   <label key={label} className="block">
                     <span className="text-xs font-bold uppercase">{label}</span>
                     <input
                       className="mt-1 w-full border-2 border-ink p-2 bg-paper"
                       value={val}
                       onChange={(e) => set(e.target.value)}
                       placeholder={placeholder}
                     />
                   </label>
                 ))}
                 {error && <p className="text-prizz text-sm">{error}</p>}
                 <button type="button" className="btn-primary w-full text-lg mt-4" onClick={send} disabled={!amount}>
                   {myBid ? 'Submit Counter-Offer' : 'Submit Offer'}
                 </button>
               </div>
             )}
          </div>
        </div>
      </div>
      <MobileNav variant="provider" />
    </AppShell>
  )
}

export function ProviderActive() {
  const [booking, setBooking] = useState(null)

  useEffect(() => {
    fetchProviderActiveBooking().then(setBooking).catch(() => {})
  }, [])

  return (
    <AppShell>
      <div className="max-w-lg mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-4xl mb-6">ACTIVE JOB</h1>
        {booking ? (
          <div className="panel-border p-6">
            <h2 className="font-bold uppercase text-xl">{booking.requests?.title}</h2>
            <p className="text-stone">{booking.requests?.location}</p>
            <p className="font-display text-4xl mt-4">₹{Number(booking.total_amount).toLocaleString('en-IN')}</p>
            <StatusBadge status={booking.status} />
            <Link to={`/booking/${booking.id}`} className="btn-primary mt-6 inline-flex">
              Open job
            </Link>
          </div>
        ) : (
          <p className="text-stone">No active job. Accept flow starts when customer locks an offer.</p>
        )}
      </div>
      <MobileNav variant="provider" />
    </AppShell>
  )
}

export function ProviderEarnings() {
  const [earnings, setEarnings] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchAdminSnapshot()
      .then((data) => {
        // filter bookings for DEMO_PROVIDER_ID
        const providerBookings = data.allBookings.filter((b) => b.provider_id === DEMO_PROVIDER_ID && ['completed', 'confirmed'].includes(b.status))
        const total = providerBookings.reduce((sum, b) => sum + Number(b.provider_amount), 0)
        setEarnings(total)
      })
      .finally(() => setLoading(false))
  }, [])

  return (
    <AppShell>
      <div className="max-w-lg mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-4xl mb-4">EARNINGS</h1>
        {loading ? (
          <p className="text-stone">Loading earnings...</p>
        ) : (
          <div className="panel-border p-6 bg-signal/10">
            <p className="text-xs font-bold uppercase text-stone mb-2">Total Provider Earnings</p>
            <p className="prizz-display text-5xl">₹{earnings.toLocaleString('en-IN')}</p>
          </div>
        )}
      </div>
      <MobileNav variant="provider" />
    </AppShell>
  )
}

export function ProviderProfile() {
  return (
    <AppShell>
      <div className="max-w-lg mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-4xl mb-4">PROFILE</h1>
        <div className="panel-border p-6">
          <p className="font-bold">Aman Event Rentals</p>
          <p className="text-stone text-sm mt-2">{DEMO_PROVIDER_ID}</p>
        </div>
      </div>
      <MobileNav variant="provider" />
    </AppShell>
  )
}
