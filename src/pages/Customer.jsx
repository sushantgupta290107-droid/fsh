import AppShell from '../components/AppShell'
import MobileNav from '../components/MobileNav'
import NotificationPanel from '../components/NotificationPanel'
import RequestCard from '../components/RequestCard'
import { SupabaseBanner } from '../components/SupabaseBanner'
import { useRequests } from '../hooks/useRequests'
import { Link } from 'react-router-dom'

export default function CustomerHome() {
  const { requests, loading } = useRequests()
  const active = requests.filter((r) => !['completed', 'cancelled'].includes(r.status))

  return (
    <AppShell>
      <SupabaseBanner />
      <div className="max-w-7xl mx-auto px-4 py-8 pb-24 md:pb-8">
        <div className="grid lg:grid-cols-[1fr_280px] gap-8">
          <div>
            <h1 className="prizz-display text-5xl md:text-6xl mb-2">POST A REQUEST</h1>
            <p className="text-stone mb-8">Customer demo · demo_customer_001</p>

            <Link to="/customer/new" className="btn-primary text-lg mb-10 inline-flex">
              New request
            </Link>

            <h2 className="font-bold uppercase tracking-widest text-sm mb-4 border-b-2 border-ink pb-2">
              My requests
            </h2>
            {loading && <p className="text-stone">Loading…</p>}
            <div className="space-y-3">
              {active.map((r) => (
                <RequestCard key={r.id} request={r} />
              ))}
              {!loading && !active.length && (
                <p className="text-stone panel-border p-6">No active requests. Post one to start live bidding.</p>
              )}
            </div>
          </div>
          <aside className="space-y-4 hidden lg:block">
            <nav className="panel-border divide-y-2 divide-ink">
              {[
                ['POST A REQUEST', '/customer/new'],
                ['MY REQUESTS', '/customer/requests'],
                ['MESSAGES', '/customer/messages'],
                ['PROFILE', '/customer/profile'],
              ].map(([label, to]) => (
                <Link key={to} to={to} className="block px-4 py-3 font-bold uppercase text-sm hover:bg-border/30">
                  {label}
                </Link>
              ))}
            </nav>
            <NotificationPanel />
          </aside>
        </div>
      </div>
      <MobileNav variant="customer" />
    </AppShell>
  )
}

export function CustomerRequests() {
  const { requests, loading } = useRequests()
  return (
    <AppShell>
      <div className="max-w-3xl mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-4xl mb-6">MY REQUESTS</h1>
        {loading && <p className="text-stone">Loading…</p>}
        <div className="space-y-3">
          {requests.map((r) => (
            <RequestCard key={r.id} request={r} />
          ))}
        </div>
      </div>
      <MobileNav variant="customer" />
    </AppShell>
  )
}

export function CustomerMessages() {
  return (
    <AppShell>
      <div className="max-w-3xl mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-4xl mb-4">MESSAGES</h1>
        <p className="text-stone">Booking chat opens from Live PRIZZ or booking screen after you accept an offer.</p>
        <NotificationPanel />
      </div>
      <MobileNav variant="customer" />
    </AppShell>
  )
}

export function CustomerProfile() {
  return (
    <AppShell>
      <div className="max-w-3xl mx-auto px-4 py-8 pb-24">
        <h1 className="prizz-display text-4xl mb-4">PROFILE</h1>
        <div className="panel-border p-6">
          <p className="font-bold uppercase">Demo customer</p>
          <p className="text-stone mt-2">demo_customer_001</p>
          <p className="text-sm text-stone mt-4">No authentication — demo identity only.</p>
        </div>
      </div>
      <MobileNav variant="customer" />
    </AppShell>
  )
}
