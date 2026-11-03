import { useEffect, useState } from 'react'
import { useParams, useSearchParams } from 'react-router-dom'
import AppShell from '../components/AppShell'
import ChatPanel from '../components/ChatPanel'
import StatusBadge from '../components/StatusBadge'
import { SupabaseBanner } from '../components/SupabaseBanner'
import { useBooking } from '../hooks/useBooking'
import { useMode } from '../context/ModeContext'
import {
  completeMockPayment,
  confirmCompletion,
  reportProblem,
  startJob,
  submitProof,
  submitRating,
} from '../lib/api'

export default function BookingPage() {
  const { bookingId } = useParams()
  const [search] = useSearchParams()
  const showLocked = search.get('locked') === '1'
  const { booking, loading, reload } = useBooking(bookingId)
  const { identity, isCustomer, isProvider } = useMode() // eslint-friendly role gates
  const [proofNote, setProofNote] = useState('')
  const [proofPhoto, setProofPhoto] = useState('')
  const [rating, setRating] = useState(5)
  const [ratingComment, setRatingComment] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState(null)
  const [paymentDone, setPaymentDone] = useState(false)

  useEffect(() => {
    if (booking?.status && booking.status !== 'payment_pending') setPaymentDone(true)
  }, [booking?.status])

  const run = async (fn) => {
    setBusy(true)
    setError(null)
    try {
      await fn()
      await reload()
    } catch (e) {
      setError(e.message)
    } finally {
      setBusy(false)
    }
  }

  if (loading || !booking) {
    return (
      <AppShell>
        <p className="p-8 text-stone">Loading booking…</p>
      </AppShell>
    )
  }

  const req = booking.requests
  const bid = booking.bids
  const providerName = bid?.provider_profiles?.display_name ?? booking.provider_id

  return (
    <AppShell>
      <SupabaseBanner />
      <div className="max-w-4xl mx-auto px-4 py-8 space-y-8">
        {(showLocked || ['payment_pending', 'confirmed', 'in_progress', 'proof_submitted', 'completed'].includes(booking.status)) && (
          <div className="panel-border p-6 bg-ink text-paper">
            <p className="prizz-display text-5xl text-prizz">PRIZZ LOCKED</p>
            <p className="mt-4 text-2xl font-bold uppercase">{providerName}</p>
            <p className="text-border mt-2">₹{Number(booking.total_amount).toLocaleString('en-IN')} · Platform fee ₹{Number(booking.platform_fee).toLocaleString('en-IN')}</p>
          </div>
        )}

        <div className="flex flex-wrap gap-4 items-center">
          <h1 className="prizz-display text-4xl flex-1">{req?.title}</h1>
          <StatusBadge status={booking.status} />
        </div>

        {error && <p className="text-prizz">{error}</p>}

        {booking.status === 'payment_pending' && isCustomer && !paymentDone && (
          <section className="panel-border p-6">
            <p className="text-xs font-bold uppercase text-stone mb-2">Mock test payment — demo only</p>
            <p className="mb-4">No real money moves. Structured for future Razorpay integration.</p>
            <button
              type="button"
              className="btn-primary"
              disabled={busy}
              onClick={() => run(() => completeMockPayment(bookingId))}
            >
              Pay ₹{Number(booking.total_amount).toLocaleString('en-IN')} (test)
            </button>
          </section>
        )}

        {isProvider && ['confirmed', 'in_progress', 'proof_submitted'].includes(booking.status) && (
          <section className="panel-border p-6 space-y-4">
            <h2 className="font-bold uppercase">Provider actions</h2>
            {booking.status === 'confirmed' && (
              <button type="button" className="btn-primary" disabled={busy} onClick={() => run(() => startJob(bookingId))}>
                Start job
              </button>
            )}
            {['confirmed', 'in_progress'].includes(booking.status) && (
              <>
                <label className="block text-sm font-bold uppercase">Completion note</label>
                <textarea
                  className="w-full border-2 border-ink p-2 bg-paper"
                  value={proofNote}
                  onChange={(e) => setProofNote(e.target.value)}
                />
                <label className="block text-sm font-bold uppercase">Photo (base64 or URL — demo)</label>
                <input
                  className="w-full border-2 border-ink p-2 bg-paper text-sm"
                  value={proofPhoto}
                  onChange={(e) => setProofPhoto(e.target.value)}
                  placeholder="Optional demo proof reference"
                />
                <button
                  type="button"
                  className="btn-secondary"
                  disabled={busy}
                  onClick={() =>
                    run(() => submitProof({ bookingId, photoData: proofPhoto, note: proofNote }))
                  }
                >
                  Upload proof
                </button>
              </>
            )}
          </section>
        )}

        {booking.status === 'proof_submitted' && isCustomer && (
          <section className="panel-border p-6">
            <h2 className="prizz-display text-4xl mb-4">IS THE JOB DONE?</h2>
            <p className="text-stone mb-4">Review provider proof (demo).</p>
            <div className="flex flex-wrap gap-3">
              <button type="button" className="btn-primary bg-success border-ink" disabled={busy} onClick={() => run(() => confirmCompletion(bookingId))}>
                Confirm completion
              </button>
              <button type="button" className="btn-secondary" disabled={busy} onClick={() => run(() => reportProblem(bookingId))}>
                Report problem
              </button>
            </div>
          </section>
        )}

        {booking.status === 'completed' && isCustomer && (
          <section className="panel-border p-6">
            <p className="inline-block bg-success text-paper font-black uppercase px-3 py-1 border-2 border-ink mb-4">
              Done
            </p>
            <h2 className="font-bold uppercase mb-4">Leave a rating</h2>
            <input
              type="range"
              min={1}
              max={5}
              value={rating}
              onChange={(e) => setRating(Number(e.target.value))}
              className="w-full"
            />
            <p className="font-display text-3xl my-2">{rating} ★</p>
            <textarea
              className="w-full border-2 border-ink p-2 bg-paper mb-4"
              placeholder="Comment (optional)"
              value={ratingComment}
              onChange={(e) => setRatingComment(e.target.value)}
            />
            <button
              type="button"
              className="btn-primary"
              disabled={busy}
              onClick={() => run(() => submitRating({ bookingId, stars: rating, comment: ratingComment }))}
            >
              Submit rating
            </button>
          </section>
        )}

        <ChatPanel bookingId={bookingId} senderId={identity} />
      </div>
    </AppShell>
  )
}
