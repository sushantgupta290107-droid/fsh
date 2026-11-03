import { dbStore } from './store'
import {
  DEMO_CUSTOMER_ID,
  DEMO_PROVIDER_ID,
} from './constants'

const SEED_REQUEST_ID = 'a1000000-0000-4000-8000-000000000001'
const SEED_BID_IDS = [
  'b1000000-0000-4000-8000-000000000001',
  'b1000000-0000-4000-8000-000000000002',
  'b1000000-0000-4000-8000-000000000003',
  'b1000000-0000-4000-8000-000000000004',
]

export const LANDING_DEMO_REQUEST_ID = SEED_REQUEST_ID

const providers = [
  {
    id: DEMO_PROVIDER_ID,
    display_name: 'Aman Event Rentals',
    verified: true,
    rating: 4.8,
    completed_jobs: 126,
    location: 'Rajpura',
  },
  {
    id: 'demo_provider_002',
    display_name: 'Quick Chairs Co.',
    verified: true,
    rating: 4.6,
    completed_jobs: 89,
    location: 'Rajpura',
  },
  {
    id: 'demo_provider_003',
    display_name: 'Party Hub Supplies',
    verified: false,
    rating: 4.3,
    completed_jobs: 41,
    location: 'Ambala',
  },
]

export async function clearAllDemoData() {
  dbStore.clearAll()
}

export async function seedBaseScenario() {
  dbStore.setTable('provider_profiles', providers)

  const endsAt = new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString()

  dbStore.setTable('requests', [{
    id: SEED_REQUEST_ID,
    customer_id: DEMO_CUSTOMER_ID,
    category: 'Events',
    title: 'Need 150 chairs tonight',
    description: 'Demo listing — plastic chairs with delivery and pickup for a local event.',
    quantity: 150,
    scheduled_date: new Date().toISOString().slice(0, 10),
    start_time: '18:00',
    duration_hours: 6,
    location: 'Rajpura · 3.4 km',
    budget: 2500,
    let_providers_set_price: false,
    urgent: true,
    status: 'bidding',
    bidding_ends_at: endsAt,
    is_demo_seed: true,
    created_at: new Date().toISOString()
  }])

  const bids = [
    {
      id: SEED_BID_IDS[0],
      request_id: SEED_REQUEST_ID,
      provider_id: DEMO_PROVIDER_ID,
      amount: 2800,
      eta_minutes: 45,
      availability: 'Tonight',
      message: 'Delivery + pickup included',
      included_services: ['Delivery', 'Pickup', 'Setup'],
      status: 'pending',
      is_new: false,
      created_at: new Date(Date.now() - 10000).toISOString()
    },
    {
      id: SEED_BID_IDS[1],
      request_id: SEED_REQUEST_ID,
      provider_id: 'demo_provider_002',
      amount: 2550,
      eta_minutes: 38,
      status: 'pending',
      is_new: false,
      created_at: new Date(Date.now() - 8000).toISOString()
    },
    {
      id: SEED_BID_IDS[2],
      request_id: SEED_REQUEST_ID,
      provider_id: 'demo_provider_003',
      amount: 2300,
      eta_minutes: 52,
      status: 'pending',
      is_new: false,
      created_at: new Date(Date.now() - 5000).toISOString()
    },
    {
      id: SEED_BID_IDS[3],
      request_id: SEED_REQUEST_ID,
      provider_id: DEMO_PROVIDER_ID,
      amount: 2050,
      eta_minutes: 34,
      status: 'pending',
      is_new: false,
      created_at: new Date(Date.now() - 2000).toISOString()
    },
  ]

  dbStore.setTable('bids', bids)
}

export async function resetDemoToSeed() {
  await clearAllDemoData()
  await seedBaseScenario()
}

export async function loadLiveBiddingDemo() {
  await resetDemoToSeed()
}

export async function simulateNewOffer(requestId) {
  const existing = dbStore.getTable('bids')
    .filter(b => b.request_id === requestId)
    .sort((a, b) => a.amount - b.amount)

  const lowest = existing?.[0]?.amount ?? 2500
  const amount = Math.max(1500, Math.round(lowest - 150))

  dbStore.insert('bids', {
    request_id: requestId,
    provider_id: 'demo_provider_002',
    amount,
    eta_minutes: 28,
    availability: 'Available now',
    message: 'Demo simulated offer',
    included_services: ['Delivery'],
    status: 'pending',
    is_new: true,
  })
}

export async function clearActiveCustomerRequests() {
  const reqs = dbStore.getTable('requests').filter(r => r.customer_id === DEMO_CUSTOMER_ID && !r.is_demo_seed)
  if (!reqs?.length) return
  
  for (const r of reqs) {
    dbStore.delete('requests', r.id)
  }
}
