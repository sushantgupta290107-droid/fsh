import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'
import AppShell from '../components/AppShell'
import LiveIndicator from '../components/LiveIndicator'
import ModeSwitcher from '../components/ModeSwitcher'
import { OfferStackItem } from '../components/OfferCard'
import PriceDisplay from '../components/PriceDisplay'
import { SetupSeedHint, SupabaseBanner } from '../components/SupabaseBanner'
import { useBids } from '../hooks/useBids'
import { useRequest } from '../hooks/useRequest'
import { LANDING_DEMO_REQUEST_ID } from '../lib/demoSeed'

const heroPrices = [2800, 2450, 2200, 1950]

export default function Home() {
  const { bids: demoBids } = useBids(LANDING_DEMO_REQUEST_ID)
  const { request: demoRequest } = useRequest(LANDING_DEMO_REQUEST_ID)

  const stackPrices = demoBids.length
    ? demoBids.map((b) => Number(b.amount)).sort((a, b) => b - a)
    : heroPrices

  return (
    <AppShell compact>
      <SupabaseBanner />
      <section className="max-w-7xl mx-auto px-4 pt-8 pb-16 lg:pt-12">
        <div className="grid lg:grid-cols-[1fr_340px] gap-8 lg:gap-4 items-start">
          <div className="space-y-6">
            <LiveIndicator />
            <div>
              <h1 className="prizz-display text-[clamp(3.5rem,12vw,7rem)]">
                NEED
                <br />
                SOMETHING?
              </h1>
              <p className="prizz-display text-3xl md:text-4xl text-stone mt-4 max-w-xl">
                LET THE MARKET ANSWER.
              </p>
            </div>
            <p className="text-stone max-w-md text-lg leading-relaxed">
              Post a local requirement. Nearby providers compete with price, ETA, and service — you pick the offer.
            </p>
            <div className="flex flex-wrap gap-3 items-center">
              <Link to="/customer/new" className="btn-primary text-lg px-8">
                Post a request
              </Link>
              <Link to="/provider" className="btn-secondary">
                Offer your services
              </Link>
            </div>
            <ModeSwitcher />
            <SetupSeedHint />
          </div>

          <div className="lg:mt-16 space-y-0">
            <p className="text-xs font-bold uppercase tracking-widest mb-2 flex items-center gap-2">
              <span className="w-2 h-2 bg-prizz border border-ink" /> Incoming offers (demo visual)
            </p>
            <div className="space-y-2">
              {stackPrices.map((p, i) => (
                <OfferStackItem key={p + i} amount={p} highlight={i === stackPrices.length - 1} />
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="border-y-2 border-ink bg-ink text-paper">
        <div className="max-w-7xl mx-auto px-4 py-10 grid md:grid-cols-[200px_1fr] gap-6 items-center">
          <div>
            <p className="prizz-display text-5xl text-signal">LIVE</p>
            <p className="prizz-display text-3xl">PRIZZ</p>
          </div>
          <div className="grid sm:grid-cols-2 gap-6">
            <div>
              <p className="text-xs uppercase tracking-widest text-border mb-2">Demo activity</p>
              <h2 className="font-bold text-2xl uppercase leading-tight">
                {demoRequest?.title ?? 'Need 150 chairs tonight'}
              </h2>
              <p className="text-border mt-2">{demoRequest?.location ?? 'Rajpura · 3.4 km'}</p>
              <p className="mt-4 font-display text-4xl text-signal">
                {demoBids.length || 4} offers
              </p>
            </div>
            <div className="flex flex-col gap-2">
              {(demoBids.length ? demoBids : heroPrices.map((amount, i) => ({ amount, id: i })))
                .slice(0, 4)
                .sort((a, b) => Number(a.amount ?? a) - Number(b.amount ?? b))
                .map((b) => (
                  <div
                    key={b.id ?? b.amount}
                    className="flex justify-between items-center border-2 border-paper/30 px-4 py-2"
                  >
                    <PriceDisplay amount={Number(b.amount ?? b)} size="sm" className="text-paper" />
                  </div>
                ))}
            </div>
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 py-16 grid md:grid-cols-2 gap-10">
        <div className="panel-border p-8">
          <h2 className="prizz-display text-4xl mb-4">REQUEST</h2>
          <p className="text-stone leading-relaxed">
            Describe what you need — chairs, labour, transport, repairs. Set a budget or let providers set the price.
          </p>
        </div>
        <div className="border-l-4 border-prizz pl-6 py-4">
          <h2 className="prizz-display text-4xl mb-4">COMPETING OFFERS</h2>
          <p className="text-stone leading-relaxed">
            Watch offers arrive in real time. Compare price, distance, ETA, and ratings — then lock one provider.
          </p>
          <Link to={`/live/${LANDING_DEMO_REQUEST_ID}`} className="inline-block mt-6 font-bold uppercase underline">
            View demo live screen →
          </Link>
        </div>
      </section>

      <section className="border-t-2 border-ink bg-border/30">
        <div className="max-w-7xl mx-auto px-4 py-14 flex flex-col md:flex-row md:items-end justify-between gap-6">
          <div>
            <p className="prizz-display text-5xl">READY?</p>
            <p className="text-stone mt-2 max-w-lg">Fully local demo mode.</p>
          </div>
          <Link to="/customer/new" className="btn-primary text-xl px-10 py-4">
            Post a request
          </Link>
        </div>
      </section>
    </AppShell>
  )
}
