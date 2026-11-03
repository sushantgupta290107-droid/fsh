import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import AppShell from '../components/AppShell'
import Countdown from '../components/Countdown'
import LiveIndicator from '../components/LiveIndicator'
import OfferCard from '../components/OfferCard'
import { SupabaseBanner } from '../components/SupabaseBanner'
import { useBids } from '../hooks/useBids'
import { useRequest } from '../hooks/useRequest'
import { useCountdown } from '../hooks/useCountdown'
import { acceptBid } from '../lib/api'
import { DEMO_CUSTOMER_ID } from '../lib/constants'
import { simulateNewOffer } from '../lib/demoSeed'

function Radar() {
  return (
    <div className="relative w-16 h-16 flex items-center justify-center shrink-0">
      <div className="absolute inset-0 border-2 border-prizz rounded-full animate-radar-ping"></div>
      <div className="absolute inset-2 border-2 border-prizz/50 rounded-full animate-radar-ping" style={{ animationDelay: '0.5s' }}></div>
      <div className="w-4 h-4 bg-prizz rounded-full"></div>
    </div>
  )
}

export default function LivePrizz() {
  const { requestId } = useParams()
  const { request, loading: reqLoading, error: reqError } = useRequest(requestId)
  const { bids, loading: bidsLoading } = useBids(requestId)
  const countdown = useCountdown(request?.bidding_ends_at)
  const [accepting, setAccepting] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  const locked = request && !['open', 'bidding'].includes(request.status)

  // Auto-simulator for demo purposes
  useEffect(() => {
    if (!request || !request.is_demo_seed || locked) return
    const timer = setInterval(() => {
      simulateNewOffer(request.id)
    }, 6000) // spawn a new bid every 6 seconds
    return () => clearInterval(timer)
  }, [request, locked])

  const providerCount = new Set(bids.map((b) => b.provider_id)).size

  const handleAccept = async (bid) => {
    if (!request) return
    setAccepting(true)
    setError(null)
    try {
      const booking = await acceptBid({ request, bid })
      navigate(`/booking/${booking.id}?locked=1`)
    } catch (e) {
      setError(e.message)
    } finally {
      setAccepting(false)
    }
  }

  if (reqLoading) {
    return (
      <AppShell>
        <p className="p-8 text-stone">Loading live request…</p>
      </AppShell>
    )
  }

  if (reqError || !request) {
    return (
      <AppShell>
        <p className="p-8 text-prizz">{reqError || 'Request not found'}</p>
      </AppShell>
    )
  }

  const sortedBids = [...bids].sort((a, b) => Number(a.amount) - Number(b.amount))
  const lowestBidId = sortedBids.length > 0 ? sortedBids[0].id : null

  return (
    <AppShell>
      {/* Sticky Header Section */}
      <div className="sticky top-[68px] z-30 bg-paper/95 backdrop-blur-sm border-b-2 border-ink shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <LiveIndicator />
            <h2 className="font-bold uppercase tracking-wide truncate max-w-[200px] md:max-w-none">{request.title}</h2>
          </div>
          <div className="flex items-center gap-6">
            {!locked && <Countdown value={countdown} label="Time left" />}
            {lowestBidId && (
               <div className="hidden sm:block text-right">
                 <p className="text-[10px] font-bold uppercase text-stone">Market Leader</p>
                 <p className="font-display text-2xl text-prizz leading-none">
                   ₹{Math.min(...bids.map(b => Number(b.amount))).toLocaleString('en-IN')}
                 </p>
               </div>
            )}
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-6 pb-28 md:pb-8">
        <div className="grid lg:grid-cols-[minmax(0,1fr)_380px] gap-8">
          <div>
            <div className="flex flex-wrap items-start justify-between gap-4 mb-8">
              <div>
                <h1 className="prizz-display text-5xl md:text-7xl mt-4 leading-none">
                  {request.title}
                </h1>
                <p className="text-stone mt-2 uppercase text-sm tracking-wide flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-ink inline-block"></span>
                  {request.location}
                </p>
              </div>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 mb-8">
              <div className="panel-border p-4">
                <p className="prizz-display text-4xl">{providerCount || 18}</p>
                <p className="text-xs font-bold uppercase text-stone">Providers found</p>
              </div>
              <div className="panel-border p-4 bg-signal/20">
                <p className="prizz-display text-4xl">{bids.length}</p>
                <p className="text-xs font-bold uppercase text-stone">Offers</p>
              </div>
              <div className="panel-border p-4 col-span-2 sm:col-span-1">
                <p className="text-xs font-bold uppercase text-stone">Status</p>
                <p className="font-bold uppercase mt-1">{request.status}</p>
              </div>
            </div>

            {error && <p className="text-prizz mb-4">{error}</p>}

            {locked ? (
              <div className="panel-border p-6 bg-ink text-paper">
                <p className="prizz-display text-4xl text-prizz">PRIZZ LOCKED</p>
                <p className="mt-2 text-border">This request is no longer accepting offers.</p>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="flex items-center justify-between border-b-2 border-ink pb-2">
                   <h2 className="font-bold uppercase tracking-widest text-sm flex items-center gap-2">
                     Live offer stream
                   </h2>
                   {bids.length > 0 && <span className="text-xs font-bold text-prizz uppercase animate-pulse">Scanning market...</span>}
                </div>
                {bidsLoading && <p className="text-stone">Loading offers…</p>}
                
                {!bidsLoading && !bids.length && (
                  <div className="panel-border p-8 text-stone flex flex-col items-center justify-center text-center gap-6 min-h-[200px]">
                    <Radar />
                    <div>
                      <p className="font-bold uppercase text-ink">Broadcasting to network</p>
                      <p className="text-sm mt-1">Waiting for nearby providers to review your request.</p>
                    </div>
                  </div>
                )}

                {/* Render bids with the lowest logic highlighted */}
                {bids.map((bid) => (
                  <OfferCard
                    key={bid.id}
                    bid={bid}
                    animate={bid.is_new}
                    isLeader={bid.id === lowestBidId}
                    onAccept={handleAccept}
                    accepting={accepting}
                    onChat={() => {}}
                  />
                ))}
              </div>
            )}
          </div>

          <aside className="hidden lg:block space-y-4">
            <div className="panel-border p-4">
              <p className="text-xs uppercase text-stone">Customer</p>
              <p className="font-mono text-sm">{DEMO_CUSTOMER_ID}</p>
            </div>
            {request.is_demo_seed && (
              <div className="panel-border p-4 bg-ink text-paper">
                <p className="text-xs uppercase text-prizz font-bold">Auto-Demo Active</p>
                <p className="text-xs text-border mt-1 leading-relaxed">
                  You are viewing a seeded demo record. The system is automatically injecting simulated offers every few seconds to demonstrate the live bidding experience.
                </p>
              </div>
            )}
          </aside>
        </div>

        {!locked && bids.some((b) => b.is_new) && (
          <div className="fixed bottom-20 md:bottom-4 left-4 right-4 md:left-auto md:right-8 md:max-w-sm panel-border p-4 bg-signal/30 animate-slide-in-spring shadow-2xl z-50">
            <p className="font-black uppercase flex items-center gap-2">
              <span className="w-2 h-2 bg-prizz animate-pulse inline-block"></span>
              New offer received
            </p>
            <p className="prizz-display text-3xl mt-1">
              ₹{Math.min(...bids.filter((b) => b.is_new).map((b) => Number(b.amount))).toLocaleString('en-IN')}
            </p>
          </div>
        )}
      </div>
    </AppShell>
  )
}
