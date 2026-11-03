import { dbStore } from './store'
import {
  DEMO_CUSTOMER_ID,
  DEMO_PROVIDER_ID,
  PLATFORM_FEE_RATE,
} from './constants'

function fees(total) {
  const platform_fee = Math.round(total * PLATFORM_FEE_RATE)
  const provider_amount = total - platform_fee
  return { platform_fee, provider_amount }
}

export async function fetchRequest(id) {
  const req = dbStore.getTable('requests').find(r => r.id === id)
  if (!req) throw new Error('Not found')
  return req
}

export async function fetchCustomerRequests(customerId = DEMO_CUSTOMER_ID) {
  return dbStore.getTable('requests')
    .filter(r => r.customer_id === customerId)
    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
}

export async function fetchOpenRequestsForProviders() {
  return dbStore.getTable('requests')
    .filter(r => ['open', 'bidding'].includes(r.status))
    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
}

export async function createRequest(payload) {
  const bidding_ends_at = new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString()
  return dbStore.insert('requests', {
    ...payload,
    customer_id: DEMO_CUSTOMER_ID,
    status: 'bidding',
    bidding_ends_at,
    is_demo_seed: false,
  })
}

export async function fetchBidsForRequest(requestId) {
  const bids = dbStore.getTable('bids').filter(b => b.request_id === requestId)
  const profiles = dbStore.getTable('provider_profiles')
  return bids.map(b => ({
    ...b,
    provider_profiles: profiles.find(p => p.id === b.provider_id)
  })).sort((a, b) => Number(a.amount) - Number(b.amount))
}

export async function submitBid({ requestId, amount, etaMinutes, availability, message, includedServices }) {
  const bid = dbStore.insert('bids', {
    request_id: requestId,
    provider_id: DEMO_PROVIDER_ID,
    amount,
    eta_minutes: etaMinutes,
    availability,
    message,
    included_services: includedServices ?? [],
    status: 'pending',
    is_new: true,
  })

  dbStore.insert('notifications', {
    user_id: DEMO_CUSTOMER_ID,
    title: 'NEW OFFER',
    body: `₹${amount} from provider on your request`,
  })

  return bid
}

export async function acceptBid({ request, bid }) {
  const total = Number(bid.amount)
  const { platform_fee, provider_amount } = fees(total)

  const bids = dbStore.getTable('bids')
  for (const b of bids) {
    if (b.request_id === request.id) {
      dbStore.update('bids', b.id, { status: b.id === bid.id ? 'accepted' : 'rejected' })
    }
  }

  dbStore.update('requests', request.id, {
    status: 'provider_selected',
    accepted_bid_id: bid.id,
    updated_at: new Date().toISOString(),
  })

  const booking = dbStore.insert('bookings', {
    request_id: request.id,
    bid_id: bid.id,
    customer_id: DEMO_CUSTOMER_ID,
    provider_id: bid.provider_id,
    status: 'payment_pending',
    total_amount: total,
    platform_fee,
    provider_amount,
  })

  dbStore.insert('payments', {
    booking_id: booking.id,
    amount: total,
    platform_fee,
    provider_amount,
    status: 'mock_pending',
    payment_method: 'mock_test',
  })

  return booking
}

export async function completeMockPayment(bookingId) {
  const now = new Date().toISOString()
  const payments = dbStore.getTable('payments').filter(p => p.booking_id === bookingId)
  payments.forEach(p => dbStore.update('payments', p.id, { status: 'mock_completed', paid_at: now }))
  
  dbStore.update('bookings', bookingId, { status: 'confirmed' })
  const booking = dbStore.getTable('bookings').find(b => b.id === bookingId)
  dbStore.update('requests', booking.request_id, { status: 'confirmed' })
  
  const request = dbStore.getTable('requests').find(r => r.id === booking.request_id)
  return { ...booking, requests: request }
}

export async function startJob(bookingId) {
  const now = new Date().toISOString()
  dbStore.update('bookings', bookingId, { status: 'in_progress', started_at: now, updated_at: now })
  const b = dbStore.getTable('bookings').find(b => b.id === bookingId)
  dbStore.update('requests', b.request_id, { status: 'in_progress' })
}

export async function submitProof({ bookingId, photoData, note }) {
  dbStore.insert('proof_submissions', { booking_id: bookingId, photo_data: photoData, note })
  dbStore.update('bookings', bookingId, { status: 'proof_submitted' })
  const b = dbStore.getTable('bookings').find(b => b.id === bookingId)
  dbStore.update('requests', b.request_id, { status: 'proof_submitted' })
}

export async function confirmCompletion(bookingId) {
  const now = new Date().toISOString()
  dbStore.update('bookings', bookingId, { status: 'completed', completed_at: now })
  const b = dbStore.getTable('bookings').find(b => b.id === bookingId)
  dbStore.update('requests', b.request_id, { status: 'completed' })
}

export async function reportProblem(bookingId) {
  dbStore.update('bookings', bookingId, { status: 'disputed' })
  const b = dbStore.getTable('bookings').find(b => b.id === bookingId)
  dbStore.update('requests', b.request_id, { status: 'disputed' })
}

export async function submitRating({ bookingId, stars, comment }) {
  dbStore.insert('ratings', { booking_id: bookingId, stars, comment })
}

export async function fetchBooking(id) {
  const booking = dbStore.getTable('bookings').find(b => b.id === id)
  if (!booking) throw new Error('Not found')
  
  const request = dbStore.getTable('requests').find(r => r.id === booking.request_id)
  const bid = dbStore.getTable('bids').find(b => b.id === booking.bid_id)
  const provider = dbStore.getTable('provider_profiles').find(p => p.id === bid?.provider_id)
  
  return { ...booking, requests: request, bids: { ...bid, provider_profiles: provider } }
}

export async function fetchProviderActiveBooking() {
  const bookings = dbStore.getTable('bookings')
    .filter(b => b.provider_id === DEMO_PROVIDER_ID && ['confirmed', 'in_progress', 'proof_submitted', 'payment_pending', 'provider_selected'].includes(b.status))
    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
    
  if (bookings.length === 0) return null
  const booking = bookings[0]
  const request = dbStore.getTable('requests').find(r => r.id === booking.request_id)
  return { ...booking, requests: request }
}

export async function fetchMessages(bookingId) {
  return dbStore.getTable('messages')
    .filter(m => m.booking_id === bookingId)
    .sort((a, b) => new Date(a.created_at) - new Date(b.created_at))
}

export async function sendMessage({ bookingId, senderId, body }) {
  return dbStore.insert('messages', { booking_id: bookingId, sender_id: senderId, body })
}

export async function fetchNotifications(userId) {
  return dbStore.getTable('notifications')
    .filter(n => n.user_id === userId)
    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
    .slice(0, 30)
}

export async function fetchAdminSnapshot() {
  const counts = {
    requests: dbStore.getTable('requests').length,
    bids: dbStore.getTable('bids').length,
    bookings: dbStore.getTable('bookings').length,
    payments: dbStore.getTable('payments').length,
    provider_profiles: dbStore.getTable('provider_profiles').length,
    notifications: dbStore.getTable('notifications').length,
  }
  
  const liveRequests = dbStore.getTable('requests')
    .filter(r => ['open', 'bidding'].includes(r.status))
    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
    
  const allBookings = [...dbStore.getTable('bookings')].sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
  const allPayments = [...dbStore.getTable('payments')].sort((a, b) => new Date(b.created_at) - new Date(a.created_at))
  
  const disputes = dbStore.getTable('bookings')
    .filter(b => b.status === 'disputed')
    .map(b => ({ ...b, requests: dbStore.getTable('requests').find(r => r.id === b.request_id) }))
    
  return { counts, liveRequests, allBookings, allPayments, disputes }
}

export async function markBidSeen(bidId) {
  dbStore.update('bids', bidId, { is_new: false })
}
