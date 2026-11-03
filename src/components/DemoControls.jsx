import {
  clearActiveCustomerRequests,
  loadLiveBiddingDemo,
  resetDemoToSeed,
  simulateNewOffer,
  LANDING_DEMO_REQUEST_ID,
} from '../lib/demoSeed'

export default function DemoControls({ onRefresh }) {
  const run = async (fn) => {

    await fn()
    onRefresh?.()
  }

  return (
    <section className="panel-border p-6 bg-paper">
      <p className="text-xs font-bold uppercase tracking-widest text-prizz mb-2">Demo only</p>
      <h2 className="prizz-display text-4xl mb-4">DEMO CONTROLS</h2>
      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
        <button
          type="button"
          className="btn-primary"
          onClick={() => {
            if (window.confirm('Reset all demo data to the original seeded scenario? (DEMO ONLY)')) {
              run(resetDemoToSeed)
            }
          }}
        >
          Reset demo
        </button>
        <button type="button" className="btn-secondary" onClick={() => run(loadLiveBiddingDemo)}>
          Load live bidding demo
        </button>
        <button
          type="button"
          className="btn-secondary"
          onClick={() => run(() => simulateNewOffer(LANDING_DEMO_REQUEST_ID))}
        >
          Simulate new offer
        </button>
        <button type="button" className="btn-secondary" onClick={() => run(clearActiveCustomerRequests)}>
          Clear active request
        </button>
        <button type="button" className="btn-secondary" onClick={() => onRefresh?.()}>
          Refresh demo state
        </button>
      </div>
    </section>
  )
}
