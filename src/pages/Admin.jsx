import { useEffect, useState } from 'react'
import AppShell from '../components/AppShell'
import DemoControls from '../components/DemoControls'
import RequestCard from '../components/RequestCard'
import StatusBadge from '../components/StatusBadge'
import { SupabaseBanner } from '../components/SupabaseBanner'
import { fetchAdminSnapshot } from '../lib/api'
import { DEMO_ADMIN_ID } from '../lib/constants'

const sections = [
  'Overview',
  'Live Requests',
  'Providers',
  'Customers',
  'Offers',
  'Bookings',
  'Payments',
  'Disputes',
  'Categories',
  'Analytics',
]

export default function Admin() {
  const [tab, setTab] = useState('Overview')
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    fetchAdminSnapshot()
      .then(setData)
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  const c = data?.counts ?? {}

  return (
    <AppShell>
      <SupabaseBanner />
      <div className="max-w-7xl mx-auto px-4 py-8 grid lg:grid-cols-[220px_1fr] gap-8">
        <nav className="panel-border divide-y-2 divide-ink h-fit">
          {sections.map((s) => (
            <button
              key={s}
              type="button"
              onClick={() => setTab(s)}
              className={`w-full text-left px-4 py-3 text-sm font-bold uppercase ${
                tab === s ? 'bg-ink text-paper' : 'hover:bg-border/30'
              }`}
            >
              {s}
            </button>
          ))}
        </nav>

        <div className="space-y-8 min-w-0">
          <div>
            <h1 className="prizz-display text-5xl">ADMIN</h1>
            <p className="text-stone text-sm mt-2">{DEMO_ADMIN_ID} · no login</p>
          </div>

          <DemoControls onRefresh={load} />

          {loading && <p className="text-stone">Loading from database…</p>}

          {tab === 'Overview' && data && (
            <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
              {Object.entries(c).map(([k, v]) => (
                <div key={k} className="panel-border p-4">
                  <p className="text-xs uppercase text-stone">{k.replace('_', ' ')}</p>
                  <p className="prizz-display text-4xl">{v}</p>
                </div>
              ))}
            </div>
          )}

          {tab === 'Live Requests' && data && (
            <div className="space-y-3">
              {data.liveRequests.map((r) => (
                <RequestCard key={r.id} request={r} />
              ))}
            </div>
          )}

          {tab === 'Bookings' && data && (
            <div className="border-2 border-ink overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-ink text-paper uppercase text-left">
                  <tr>
                    <th className="p-3">Status</th>
                    <th className="p-3">Total</th>
                    <th className="p-3">Provider amt</th>
                  </tr>
                </thead>
                <tbody>
                  {data.allBookings.map((b) => (
                    <tr key={b.id} className="border-t border-border">
                      <td className="p-3">
                        <StatusBadge status={b.status} />
                      </td>
                      <td className="p-3 font-display text-xl">₹{Number(b.total_amount).toLocaleString('en-IN')}</td>
                      <td className="p-3">₹{Number(b.provider_amount).toLocaleString('en-IN')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {tab === 'Payments' && data && (
            <ul className="space-y-2">
              {data.allPayments.map((p) => (
                <li key={p.id} className="panel-border p-4 flex justify-between gap-4 flex-wrap">
                  <span className="font-bold uppercase">{p.status}</span>
                  <span className="font-display text-2xl">₹{Number(p.amount).toLocaleString('en-IN')}</span>
                  <span className="text-stone text-xs">{p.payment_method}</span>
                </li>
              ))}
            </ul>
          )}

          {tab === 'Disputes' && data && (
            <div className="space-y-3">
              {data.disputes.length === 0 && <p className="text-stone">No disputes.</p>}
              {data.disputes.map((b) => (
                <div key={b.id} className="panel-border p-4 border-prizz">
                  <StatusBadge status="disputed" />
                  <p className="font-bold mt-2">{b.requests?.title}</p>
                </div>
              ))}
            </div>
          )}

          {['Providers', 'Customers', 'Offers', 'Categories', 'Analytics'].includes(tab) && data && (
            <p className="text-stone panel-border p-6">
              {tab} metrics derived from live tables — see Overview counts (providers: {c.provider_profiles}, offers:{' '}
              {c.bids}, requests: {c.requests}).
            </p>
          )}
        </div>
      </div>
    </AppShell>
  )
}
