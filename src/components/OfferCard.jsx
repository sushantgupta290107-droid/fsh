import { Link } from 'react-router-dom'
import OfferBadge from './OfferBadge'
import PriceDisplay from './PriceDisplay'

export default function OfferCard({
  bid,
  onAccept,
  onChat,
  accepting,
  animate = false,
  showActions = true,
  isLeader = false,
}) {
  const provider = bid.provider_profiles
  const name = provider?.display_name ?? bid.provider_id

  return (
    <article
      className={`panel-border p-0 overflow-hidden relative transition-all duration-500 ${animate ? 'animate-slide-in-spring' : ''} ${isLeader ? 'animate-pulse-glow border-prizz shadow-xl' : ''}`}
    >
      <div className="flex border-b-2 border-ink">
        {bid.is_new && (
          <div className="bg-signal px-3 py-2 border-r-2 border-ink flex items-center">
            <OfferBadge />
          </div>
        )}
        <div className="flex-1 px-4 py-3 flex flex-wrap items-center justify-between gap-2">
          <div>
            <h3 className="font-bold text-lg uppercase leading-tight flex items-center gap-2">
              {name}
              {isLeader && <span className="text-[10px] bg-prizz text-ink px-1.5 py-0.5 ml-2 uppercase font-black tracking-widest">Market Leader</span>}
            </h3>
            <p className="text-xs text-stone mt-1">
              {provider?.verified && <span className="text-success font-bold">✓ VERIFIED · </span>}
              ETA {bid.eta_minutes ?? '—'} MIN
            </p>
          </div>
          <PriceDisplay amount={Number(bid.amount)} size="md" />
        </div>
      </div>
      <div className="px-4 py-3 flex flex-wrap gap-4 text-sm text-stone border-b border-border">
        {provider?.rating && (
          <span>
            ⭐ {provider.rating} · {provider.completed_jobs} jobs
          </span>
        )}
        {bid.message && <span className="text-ink">{bid.message}</span>}
      </div>
      {showActions && (
        <div className="flex divide-x-2 divide-ink border-t-0">
          {onChat && (
            <button type="button" onClick={onChat} className="flex-1 py-3 font-bold uppercase text-sm hover:bg-border/30">
              Chat
            </button>
          )}
          {onAccept && (
            <button
              type="button"
              disabled={accepting}
              onClick={() => onAccept(bid)}
              className="flex-1 py-3 font-bold uppercase text-sm bg-prizz text-ink hover:brightness-95 disabled:opacity-50"
            >
              Accept
            </button>
          )}
        </div>
      )}
    </article>
  )
}

export function OfferStackItem({ amount, highlight }) {
  return (
    <div
      className={`flex items-center justify-between px-4 py-3 border-2 border-ink ${
        highlight ? 'bg-signal/40' : 'bg-paper'
      }`}
    >
      <PriceDisplay amount={amount} size="sm" />
      {highlight && <OfferBadge />}
    </div>
  )
}
